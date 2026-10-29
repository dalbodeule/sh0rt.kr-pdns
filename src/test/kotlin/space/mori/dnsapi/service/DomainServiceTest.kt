package space.mori.dnsapi.service

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import space.mori.dnsapi.PowerDNSAPIClient
import space.mori.dnsapi.db.Domain
import space.mori.dnsapi.db.DomainRepository
import space.mori.dnsapi.db.User
import java.util.Optional

class DomainServiceTest {
    private val repository = Mockito.mock(DomainRepository::class.java)
    private val powerDns = Mockito.mock(PowerDNSAPIClient::class.java)
    private val service = DomainService(repository, powerDns)

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun rejectsDeletingAnotherUsersDomainBeforeCallingPowerDns() {
        val owner = User(id = 1, apiKey = "owner", name = "owner")
        val otherUser = User(id = 2, apiKey = "other", name = "other")
        val domain = Domain(name = "example.com", user = owner)
        Mockito.`when`(repository.findByCfid(domain.cfid)).thenReturn(Optional.of(domain))
        authenticate(otherUser)

        assertThrows(RuntimeException::class.java) { service.deleteDomain(domain.cfid) }
        Mockito.verifyNoInteractions(powerDns)
        Mockito.verify(repository, Mockito.never()).deleteByCfid(domain.cfid)
    }

    @Test
    fun deletingAnOwnedDomainSucceedsWhenOneRowWasRemoved() {
        val owner = User(id = 1, apiKey = "owner", name = "owner")
        val domain = Domain(name = "example.com", user = owner)
        Mockito.`when`(repository.findByCfid(domain.cfid)).thenReturn(Optional.of(domain))
        Mockito.`when`(repository.deleteByCfid(domain.cfid)).thenReturn(1)
        authenticate(owner)

        assertEquals(domain.cfid, service.deleteDomain(domain.cfid))
        Mockito.verify(powerDns).deleteZone(domain.name)
    }

    @Test
    fun aUserWithoutDomainsGetsAnEmptyList() {
        val user = User(id = 1, apiKey = "owner", name = "owner")
        Mockito.`when`(repository.findAllByUser(user)).thenReturn(emptyList())
        authenticate(user)

        assertEquals(emptyList<Domain>(), service.getAllDomains())
    }

    private fun authenticate(user: User) {
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(user, null, emptyList())
    }
}
