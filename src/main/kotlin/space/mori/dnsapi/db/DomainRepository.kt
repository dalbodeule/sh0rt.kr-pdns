package space.mori.dnsapi.db

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface DomainRepository : JpaRepository<Domain?, Long?> {
    @Transactional
    fun findByCfid(cfid: String): Optional<Domain>

    @Transactional
    fun findAllByUser(user: User): List<Domain>

    @Transactional
    fun deleteByCfid(cfid: String): Int
}