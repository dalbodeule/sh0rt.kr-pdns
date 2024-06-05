package space.mori.dnsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.web.bind.annotation.*
import space.mori.dnsapi.db.Domain
import space.mori.dnsapi.db.Record as DomainRecord
import space.mori.dnsapi.dto.RecordRequestDTO
import space.mori.dnsapi.dto.RecordResponseDTO
import space.mori.dnsapi.getISOFormat
import space.mori.dnsapi.service.RecordService
import java.util.*

@RestController
@RequestMapping("/zones")
class RecordController(
    @Autowired
    private val recordService: RecordService,
) {
    @GetMapping("{zone_id}/dns_records")
    @Operation(summary = "Get all records", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return All Records",
            content = [Content(array = ArraySchema(schema = Schema(implementation = RecordResponseDTO::class)))]),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = Void::class))]),
    ])
    fun allRecords(@PathVariable zone_id: String): List<RecordResponseDTO> {
        return recordService.getRecordsByDomain(zone_id)?.map{ it } ?: listOf()
    }

    @GetMapping("{zone_id}/dns_records/{dns_record_id}")
    @Operation(summary = "Get Record by ID", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return Record",
            content = [Content(schema = Schema(implementation = RecordResponseDTO::class))]),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = Void::class))])
    ])
    fun getRecordByCfid(@PathVariable zone_id: String, @PathVariable dns_record_id: String): RecordResponseDTO {
        return recordService.getRecord(zone_id, dns_record_id)
    }

    @PostMapping("{zone_id}/dns_records")
    @Operation(summary = "Add Record by ID", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return Record",
            content = [Content(schema = Schema(implementation = RecordResponseDTO::class))]),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = Void::class))]),
    ])
    fun createRecord(@PathVariable zone_id: String, @RequestBody record: RecordRequestDTO): RecordResponseDTO {
        return recordService.createRecord(zone_id, record)
    }

    @DeleteMapping("{zone_id}/dns_records/{dns_record_id}")
    @Operation(summary = "Remove Record by ID", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return Record",
            content = [Content(schema = Schema(implementation = Void::class))]),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = Void::class))]),
    ])
    fun deleteRecord(@PathVariable zone_id: String, @PathVariable dns_record_id: String) {
        recordService.deleteRecord(zone_id, dns_record_id)
    }

    @PatchMapping("{zone_id}/dns_records/{dns_record_id}")
    @Operation(summary = "Update Record by ID", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return Record",
            content = [Content(schema = Schema(implementation = RecordResponseDTO::class))]),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = Void::class))]),
    ])
    fun updateRecord(@PathVariable zone_id: String, @PathVariable dns_record_id: String, @RequestBody record: RecordRequestDTO): RecordResponseDTO {
        return recordService.updateRecord(zone_id, dns_record_id, record)
    }

    private fun DomainRecord.toDTO() = RecordResponseDTO(
        id = cfid,
        type = type,
        name = name,
        content = content,
        zoneId = domain.cfid,
        zoneName = domain.name,
        priority = prio,
        ttl = ttl,
        createdOn = createdOn.getISOFormat(),
        modifiedOn = modifiedOn.getISOFormat(),
        comment = comment
    )
}