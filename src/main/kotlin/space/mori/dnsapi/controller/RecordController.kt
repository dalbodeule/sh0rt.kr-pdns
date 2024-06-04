package space.mori.dnsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import space.mori.dnsapi.service.RecordService
import space.mori.dnsapi.db.Record
import space.mori.dnsapi.dto.RecordRequestDTO
import space.mori.dnsapi.dto.RecordResponseDTO
import java.util.*

@RestController
@RequestMapping("/record")
class RecordController {
    @Autowired
    private val recordService: RecordService? = null

    @GetMapping
    @Operation(summary = "Get all records", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return All Records",
            content = [Content(array = ArraySchema(schema = Schema(implementation = RecordResponseDTO::class)))]),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = Void::class))]),
    ])
    fun allRecords(@PathVariable cfid: String?): List<RecordResponseDTO?> {
        return recordService!!.getAllRecords(cfid!!).map{ it?.toDTO() }
    }

    @GetMapping("/{cfid}")
    @Operation(summary = "Get Record by ID", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return Record",
            content = [Content(schema = Schema(implementation = RecordResponseDTO::class))]),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = Void::class))])
    ])
    fun getRecordByCfid(@PathVariable cfid: String?): Optional<RecordResponseDTO> {
        return recordService!!.getRecordById(cfid!!).map { it.toDTO() }
    }

    @PostMapping
    @Operation(summary = "Add Record by ID", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return Record",
            content = [Content(schema = Schema(implementation = RecordResponseDTO::class))]),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = Void::class))]),
    ])
    fun createRecord(@RequestBody record: RecordRequestDTO): RecordResponseDTO {
        return recordService!!.createRecord(record).toDTO()
    }

    @DeleteMapping("/{cfid}")
    @Operation(summary = "Remove Record by ID", tags=["record"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Return Record",
            content = [Content(schema = Schema(implementation = Void::class))]),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = Void::class))]),
    ])
    fun deleteRecord(@PathVariable cfid: String?) {
        recordService!!.deleteRecord(cfid!!)
    }

    private fun Record.toDTO() = RecordResponseDTO(
        cfid = cfid!!,
        name = name!!,
        type = type!!,
        content = content!!,
        prio = prio!!,
        ttl = ttl!!,
        changeDate = changeDate!!,
        auth = auth,
        disabled = disabled
    )
}