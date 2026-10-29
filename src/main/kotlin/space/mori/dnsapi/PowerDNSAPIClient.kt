package space.mori.dnsapi

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


@Service
class PowerDNSAPIClient() {
    @Value("\${pdns.api.url}")
    private lateinit var apiUrl: String

    @Value("\${pdns.api.key}")
    private lateinit var apiKey: String

    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun execute(request: Request) {
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) return

            val error = runCatching {
                gson.fromJson(response.peekBody(64 * 1024L).string(), PowerDNSAPIError::class.java)
            }.getOrNull()
            throw PowerDNSAPIException(
                if (error?.error.isNullOrBlank())
                    PowerDNSAPIError("PowerDNS API returned HTTP ${response.code}", null)
                else error
            )
        }
    }

    @Throws(PowerDNSAPIException::class)
    fun createZone(zoneName: String) {
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

        execute(request)
    }

    @Throws(PowerDNSAPIException::class)
    fun deleteZone(zoneName: String) {
        val request = Request.Builder()
            .url("$apiUrl/api/v1/servers/localhost/zones/$zoneName.")
            .addHeader("X-API-Key", apiKey)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .delete()
            .build()

        execute(request)
    }

    @Throws(PowerDNSAPIException::class)
    fun createRecord(zoneName: String, recordName: String, recordType: String, recordContent: String, ttl: Int = 300, priority: Int = 0) {
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

        execute(request)
    }

    @Throws(PowerDNSAPIException::class)
    fun updateRecord(zoneName: String, recordName: String, recordType: String, recordContent: String, ttl: Int = 300, priority: Int = 0) {
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

        execute(request)
    }

    @Throws(PowerDNSAPIException::class)
    fun deleteRecord(zoneName: String, recordName: String, recordType: String) {
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

        execute(request)
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
