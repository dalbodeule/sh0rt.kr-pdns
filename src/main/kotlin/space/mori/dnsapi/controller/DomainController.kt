package space.mori.dnsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import space.mori.dnsapi.db.Domain
import space.mori.dnsapi.dto.ApiResponseDTO
import space.mori.dnsapi.dto.DeleteResponseWithId
import space.mori.dnsapi.dto.DomainRequestDTO
import space.mori.dnsapi.dto.DomainResponseDTO
import space.mori.dnsapi.filter.getCurrentUser
import space.mori.dnsapi.service.DomainService


@RestController
@RequestMapping("/zones")
class DomainController(
    @Autowired
    private val domainService: DomainService
) {
    @GetMapping
    @Operation(summary = "Get all domains", tags = ["domain"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Returns all domains", useReturnTypeSchema = true),
        ApiResponse(responseCode = "404", description = "Returns not found",
            content = [Content(schema = Schema(implementation = ApiResponseDTO::class))])
    ])
    fun allDomains(): ApiResponseDTO<List<DomainResponseDTO?>> {
        return ApiResponseDTO(result = domainService.getAllDomains().map { it.toDTO() })
    }

    @Operation(summary = "Get domain", tags = ["domain"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Returns domain", useReturnTypeSchema = true),
        ApiResponse(responseCode = "404", description = "Returns not found",
            content = [Content(schema = Schema(implementation = ApiResponseDTO::class))])
    ])
    @GetMapping("/{cfid}")
    fun getDomainByCfid(
        @PathVariable cfid: String?
    ): ApiResponseDTO<DomainResponseDTO> {
        return ApiResponseDTO(result = domainService.getDomainById(cfid!!).toDTO())
    }

    @Operation(summary = "Create domain", tags = ["domain"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Created domain", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = ApiResponseDTO::class))])
    ])
    @PostMapping
    fun createDomain(@RequestBody domain: DomainRequestDTO): ApiResponseDTO<DomainResponseDTO> {
        return ApiResponseDTO(result = domainService.createDomain(domain).toDTO())
    }

    @Operation(summary = "Delete domain", tags = ["domain"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Deleted domain", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "Bad request",
            content = [Content(schema = Schema(implementation = ApiResponseDTO::class))])
    ])
    @DeleteMapping("/{domain_id}")
    fun deleteDomain(@PathVariable domain_id: String?): ApiResponseDTO<DeleteResponseWithId> {
        domainService.deleteDomain(domain_id!!)

        return ApiResponseDTO(result=DeleteResponseWithId(domain_id))
    }

    private fun Domain.toDTO() = DomainResponseDTO(id = cfid, name = name)
}
