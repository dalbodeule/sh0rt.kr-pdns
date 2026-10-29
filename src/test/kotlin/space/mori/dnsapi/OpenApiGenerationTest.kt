package space.mori.dnsapi

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.nio.file.Files
import java.nio.file.Path

@Tag("openapi-generation")
@SpringBootTest(properties = ["springdoc.api-docs.enabled=true"])
@ActiveProfiles("test")
class OpenApiGenerationTest(@Autowired private val context: WebApplicationContext) {
    @Test
    fun generateOpenApi() {
        val response = MockMvcBuilders.webAppContextSetup(context).build()
            .perform(get("/api-docs").with { request ->
                request.serverName = "localhost"
                request.serverPort = 8080
                request
            })
            .andReturn().response
        check(response.status == 200) { "OpenAPI endpoint returned HTTP ${response.status}: ${response.contentAsString}" }

        val output = Path.of(System.getProperty("openapi.output"))
        val json = GsonBuilder().setPrettyPrinting().create()
            .toJson(JsonParser.parseString(response.contentAsString))
        Files.writeString(output, json + "\n")
    }
}
