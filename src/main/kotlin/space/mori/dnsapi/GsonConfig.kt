package space.mori.dnsapi

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class GsonConfig {
    @Bean
    fun gson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapter(PowerDNSAPIError::class.java, PowerDNSAPIErrorDeserializer())
            .create()
    }
}