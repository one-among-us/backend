import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.6"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"
	java
}

group = "org.hydev"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// https://my.oschina.net/mingyuelab/blog/3190313
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.xerial:sqlite-jdbc:3.34.0")
	implementation("com.github.gwenn:sqlite-dialect:0.1.1")

	// https://mvnrepository.com/artifact/org.kohsuke/github-api
	implementation("org.kohsuke:github-api:1.135")
	// https://mvnrepository.com/artifact/org.eclipse.jgit/org.eclipse.jgit
	implementation("org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
