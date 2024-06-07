package space.mori.dnsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import space.mori.dnsapi.PowerDNSAPIError
import space.mori.dnsapi.dto.*
import space.mori.dnsapi.service.RecordService

@RestController
@RequestMapping("/zones")
class RecordController(
    @Autowired
    private val recordService: RecordService,
) {
    @GetMapping("{zone_id}/dns_records")
    @Operation(summary = "Get all records", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return All Records", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = ApiResponseDTO::class))]),
    ])
    fun allRecords(@PathVariable zone_id: String): ApiResponseDTO<List<RecordResponseDTO>> {
        try {
            return ApiResponseDTO(result = recordService.getRecordsByDomain(zone_id)?.map{ it } ?: listOf())
        } catch(e : PowerDNSAPIError) {
            val errors = mutableListOf(e.error)
            errors.addAll(e.errors)
            throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED,
                ApiResponseDTO(false, errors = errors.map { ErrorOrMessage(1, it) }, result = listOf(null)).toString()
            )
        }
    }

    @GetMapping("{zone_id}/dns_records/{dns_record_id}")
    @Operation(summary = "Get Record by ID", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return Record", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = ApiResponseDTO::class))]),
    ])
    fun getRecordByCfid(@PathVariable zone_id: String, @PathVariable dns_record_id: String): ApiResponseDTO<RecordResponseDTO> {
        try {
            return ApiResponseDTO(result = recordService.getRecord(zone_id, dns_record_id))
        } catch(e : PowerDNSAPIError) {
            val errors = mutableListOf(e.error)
            errors.addAll(e.errors)
            throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED,
                ApiResponseDTO(false, errors = errors.map { ErrorOrMessage(1, it) }, result = listOf(null)).toString()
            )
        }
    }

    @PostMapping("{zone_id}/dns_records")
    @Operation(summary = "Add Record by ID", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return Record", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = ApiResponseDTO::class))]),
    ])
    fun createRecord(@PathVariable zone_id: String, @RequestBody record: RecordRequestDTO): ApiResponseDTO<RecordResponseDTO> {
        try {
            return ApiResponseDTO(result = recordService.createRecord(zone_id, record))
        } catch(e : PowerDNSAPIError) {
            val errors = mutableListOf(e.error)
            errors.addAll(e.errors)
            throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED,
                ApiResponseDTO(false, errors = errors.map { ErrorOrMessage(1, it) }, result = listOf(null)).toString()
            )
        }
    }

    @DeleteMapping("{zone_id}/dns_records/{dns_record_id}")
    @Operation(summary = "Remove Record by ID", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return Record", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = ApiResponseDTO::class))]),
    ])
    fun deleteRecord(@PathVariable zone_id: String, @PathVariable dns_record_id: String): ApiResponseDTO<DeleteResponseWithId> {
        try {
            val record_id = recordService.deleteRecord(zone_id, dns_record_id)
            return ApiResponseDTO(result = DeleteResponseWithId(record_id))
        } catch(e : PowerDNSAPIError) {
            val errors = mutableListOf(e.error)
            errors.addAll(e.errors)
            throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED,
                ApiResponseDTO(false, errors = errors.map { ErrorOrMessage(1, it) }, result = listOf(null)).toString()
            )
        }
    }

    @PatchMapping("{zone_id}/dns_records/{dns_record_id}")
    @Operation(summary = "Update Record by ID", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return Record", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = ApiResponseDTO::class))]),
    ])
    fun updateRecord(@PathVariable zone_id: String, @PathVariable dns_record_id: String, @RequestBody record: RecordRequestDTO): ApiResponseDTO<RecordResponseDTO> {
        try {
            return ApiResponseDTO(result = recordService.updateRecord(zone_id, dns_record_id, record))
        } catch(e : PowerDNSAPIError) {
            val errors = mutableListOf(e.error)
            errors.addAll(e.errors)
            throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED,
                ApiResponseDTO(false, errors = errors.map { ErrorOrMessage(1, it) }, result = listOf(null)).toString()
            )
        }
    }
}