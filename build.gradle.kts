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
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	maven { setUrl("https://jitpack.io") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter:_")
	implementation("org.springframework.boot:spring-boot-starter-web:_")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
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
	// https://mvnrepository.com/artifact/org.eclipse.jgit/org.eclipse.jgit
	implementation("org.eclipse.jgit:org.eclipse.jgit:_")

	// https://mvnrepository.com/artifact/com.github.kittinunf.fuel/fuel
	implementation("com.github.kittinunf.fuel:fuel:_")
	implementation("com.github.kittinunf.fuel:fuel-jackson:_")

	// Kotlin Telegram Bot
	implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:_")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}
