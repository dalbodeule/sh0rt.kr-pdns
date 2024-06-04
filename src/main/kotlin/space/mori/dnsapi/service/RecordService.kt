package space.mori.dnsapi.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import space.mori.dnsapi.db.DomainRepository
import space.mori.dnsapi.db.RecordRepository
import space.mori.dnsapi.db.Record as DomainRecord
import space.mori.dnsapi.dto.RecordRequestDTO
import java.util.*


@Service
class RecordService {
    @Autowired
    private lateinit var domainRepository: DomainRepository
    @Autowired
    private val recordRepository: RecordRepository? = null

    fun getAllRecords(cfid: String): List<DomainRecord?> {
        return recordRepository!!.findByDomainCfid(cfid)
    }

    fun getRecordById(cfid: String): Optional<DomainRecord> {
        return recordRepository!!.findByCfid(cfid)
    }

    fun createRecord(record: RecordRequestDTO): DomainRecord {
        val domain = domainRepository.findByCfid(record.cfid)
            .orElseThrow { IllegalArgumentException("Invalid domain CFID") }
        val r = DomainRecord(
            name = record.host,
            type = record.type,
            content = record.data,
            ttl = record.ttl,
            domain = domain,
            comment = record.comment,
            changeDate = java.util.Date().time.toInt(),
            disabled = false
        )
        return recordRepository!!.save(r)
    }

    fun deleteRecord(cfid: String) {
        recordRepository!!.deleteByCfid(cfid)
    }
}