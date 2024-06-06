package space.mori.dnsapi

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class PowerDNSAPIClient() {
    @Value("\${pdns.api.url}")
    private lateinit var apiUrl: String

    @Value("\${pdns.api.key}")
    private lateinit var apiKey: String

    @Value("\${pdns.ns}")
    private lateinit var nameserver: String

    private val gson = Gson()
    private val client = OkHttpClient()

    fun createZone(zoneName: String): Response {
        val body = gson.toJson(mapOf(
            "name" to zoneName,
            "nameservers" to nameserver.split(","))
        ).toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones")
            .addHeader("X-API-Key", apiKey)
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) { throw RuntimeException("Unexpected code $response") }
        return response
    }

    fun deleteZone(zoneName: String): Response {
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName")
            .addHeader("X-API-Key", apiKey)
            .delete()
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) { throw RuntimeException("Unexpected code $response") }
        return response
    }

    fun createRecord(zoneName: String, recordName: String, recordType: String, recordContent: String): Response {
        val body = gson.toJson(mapOf(
            "name" to recordName,
            "type" to recordType,
            "content" to recordContent
        )).toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName/records")
            .addHeader("X-API-Key", apiKey)
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) { throw RuntimeException("Unexpected code $response") }
        return response
    }

    fun updateRecord(zoneName: String, recordName: String, recordType: String, recordContent: String): Response {
        val body = gson.toJson(mapOf(
            "content" to recordContent
        )).toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName/records/$recordName/$recordType")
            .addHeader("X-API-Key", apiKey)
            .put(body)
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) { throw RuntimeException("Unexpected code $response") }
        return response
    }

    fun deleteRecord(zoneName: String, recordName: String, recordType: String): Response {
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName/records/$recordName/$recordType")
            .addHeader("X-API-Key", apiKey)
            .delete()
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) { throw RuntimeException("Unexpected code $response") }
        return response
    }
}