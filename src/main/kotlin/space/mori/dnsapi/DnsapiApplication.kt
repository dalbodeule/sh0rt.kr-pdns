package space.mori.dnsapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import io.github.cdimascio.dotenv.dotenv
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@SpringBootApplication
class DnsapiApplication

fun main(args: Array<String>) {
	val envVars = mapOf(
		"DB_HOST" to dotenv["DB_HOST"],
		"DB_PORT" to dotenv["DB_PORT"],
		"DB_NAME" to dotenv["DB_NAME"],
		"DB_USER" to dotenv["DB_USER"],
		"DB_PASSWORD" to dotenv["DB_PASSWORD"],
		"PDNS_API_KEY" to dotenv["PDNS_API_KEY"],
		"PDNS_API_URL" to dotenv["PDNS_API_URL"],
		"PDNS_NS" to dotenv["PDNS_NS"]
	)

	runApplication<DnsapiApplication>(*args) {
		setDefaultProperties(envVars)
	}
}

val dotenv = dotenv {
	ignoreIfMissing = true
}

fun Date.getISOFormat(): String {
	val offsetDateTime = OffsetDateTime.ofInstant(this.toInstant(), ZoneOffset.UTC)
	return offsetDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
}