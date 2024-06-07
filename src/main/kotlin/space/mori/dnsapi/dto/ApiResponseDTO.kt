package space.mori.dnsapi.dto

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

val gson = GsonBuilder().setPrettyPrinting().create()

data class ApiResponseDTO<T>(
    @SerializedName("success")
    val success: Boolean = true,
    @SerializedName("errors")
    val errors: List<ErrorOrMessage> = listOf(),
    @SerializedName("messages")
    val messages: List<ErrorOrMessage> = listOf(),
    @SerializedName("result")
    val result: T? = null
) {
    override fun toString(): String {
        return gson.toJson(this)
    }
}

data class ErrorOrMessage(
    val code: Int,
    val message: String
)

data class DeleteResponseWithId(
    val id: String
)