package space.mori.dnsapi.db

import jakarta.persistence.*
import java.util.*


@Entity
@Table(name = "records")
data class Record(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id", nullable = false)
    var domain: Domain,

    var name: String,
    var type: String,
    var content: String,
    var ttl: Int,
    var prio: Int,
    var disabled: Boolean,
    var auth: Boolean,

    var createdOn: Date,
    var modifiedOn: Date,

    var comment: String,

    @Column(nullable = false, unique = true)
    var cfid: String = UUID.randomUUID().toString().replace("-", "")
)