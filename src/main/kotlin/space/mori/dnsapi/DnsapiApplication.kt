package space.mori.dnsapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import io.github.cdimascio.dotenv.dotenv

@SpringBootApplication
class DnsapiApplication

fun main(args: Array<String>) {
	val envVars = mapOf(
		"DB_HOST" to dotenv["DB_HOST"],
		"DB_PORT" to dotenv["DB_PORT"],
		"DB_NAME" to dotenv["DB_NAME"],
		"DB_USER" to dotenv["DB_USER"],
		"DB_PASSWORD" to dotenv["DB_PASSWORD"]
	)

	runApplication<DnsapiApplication>(*args) {
		setDefaultProperties(envVars)
	}
}

val dotenv = dotenv {
	ignoreIfMissing = true
}