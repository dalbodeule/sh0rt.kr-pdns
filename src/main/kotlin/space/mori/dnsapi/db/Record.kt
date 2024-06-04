package space.mori.dnsapi.db

import jakarta.persistence.*
import java.util.*


@Entity
@Table(name = "records")
class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    @ManyToOne
    @JoinColumn(name = "domain_id", nullable = false)
    var domain: Domain? = null

    @Column(nullable = false, length = 255)
    var name: String? = null

    @Column(nullable = false, length = 10)
    var type: String? = null

    @Column(nullable = false, length = 64000)
    var content: String? = null

    @Column(nullable = true)
    var ttl: Int?

    @Column(nullable = true)
    var prio: Int?

    @Column(nullable = true)
    var changeDate: Int?

    var disabled: Boolean = false
    var auth: Boolean = true

    @Column(unique = true, nullable = false, length = 32)
    var cfid: String? = null

    @Column(nullable = true, length = 64)
    var comment: String? = null

    @PrePersist
    private fun onCreate() {
        this.cfid = UUID.randomUUID().toString().replace("-", "")
    } // Getters and setters

    constructor(
        name: String,
        type: String,
        content: String,
        changeDate: Int?,
        disabled: Boolean,
        domain: Domain,
        comment: String?,
        auth: Boolean = true,
        ttl: Int? = 300,
        prio: Int? = 0,
    ) {
        this.name = name
        this.type = type
        this.content = content
        this.ttl = ttl
        this.prio = prio
        this.changeDate = changeDate
        this.disabled = disabled
        this.auth = auth
        this.domain = domain
        this.comment = comment
    }
}