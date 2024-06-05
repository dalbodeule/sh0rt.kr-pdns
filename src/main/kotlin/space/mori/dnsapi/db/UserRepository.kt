package space.mori.dnsapi.db

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByApiKey(apiKey: String): User?
}