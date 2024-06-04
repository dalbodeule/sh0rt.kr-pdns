package space.mori.dnsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.*
import space.mori.dnsapi.db.Domain
import space.mori.dnsapi.dto.DomainResponseDTO
import space.mori.dnsapi.service.DomainService
import java.util.*


@RestController
@RequestMapping("/domain")
class DomainController {
    @Autowired
    private val domainService: DomainService? = null

    @get:GetMapping
    @get:Operation(summary = "Get all domains", tags = ["domain"])
    @get:ApiResponse(responseCode = "200", description = "Returns all domains",
        content = [Content(array = ArraySchema(schema = Schema(implementation = DomainResponseDTO::class)))])
    val allDomains: List<DomainResponseDTO?>
        get() = domainService!!.getAllDomains().map { it?.toDTO() }

    @Operation(summary = "Get domain", tags = ["domain"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Returns domain",
            content = [Content(schema = Schema(implementation = DomainResponseDTO::class))]),
        ApiResponse(responseCode = "404", description = "Returns not found",
            content = [Content(schema = Schema(implementation = Void::class))])
    ])
    @GetMapping("/{cfid}")
    fun getDomainByCfid(
        @Parameter(description = "CFID", required = true)
        @PathVariable cfid: String?
    ): Optional<DomainResponseDTO> {
        return domainService!!.getDomainById(cfid!!).map { it.toDTO() }
    }

    @Operation(summary = "Create domain", tags = ["domain"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Created domain",
            content = [Content(schema = Schema(implementation = DomainResponseDTO::class))]),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = Void::class))])
    ])
    @PostMapping
    fun createDomain(@RequestBody domain: Domain?): DomainResponseDTO {
        return domainService!!.createDomain(domain!!).toDTO()
    }

    @Operation(summary = "Delete domain", tags = ["domain"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Deleted domain",
            content = [Content(schema = Schema(implementation = Void::class))]),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = Void::class))])
    ])
    @DeleteMapping("/{cfid}")
    fun deleteDomain(@PathVariable cfid: String?) {
        domainService!!.deleteDomain(cfid!!)
    }

    private fun Domain.toDTO() = DomainResponseDTO(cfid = cfid!!, domainName = domainName!!)
}
