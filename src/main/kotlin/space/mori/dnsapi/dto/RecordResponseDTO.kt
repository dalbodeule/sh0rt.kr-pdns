package space.mori.dnsapi.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Response DTO for Record")
data class RecordResponseDTO(
    @Schema(description = "Record ID", example = "123e4567e89b12d3a456426655440001")
    val id: String,

    @Schema(description = "Record type", example = "A")
    var type: String,

    @Schema(description = "Record name", example = "test")
    var name: String,

    @Schema(description = "Record content", example = "1.1.1.1")
    var content: String,

    @Schema(description = "Zone(TLD) ID", example = "123e4567e89b12d3a456426655440001")
    val zoneId: String,

    @Schema(description = "Zone name(TLD)", example = "example.com")
    val zoneName: String,

    @Schema(description = "Record creation time", example = "2014-01-01T05:20:00.12345Z")
    val createdOn: String,

    @Schema(description = "Record modification time", example = "2014-01-01T05:20:00.12345Z")
    val modifiedOn: String,

    @Schema(description = "Record priority", example = "0")
    val priority: Int? = 0,

    @Schema(description = "is proxyable: must false", example = "false")
    val proxiable: Boolean = false,

    @Schema(description = "is proxied: must false", example = "false")
    val proxied: Boolean = false,

    @Schema(description = "Record TTL", example = "300")
    val ttl: Int = 300,

    @Schema(description = "Record is locked: must false", example = "false")
    val locked: Boolean = false,

    @Schema(description = "Record comments", example = "")
    val comment: String? = null,
)