package space.mori.dnsapi.db

import io.swagger.v3.oas.annotations.media.Schema
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
    var cfid: String = UUID.randomUUID().toString().replace("-", "")
) {
    @OneToMany(mappedBy = "domain", cascade = [CascadeType.ALL], orphanRemoval = true)
    var records: List<Record> = mutableListOf()
}