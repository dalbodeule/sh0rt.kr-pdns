package space.mori.dnsapi.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Request DTO for Domain")
data class DomainRequestDTO(
    @Schema(description = "Domain name(TLD)", example = "example.com")
    val name: String
)