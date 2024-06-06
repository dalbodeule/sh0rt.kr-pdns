import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
	id("java")
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
	id("org.hibernate.orm") version "6.5.2.Final"
	id("org.graalvm.buildtools.native") version "0.10.2"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
	kotlin("plugin.jpa") version "1.9.24"
}

val datetimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")

group = "space.mori"
version = "0.0.1"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

	// https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-bindings
	implementation("org.springframework.cloud:spring-cloud-bindings:2.0.3")

	implementation("com.google.code.gson:gson:2.11.0")
	implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<BootBuildImage> {
	docker {
		publish = true
	}

	imageName.set("dalbodeule/dnsapi")
	tags.set(setOf("dalbodeule/dnsapi:latest", "dalbodeule/dnsapi:${datetimeFormatter.format(LocalDateTime.now())}"))
	buildpacks.set(setOf("docker.io/paketobuildpacks/oracle", "urn:cnb:builder:paketo-buildpacks/java-native-image"))

	environment = mapOf(
		"BP_NATIVE_IMAGE" to "true",
		"BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "-H:+UnlockExperimentalVMOptions",
		"BP_JVM_TYPE" to "JDK",
		"BP_JVM_VERSION" to "21",
	)
}

hibernate {
	enhancement {
		enableAssociationManagement.set(true)
	}
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	systemProperty("spring.profiles.active", "dev")
}

graalvmNative {
	binaries.all {
		buildArgs.add("--initialize-at-build-time=org.slf4j.helpers")
	}
}