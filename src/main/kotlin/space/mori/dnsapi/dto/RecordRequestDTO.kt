package space.mori.dnsapi.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Request DTO for Record")
data class RecordRequestDTO(
    @Schema(description = "Record type", example = "A")
    val type: String,

    @Schema(description = "Host name", example = "www")
    val name: String,

    @Schema(description = "Record data", example = "192.0.2.1")
    val value: String,

    @Schema(description = "TTL (Time to Live)", example = "3600")
    var ttl: Int = 300,

    @Schema(description = "Priority", example = "0")
    val priority: Int? = null,

    @Schema(description = "Proxied: cloudflare api compatibility", example = "false")
    val proxied: Boolean = false,

    @Schema(description = "comment", example="")
    val comment: String
)
