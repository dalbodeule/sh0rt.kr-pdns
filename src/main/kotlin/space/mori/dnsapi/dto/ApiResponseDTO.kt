package space.mori.dnsapi.dto

data class ApiResponseDTO<T>(
    val success: Boolean = true,
    val errors: List<ErrorOrMessage> = listOf(),
    val messages: List<ErrorOrMessage> = listOf(),
    val result: T? = null
)

data class ErrorOrMessage(
    val code: Int,
    val message: String
)

data class DeleteResponseWithId(
    val id: String
)