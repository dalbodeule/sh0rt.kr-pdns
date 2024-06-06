package space.mori.dnsapi.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import space.mori.dnsapi.PowerDNSAPIClient
import space.mori.dnsapi.db.Domain
import space.mori.dnsapi.db.DomainRepository
import space.mori.dnsapi.dto.DomainRequestDTO
import space.mori.dnsapi.filter.getCurrentUser


@Service
class DomainService(
    @Autowired
    private val domainRepository: DomainRepository,
    @Autowired
    private val powerDNSApiClient: PowerDNSAPIClient
) {
    fun getAllDomains(): List<Domain> {
        val user = getCurrentUser()
        val domain = domainRepository.findAllByUser(user)
        if(domain.isEmpty()) throw RuntimeException("Unauthorized")

        return domain
    }

    fun getDomainById(domain_id: String): Domain {
        val domain = domainRepository.findByCfid(domain_id).orElseThrow {
            RuntimeException("Failed to find domain in API: $domain_id")
        }
        val user = getCurrentUser()
        if(domain.user.id != user.id)
            throw RuntimeException("Unauthorized to create record in API: $domain_id")

        return domain
    }

    fun createDomain(domain: DomainRequestDTO): Domain {
        val user = getCurrentUser()

        powerDNSApiClient.createZone(domain.name)
        val saved_domain = domainRepository.save(Domain(name=domain.name, user=user))

        return saved_domain
    }

    fun deleteDomain(domain_id: String): String {
        val domain = domainRepository.findByCfid(domain_id).orElseThrow {
            throw RuntimeException("Domain with CFID $domain_id not found")
        }

        powerDNSApiClient.deleteZone(domain.name)
        val count = domainRepository.deleteByCfid(domain_id)

        if(count > 0) throw RuntimeException("Domain with CFID $domain_id not found")
        return domain_id
    }
}