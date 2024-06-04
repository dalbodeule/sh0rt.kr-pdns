package space.mori.dnsapi.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Request DTO for Record")
data class RecordRequestDTO(
    @Schema(description = "Host name", example = "www")
    val host: String,

    @Schema(description = "Record type", example = "A")
    val type: String,

    @Schema(description = "Record data", example = "192.0.2.1")
    val data: String,

    @Schema(description = "TTL (Time to Live)", example = "3600")
    val ttl: Int,

    @Schema(description = "Domain CFID", example = "123e4567e89b12d3a456426655440000")
    val cfid: String,

    @Schema(description = "comment", example="")
    val comment: String
)
