package space.mori.dnsapi.db

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*


interface RecordRepository : JpaRepository<Record?, Long?> {
    @Transactional
    fun findByCfid(cfid: String): Optional<Record>

    @Transactional
    fun findByDomainIdAndCfid(domainId: Long, cfid: String): Optional<Record>

    @Transactional
    fun deleteByCfid(cfid: String): Int

    @Transactional
    fun deleteByDomainIdAndCfid(domain_id: Long, cfid: String): Int
}