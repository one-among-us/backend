package org.hydev.back

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

val secrets = getSecrets()

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
