package org.hydev.back

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.entities.ChatId
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

val secrets = getSecrets()
lateinit var bot: Bot

fun main(args: Array<String>) {
	bot = bot { token = secrets.telegramBotToken }
	runApplication<Application>(*args)
}
