package space.mori.dnsapi.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response DTO for Domain")
data class DomainResponseDTO(
    @Schema(description = "Domain CFID", example = "123e4567e89b12d3a456426655440000")
    val id: String,

    @Schema(description = "Domain name(TLD)", example = "example.com")
    val name: String
)