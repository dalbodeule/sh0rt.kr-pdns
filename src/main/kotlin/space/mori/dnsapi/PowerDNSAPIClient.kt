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

    @Throws(PowerDNSAPIException::class)
    fun createZone(zoneName: String): Response {
        val body = gson.toJson(mapOf(
            "name" to zoneName,
            "nameservers" to nameserver.split(","))
        ).toRequestBody()
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones")
            .addHeader("X-API-Key", apiKey)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            val error = gson.fromJson(response.body?.string(), PowerDNSAPIError::class.java)
            throw PowerDNSAPIException(error)
        }
        return response
    }

    @Throws(PowerDNSAPIException::class)
    fun deleteZone(zoneName: String): Response {
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName")
            .addHeader("X-API-Key", apiKey)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .delete()
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            val error = gson.fromJson(response.body?.string(), PowerDNSAPIError::class.java)
            throw PowerDNSAPIException(error)
        }
        return response
    }

    @Throws(PowerDNSAPIException::class)
    fun createRecord(zoneName: String, recordName: String, recordType: String, recordContent: String): Response {
        val body = gson.toJson(mapOf(
            "name" to recordName,
            "type" to recordType,
            "content" to recordContent
        )).toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName/records")
            .addHeader("X-API-Key", apiKey)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            val error = gson.fromJson(response.body?.string(), PowerDNSAPIError::class.java)
            throw PowerDNSAPIException(error)
        }
        return response
    }

    @Throws(PowerDNSAPIException::class)
    fun updateRecord(zoneName: String, recordName: String, recordType: String, recordContent: String): Response {
        val body = gson.toJson(mapOf(
            "content" to recordContent
        )).toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName/records/$recordName/$recordType")
            .addHeader("X-API-Key", apiKey)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .put(body)
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            val error = gson.fromJson(response.body?.string(), PowerDNSAPIError::class.java)
            throw PowerDNSAPIException(error)
        }
        return response
    }

    @Throws(PowerDNSAPIException::class)
    fun deleteRecord(zoneName: String, recordName: String, recordType: String): Response {
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName/records/$recordName/$recordType")
            .addHeader("X-API-Key", apiKey)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .delete()
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            val error = gson.fromJson(response.body?.string(), PowerDNSAPIError::class.java)
            throw PowerDNSAPIException(error)
        }
        return response
    }
}

class PowerDNSAPIError(val error: String, val errors: List<String>)
class PowerDNSAPIException(private val error: PowerDNSAPIError): RuntimeException(error.error) {
    val errors: List<String>
        get() = error.errors
}