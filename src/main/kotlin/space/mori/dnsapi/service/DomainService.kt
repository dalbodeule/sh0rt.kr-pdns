package space.mori.dnsapi.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import space.mori.dnsapi.db.Domain
import space.mori.dnsapi.db.DomainRepository
import java.util.*


@Service
class DomainService {
    @Autowired
    private val domainRepository: DomainRepository? = null

    fun getAllDomains(): List<Domain?> {
        return domainRepository!!.findAll()
    }

    fun getDomainById(cfid: String): Optional<Domain> {
        return domainRepository!!.findByCfid(cfid)
    }

    fun createDomain(domain: Domain): Domain {
        return domainRepository!!.save<Domain>(domain)
    }

    fun deleteDomain(cfid: String) {
        domainRepository!!.deleteByCfid(cfid)
    }
}