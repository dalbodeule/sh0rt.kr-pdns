package space.mori.dnsapi.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.mori.dnsapi.PowerDNSAPIClient
import space.mori.dnsapi.db.DomainRepository
import space.mori.dnsapi.db.Record as DomainRecord
import space.mori.dnsapi.db.RecordRepository
import space.mori.dnsapi.dto.RecordRequestDTO
import space.mori.dnsapi.dto.RecordResponseDTO
import space.mori.dnsapi.filter.getCurrentUser
import space.mori.dnsapi.getISOFormat
import java.util.*


@Service
class RecordService(
    @Autowired
    private val powerDNSApiClient: PowerDNSAPIClient,
    @Autowired
    private val domainRepository: DomainRepository,
    @Autowired
    private val recordRepository: RecordRepository,
) {
    fun createRecord(domain_id: String, recordRequest: RecordRequestDTO): RecordResponseDTO {
        val domain = domainRepository.findByCfid(domain_id).orElseThrow {
            throw RuntimeException("Failed to find domain in API: $domain_id")
        }

        val user = getCurrentUser()
        if(domain.user.id != user.id)
            throw RuntimeException("Unauthorized to create record in API: $domain_id")

        val response = powerDNSApiClient.createRecord(domain.name, recordRequest.name, recordRequest.type, recordRequest.content)
        if (!response) {
            throw RuntimeException("Failed to create record in PowerDNS")
        }
       val record = DomainRecord(
           domain = domain,
           name = recordRequest.name,
           type = recordRequest.type,
           content = recordRequest.content,
           ttl = recordRequest.ttl,
           prio = recordRequest.priority ?: 0,
           disabled = false,
           auth = true,
           createdOn = Date(),
           modifiedOn = Date(),
           comment = recordRequest.comment,
        )

        return RecordResponseDTO(
            id = record.cfid,
            type = record.type,
            name = record.name,
            content = record.content,
            proxiable = false,
            proxied = false,
            ttl = record.ttl,
            locked = false,
            zoneId = record.cfid,
            zoneName = domain.name,
            createdOn = record.createdOn.getISOFormat(),
            modifiedOn = record.modifiedOn.getISOFormat(),
            priority = record.prio,
            comment = record.comment
        )
    }

    fun getRecord(domain_id: String, record_id: String): RecordResponseDTO {
        val domain = domainRepository.findByCfid(domain_id).orElseThrow {
            RuntimeException("Failed to find domain in API: $domain_id")
        }

        val user = getCurrentUser()
        if(domain.user.id != user.id)
            throw RuntimeException("Unauthorized to get record in API: $domain_id")

        val record = domain.records.find { it.cfid == record_id }
        if(record == null) throw RuntimeException("Failed to find record in API: $record_id")

        return RecordResponseDTO(
            id = record.cfid,
            type = record.type,
            name = record.name,
            content = record.content,
            ttl = record.ttl,
            zoneId = record.domain.cfid,
            zoneName = record.domain.name,
            createdOn = record.createdOn.getISOFormat(),
            modifiedOn = record.modifiedOn.getISOFormat(),
            comment = record.comment,
        )
    }

    fun getRecordsByDomain(domain_id: String): List<RecordResponseDTO>? {
        val domain = domainRepository.findByCfid(domain_id).orElseThrow {
            RuntimeException("Failed to find domain in API: $domain_id")
        }

        val user = getCurrentUser()
        if(domain.user.id != user.id)
            throw RuntimeException("Unauthorized to create record in API: $domain_id")

        return domain?.records?.map { RecordResponseDTO(
            id = it.cfid,
            type = it.type,
            name = it.name,
            content = it.content,
            zoneId = it.domain.cfid,
            zoneName = it.domain.name,
            priority = it.prio,
            ttl = it.ttl,
            createdOn = it.createdOn.getISOFormat(),
            modifiedOn = it.modifiedOn.getISOFormat(),
            comment = it.comment,
        )}
    }

    @Transactional
    fun updateRecord(domain_id: String, cfid: String, updatedRecord: RecordRequestDTO): RecordResponseDTO {
        // 도메인 조회
        val domain = domainRepository.findByCfid(domain_id).orElseThrow {
            RuntimeException("Failed to find domain in API: $domain_id")
        }

        val user = getCurrentUser()
        if(domain.user.id != user.id)
            throw RuntimeException("Unauthorized to create record in API: $domain_id")

        // 레코드 조회
        val record = recordRepository.findByDomainIdAndCfid(domain.id!!, cfid)
            .orElseThrow { RuntimeException("Record not found") }

        // 레코드 업데이트
        record.name = updatedRecord.name
        record.type = updatedRecord.type
        record.content = updatedRecord.content
        record.ttl = updatedRecord.ttl
        record.prio = updatedRecord.priority ?: 0
        record.comment = updatedRecord.comment
        record.modifiedOn = Date()

        val response = powerDNSApiClient.updateRecord(domain!!.name, updatedRecord.name, updatedRecord.type, updatedRecord.content)
        if (!response) {
            throw RuntimeException("Failed to update record in PowerDNS")
        }

        // 저장
        val savedRecord = recordRepository.save(record)
        return RecordResponseDTO(
            id = savedRecord.cfid,
            type = savedRecord.type,
            name = savedRecord.name,
            content = savedRecord.content,
            proxiable = true,
            proxied = false,
            ttl = savedRecord.ttl,
            locked = false,
            zoneId = domain.cfid,
            zoneName = domain.name,
            createdOn = savedRecord.createdOn.getISOFormat(),
            modifiedOn = savedRecord.modifiedOn.getISOFormat(),
            priority = savedRecord.prio
        )
    }

    fun deleteRecord(domain_id: String, record_id: String): String {
        val domain = domainRepository.findByCfid(domain_id).orElseThrow {
            RuntimeException("Failed to find domain in API: $domain_id")
        }

        val user = getCurrentUser()
        if(domain.user.id != user.id)
            throw RuntimeException("Unauthorized to create record in API: $domain_id")

        val record = recordRepository.findByDomainIdAndCfid(domain.id!!, record_id).orElseThrow {
            RuntimeException("Failed to find record in API: $record_id")
        }

        powerDNSApiClient.deleteRecord(domain.name, record.name, record.type)
        recordRepository.deleteByDomainIdAndCfid(domain.id!!, record_id)

        return record_id
    }
}