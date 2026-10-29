import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
	id("java")
	id("org.springframework.boot") version "4.1.2"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.hibernate.orm") version "7.1.4.Final"
	id("org.graalvm.buildtools.native") version "1.1.17"
	kotlin("jvm") version "2.4.21"
	kotlin("plugin.spring") version "2.4.21"
	kotlin("plugin.jpa") version "2.4.21"
}

val datetimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
val jvmVersion = 25

group = "space.mori"
version = "1.0.0"

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(jvmVersion))
}

kotlin {
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
		jvmTarget.set(JvmTarget.fromTarget(jvmVersion.toString()))
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("tools.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.2.0")

	// https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-bindings
	implementation("org.springframework.cloud:spring-cloud-bindings:2.0.4")

	implementation("com.google.code.gson:gson:2.15.0")
	implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")

	// https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
	implementation("com.squareup.okhttp3:okhttp:5.5.1")

	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("com.squareup.okhttp3:mockwebserver3:5.5.1")
	testRuntimeOnly("com.h2database:h2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<Test>("test") {
	useJUnitPlatform { excludeTags("openapi-generation") }
}

tasks.register<Test>("generateOpenApi") {
	group = "documentation"
	description = "Generate the root openapi.json from the running Spring application"
	dependsOn(tasks.testClasses)
	testClassesDirs = sourceSets.test.get().output.classesDirs
	classpath = sourceSets.test.get().runtimeClasspath
	useJUnitPlatform { includeTags("openapi-generation") }
	val openApiFile = layout.projectDirectory.file("openapi.json")
	outputs.file(openApiFile)
	systemProperty("openapi.output", openApiFile.asFile.absolutePath)
}

tasks.register<Exec>("updateReadme") {
	group = "documentation"
	description = "Update README.md from the generated OpenAPI document"
	dependsOn("generateOpenApi")
	commandLine("python3", "scripts/update_readme.py")
}

tasks.register("updateDocs") {
	group = "documentation"
	description = "Refresh openapi.json and README.md"
	dependsOn("updateReadme")
}

tasks.withType<BootBuildImage> {
	val dockerId = System.getenv("DOCKER_USERNAME") ?: "dalbodeule"
	val dockerPw = System.getenv("DOCKER_PASSWORD") ?: ""

	docker {
		publish = true
		publishRegistry {
			username = dockerId
			password = dockerPw
		}
	}

	imageName.set("$dockerId/dnsapi")
	tags.set(setOf("$dockerId/dnsapi:latest", "$dockerId/dnsapi:${datetimeFormatter.format(LocalDateTime.now())}"))
	buildpacks.set(setOf("docker.io/paketobuildpacks/oracle", "urn:cnb:builder:paketo-buildpacks/java-native-image"))

	environment = mapOf(
		"BP_NATIVE_IMAGE" to "true",
		"BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "-H:+UnlockExperimentalVMOptions",
		"BP_JVM_TYPE" to "JDK",
		"BP_JVM_VERSION" to jvmVersion.toString(),
	)
}

hibernate {
	enhancement {}
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	systemProperty("spring.profiles.active", "dev")
}

graalvmNative {
	binaries.all {
		buildArgs.add("--initialize-at-build-time=org.slf4j.helpers")
	}
}
