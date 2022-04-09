package org.hydev.back

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.logging.LogLevel
import org.hydev.back.controller.CommentController
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

val secrets = getSecrets()
lateinit var bot: Bot

@SpringBootApplication
class Application

@Component
class PostConstruct(private val commentController: CommentController)
{
	@PostConstruct
	fun init()
	{
		// Create bot
		bot = bot {
			logLevel = LogLevel.Error
			token = secrets.telegramBotToken
			dispatch {
				command("start") { bot.sendMessage(ChatId.fromId(message.chat.id), "🐱") }
				callbackQuery("comment-pass", commentController.commentCallback)
				callbackQuery("comment-reject", commentController.commentCallback)
			}
		}
		bot.sendMessage(ChatId.fromId(secrets.telegramChatID), "Server Started.")
		bot.startPolling()
	}
}

fun main(args: Array<String>)
{
	runApplication<Application>(*args)
}
