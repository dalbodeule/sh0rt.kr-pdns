package space.mori.dnsapi

import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils

class PowerDNSAPIClientTest {
    @Test
    fun successfulCallsUseTheExpectedHttpMethods() {
        MockWebServer().use { server ->
            server.start()
            repeat(5) { server.enqueue(MockResponse.Builder().body("ok").build()) }
            val client = clientFor(server)

            client.createZone("example.com")
            client.deleteZone("example.com")
            client.createRecord("example.com", "www.example.com.", "A", "192.0.2.1")
            client.updateRecord("example.com", "www.example.com.", "A", "192.0.2.2")
            client.deleteRecord("example.com", "www.example.com.", "A")

            assertEquals("POST", server.takeRequest().method)
            assertEquals("DELETE", server.takeRequest().method)
            repeat(3) { assertEquals("PATCH", server.takeRequest().method) }
        }
    }

    @Test
    fun errorResponsesKeepThePowerDnsMessageAndHandleInvalidJson() {
        MockWebServer().use { server ->
            server.start()
            server.enqueue(MockResponse.Builder().code(400)
                .body("{\"error\":\"invalid zone\",\"errors\":[\"bad name\"]}").build())
            server.enqueue(MockResponse.Builder().code(500).body("not json").build())
            val client = clientFor(server)

            val error = assertThrows(PowerDNSAPIException::class.java) { client.deleteZone("example.com") }
            assertEquals("invalid zone", error.message)
            assertEquals(listOf("bad name"), error.errors)

            val fallback = assertThrows(PowerDNSAPIException::class.java) { client.deleteZone("example.com") }
            assertEquals("PowerDNS API returned HTTP 500", fallback.message)
        }
    }

    private fun clientFor(server: MockWebServer): PowerDNSAPIClient = PowerDNSAPIClient().also {
        ReflectionTestUtils.setField(it, "apiUrl", server.url("/").toString().removeSuffix("/"))
        ReflectionTestUtils.setField(it, "apiKey", "test-key")
    }
}
