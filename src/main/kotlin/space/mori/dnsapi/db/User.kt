package space.mori.dnsapi.db

import jakarta.persistence.*
import org.springframework.security.core.context.SecurityContextHolder

@Entity
data class User(
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, name = "api_key", length = 64)
    val apiKey: String,

    @Column(nullable = false, unique = false, length = 20)
    val name: String,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val domains: List<Domain> = mutableListOf(),
) {
    override fun toString(): String = "User(id=$id, name='$name')"
}