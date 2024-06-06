package space.mori.dnsapi

import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import space.mori.dnsapi.dto.RecordRequestDTO

@Service
class PowerDNSApiClient {
    @Value("\${pdns.api.url}")
    private lateinit var apiUrl: String

    @Value("\${pdns.api.key}")
    private lateinit var apiKey: String

    private val restTemplate = RestTemplate()
    private val gson = Gson()

    private fun createHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.set("X-API-Key", apiKey)
        headers.contentType = MediaType.APPLICATION_JSON
        return headers
    }

    fun createDomain(name: String): ResponseEntity<String> {
        val url = "$apiUrl/api/v1/servers/localhost/zones"
        val headers = createHeaders()
        val domainRequest = DomainRequest("$name.", "Master", arrayOf(), arrayOf())
        val body = gson.toJson(domainRequest)
        val entity = HttpEntity(body, headers)
        return restTemplate.exchange(url, HttpMethod.POST, entity, String::class.java)
    }

    fun createRecord(domainName: String, recordRequest: RecordRequestDTO): ResponseEntity<String> {
        val url = "$apiUrl/api/v1/servers/localhost/zones/$domainName."
        val headers = createHeaders()
        val record = RecordRequest(
            name = "${recordRequest.name}.$domainName.",
            type = recordRequest.type,
            ttl = recordRequest.ttl,
            changetype = "REPLACE",
            records = arrayOf(RecordContent(recordRequest.content, false))
        )
        val body = gson.toJson(RecordRequestWrapper(arrayOf(record)))
        val entity = HttpEntity(body, headers)
        return restTemplate.exchange(url, HttpMethod.PATCH, entity, String::class.java)
    }

    fun deleteDomain(name: String): ResponseEntity<String> {
        val url = "$apiUrl/api/v1/servers/localhost/zones/$name."
        val headers = createHeaders()
        val entity = HttpEntity<String>(headers)
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, String::class.java)
    }

    private data class DomainRequest(
        val name: String,
        val kind: String,
        val masters: Array<String>,
        val nameservers: Array<String>
    )

    private data class RecordRequestWrapper(
        val rrsets: Array<RecordRequest>
    )

    private data class RecordRequest(
        val name: String,
        val type: String,
        val ttl: Int,
        val changetype: String,
        val records: Array<RecordContent>
    )

    private data class RecordContent(
        val content: String,
        val disabled: Boolean
    )
}