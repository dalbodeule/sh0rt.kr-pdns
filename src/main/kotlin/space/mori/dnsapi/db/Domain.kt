package space.mori.dnsapi.db

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.util.*


@Entity
@Table(name = "domains")
class Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    @Column(nullable = false, length = 255)
    val domainName: String? = null

    @Column(nullable = true, length = 128)
    val master: String? = null

    @Column(nullable = true, name = "last_check")
    val lastCheck: Int? = null

    @Column(nullable = false, length = 6)
    val type: String? = null

    @Column(nullable = true, name = "notified_serial")
    val notifiedSerial: Int? = null

    @Column(nullable = false, length = 128)
    val account: String? = null

    @Column(unique = true, nullable = false, length = 32)
    var cfid: String? = null

    @OneToMany(mappedBy = "domain", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    private val records: Set<Record>? = null

    @PrePersist
    protected fun onCreate() {
        this.cfid = UUID.randomUUID().toString().replace("-", "")
    } // Getters and setters
}