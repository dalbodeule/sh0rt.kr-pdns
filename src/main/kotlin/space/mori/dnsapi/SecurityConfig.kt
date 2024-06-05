package space.mori.dnsapi

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import space.mori.dnsapi.filter.UserFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Autowired
    private val service: UserFilter? = null

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf{ it.disable() }
            .cors{ it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {
                it.requestMatchers("/zones/**").authenticated()
                it.requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/api-docs/**",
                    "/v3/api-docs/**",
                    "/v2/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
            }
            .addFilterBefore(service, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}