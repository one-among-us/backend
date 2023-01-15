import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	kotlin("jvm")
	kotlin("plugin.spring")
	java
}

group = "org.hydev"
version = "1.0.3"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
	maven { setUrl("https://jitpack.io") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter:_")
	implementation("org.springframework.boot:spring-boot-starter-web:_")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// Database
	// https://my.oschina.net/mingyuelab/blog/3190313
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.xerial:sqlite-jdbc:_")
	implementation("com.github.gwenn:sqlite-dialect:_")
	// https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
	implementation("org.mariadb.jdbc:mariadb-java-client:_")

	// Github Client
	// https://mvnrepository.com/artifact/org.kohsuke/github-api
	implementation("org.kohsuke:github-api:_")

	// HTTP Client
	// https://mvnrepository.com/artifact/com.github.kittinunf.fuel/fuel
	implementation("com.github.kittinunf.fuel:fuel:_")
	implementation("com.github.kittinunf.fuel:fuel-jackson:_")
	implementation("com.github.kittinunf.fuel:fuel-coroutines:_")

	// Kotlin Telegram Bot
	implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:_")

	// Jackson serialization
	// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
	implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
	// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-dataformat-yaml
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.1")

	// Geoip
	implementation("com.maxmind.geoip2:geoip2:4.0.0")
	implementation("com.github.jarod:qqwry-java:0.9.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}
