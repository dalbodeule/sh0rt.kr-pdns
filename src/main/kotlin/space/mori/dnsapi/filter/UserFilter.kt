package space.mori.dnsapi.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import space.mori.dnsapi.db.User
import space.mori.dnsapi.db.UserRepository

@Component
class UserFilter(
    @Autowired
    private val userRepository: UserRepository
): OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val apiKey = request.getHeader(HttpHeaders.AUTHORIZATION)
        if(apiKey != null) {
            val user = userRepository.findByApiKey(apiKey.replace("Bearer ", ""))
            if(user != null) {
                val authentication = UsernamePasswordAuthenticationToken(
                    user, null, emptyList()
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        chain.doFilter(request, response)
    }
}

fun getCurrentUser(): User =
    SecurityContextHolder.getContext().authentication.principal as User