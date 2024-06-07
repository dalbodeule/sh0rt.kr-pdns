package space.mori.dnsapi

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import space.mori.dnsapi.dto.ApiResponseDTO
import space.mori.dnsapi.dto.ErrorOrMessage

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(PowerDNSAPIException::class)
    fun handlePowerDNSAPIException(ex: PowerDNSAPIException): ResponseEntity<ApiResponseDTO<Nothing>> {
        var idx = 0
        val errors = mutableListOf(ErrorOrMessage(idx, ex.message ?: ""))
        ex.errors?.forEach{
            errors.add(ErrorOrMessage(idx++, it))
        }

        val response = ApiResponseDTO(
            success = false,
            errors = errors,
            result = null
        )

        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response)
    }
}