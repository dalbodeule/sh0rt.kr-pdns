package space.mori.dnsapi.dto

import com.google.gson.GsonBuilder

val gson = GsonBuilder().setPrettyPrinting().create()

data class ApiResponseDTO<T>(
    val success: Boolean = true,
    val errors: List<ErrorOrMessage> = listOf(),
    val messages: List<ErrorOrMessage> = listOf(),
    val result: T? = null
) {
    override fun toString(): String = gson.toJson(this)
}

data class ErrorOrMessage(
    val code: Int,
    val message: String
)

data class DeleteResponseWithId(
    val id: String
)