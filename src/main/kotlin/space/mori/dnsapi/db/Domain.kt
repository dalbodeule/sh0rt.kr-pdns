package space.mori.dnsapi.db

import jakarta.persistence.*
import java.util.*


@Entity
@Table(name = "domains")
data class Domain(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false, unique = true)
    var cfid: String = UUID.randomUUID().toString().replace("-", ""),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @OneToMany(mappedBy = "domain", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var records: List<Record> = mutableListOf()
) {
    override fun toString(): String = "Domain(id=$cfid, name='$name')"
}