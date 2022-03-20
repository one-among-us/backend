import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.6"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"
	java
}

group = "org.hydev"
version = "1.0.3"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	maven { setUrl("https://jitpack.io") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter:2.5.6")
	implementation("org.springframework.boot:spring-boot-starter-web:2.5.6")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.6")

	// Database
	// https://my.oschina.net/mingyuelab/blog/3190313
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.5.6")
	implementation("org.xerial:sqlite-jdbc:3.36.0.2")
	implementation("com.github.gwenn:sqlite-dialect:0.1.2")
	// https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
	implementation("org.mariadb.jdbc:mariadb-java-client:2.7.4")

	// Github Client
	// https://mvnrepository.com/artifact/org.kohsuke/github-api
	implementation("org.kohsuke:github-api:1.135")
	// https://mvnrepository.com/artifact/org.eclipse.jgit/org.eclipse.jgit
	implementation("org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r")

	// https://mvnrepository.com/artifact/com.github.kittinunf.fuel/fuel
	implementation("com.github.kittinunf.fuel:fuel:2.3.1")
	implementation("com.github.kittinunf.fuel:fuel-jackson:2.3.1")

	// Kotlin Telegram Bot
	implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.0.6")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}
