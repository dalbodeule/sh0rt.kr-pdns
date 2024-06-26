package space.mori.dnsapi

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.lang.reflect.Type


@Service
class PowerDNSAPIClient() {
    @Value("\${pdns.api.url}")
    private lateinit var apiUrl: String

    @Value("\${pdns.api.key}")
    private lateinit var apiKey: String

    private val gson = Gson()
    private val client = OkHttpClient()

    @Throws(PowerDNSAPIException::class)
    fun createZone(zoneName: String): Response {
        try {
            val body = gson.toJson(
                mapOf(
                    "name" to "$zoneName.",
                    "kind" to "Primary"
                )
            ).toRequestBody()
            val request = Request.Builder()
                .url("$apiUrl/api/v1/servers/localhost/zones")
                .addHeader("X-API-Key", apiKey)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val str = response.body?.string()
                val error = gson.fromJson(str, PowerDNSAPIError::class.java)
                throw PowerDNSAPIException(error)
            }
            return response
        } catch(ex: Exception) {
            println(ex)
            throw ex
        }
    }

    @Throws(PowerDNSAPIException::class)
    fun deleteZone(zoneName: String): Response {
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName.")
            .addHeader("X-API-Key", apiKey)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .delete()
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            val str = response.body?.string()
            val error = gson.fromJson(str, PowerDNSAPIError::class.java)
            throw PowerDNSAPIException(error)
        }
        return response
    }

    @Throws(PowerDNSAPIException::class)
    fun createRecord(zoneName: String, recordName: String, recordType: String, recordContent: String, ttl: Int = 300, priority: Int = 0): Response {
        val rrset = mapOf(
            "name" to recordName,
            "type" to recordType,
            "ttl" to ttl,
            "changetype" to "REPLACE",
            "records" to listOf(
                mapOf(
                    "content" to recordContent,
                    "disabled" to false
                )
            )
        )
        val body = gson.toJson(mapOf("rrsets" to listOf(rrset))).toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName.")
            .addHeader("X-API-Key", apiKey)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .patch(body)
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            val str = response.body?.string()
            val error = gson.fromJson(str, PowerDNSAPIError::class.java)
            throw PowerDNSAPIException(error)
        }
        return response
    }

    @Throws(PowerDNSAPIException::class)
    fun updateRecord(zoneName: String, recordName: String, recordType: String, recordContent: String, ttl: Int = 300, priority: Int = 0): Response {
        val rrset = mapOf(
            "name" to recordName,
            "type" to recordType,
            "ttl" to ttl,
            "changetype" to "REPLACE",
            "records" to listOf(
                mapOf(
                    "content" to recordContent,
                    "disabled" to false
                )
            )
        )
        val body = gson.toJson(mapOf("rrsets" to listOf(rrset))).toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName.")
            .addHeader("X-API-Key", apiKey)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .patch(body)
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            val str = response.body?.string()
            val error = gson.fromJson(str, PowerDNSAPIError::class.java)
            throw PowerDNSAPIException(error)
        }
        return response
    }

    @Throws(PowerDNSAPIException::class)
    fun deleteRecord(zoneName: String, recordName: String, recordType: String): Response {
        val rrset = mapOf(
            "name" to recordName,
            "type" to recordType,
            "changetype" to "DELETE",
            "records" to listOf<Map<String, Any>>()  // 빈 레코드 리스트
        )
        val body = gson.toJson(mapOf("rrsets" to listOf(rrset))).toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName.")
            .addHeader("X-API-Key", apiKey)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .patch(body)
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            val str = response.body?.string()
            val error = gson.fromJson(str, PowerDNSAPIError::class.java)
            throw PowerDNSAPIException(error)
        }
        return response
    }
}

@ReflectiveAccess
data class PowerDNSAPIError(
    @SerializedName("error") val error: String,
    @SerializedName("errors") val errors: List<String>?
) {
    override fun toString(): String {
        return "PowerDNSAPIError(error=$error, errors=${errors?.joinToString(", ")})"
    }
}

class PowerDNSAPIErrorDeserializer : JsonDeserializer<PowerDNSAPIError?> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext?): PowerDNSAPIError {
        val jsonObject = json.asJsonObject
        val error = jsonObject["error"].asString
        val errorsJson = jsonObject["errors"].asJsonArray
        val errors: MutableList<String> = ArrayList()
        for (element in errorsJson) {
            errors.add(element.asString)
        }
        return PowerDNSAPIError(error, errors)
    }
}

class PowerDNSAPIException(private val error: PowerDNSAPIError): RuntimeException(error.error) {
    val errors: List<String>?
        get() = error.errors

    override fun toString(): String = "PowerDNSAPIException(${error.error} ${errors?.joinToString(", ") ?: ""})"
}