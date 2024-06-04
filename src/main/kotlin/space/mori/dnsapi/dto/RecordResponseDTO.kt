package space.mori.dnsapi.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Response DTO for Record")
data class RecordResponseDTO(
    @Schema(description = "Record CFID", example = "123e4567e89b12d3a456426655440001")
    val cfid: String,

    @Schema(description = "Host name", example = "www.domain.tld")
    val name: String,

    @Schema(description = "Record type", example = "A")
    val type: String,

    @Schema(description = "Record data", example = "192.0.2.1")
    val content: String,

    @Schema(description = "TTL (Time to Live)", example = "3600")
    val ttl: Int,

    @Schema(description = "TTL per second", example = "300s")
    val prio: Int,

    @Schema(description = "Changed date with Unix Timestamp")
    val changeDate: Int,

    @Schema(description = "is disabled?", example = "false")
    val disabled: Boolean,

    @Schema(description = "is authed", example = "true")
    val auth: Boolean,
)