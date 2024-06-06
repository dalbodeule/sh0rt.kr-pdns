package space.mori.dnsapi

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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

    fun createZone(zoneName: String): Boolean {
        val body = gson.toJson(mapOf(
            "name" to zoneName,
            "nameservers" to nameserver.split(","))
        ).toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        return response.isSuccessful
    }

    fun deleteZone(zoneName: String): Boolean {
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName")
            .addHeader("Authorization", "Bearer $apiKey")
            .delete()
            .build()

        val response = client.newCall(request).execute()
        return response.isSuccessful
    }

    fun createRecord(zoneName: String, recordName: String, recordType: String, recordContent: String): Boolean {
        val body = gson.toJson(mapOf(
            "name" to recordName,
            "type" to recordType,
            "content" to recordContent
        )).toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName/records")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        return response.isSuccessful
    }

    fun updateRecord(zoneName: String, recordName: String, recordType: String, recordContent: String): Boolean {
        val body = gson.toJson(mapOf(
            "content" to recordContent
        )).toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName/records/$recordName/$recordType")
            .addHeader("Authorization", "Bearer $apiKey")
            .put(body)
            .build()

        val response = client.newCall(request).execute()
        return response.isSuccessful
    }

    fun deleteRecord(zoneName: String, recordName: String, recordType: String): Boolean {
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName/records/$recordName/$recordType")
            .addHeader("Authorization", "Bearer $apiKey")
            .delete()
            .build()

        val response = client.newCall(request).execute()
        return response.isSuccessful
    }
}