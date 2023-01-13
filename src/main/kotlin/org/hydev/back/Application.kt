package org.hydev.back

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.logging.LogLevel
import kotlinx.coroutines.*
import org.hydev.back.controller.CommentController
import org.hydev.back.geoip.GeoIP
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

val secrets = getSecrets()
lateinit var bot: Bot

/**
 * Command that can only be used in the telegram chats specified in the secrets
 */
fun Dispatcher.secureCmd(name: String, handler: CmdHandler) = cmd(name) {
	val id = message.chat.id
	if (id != secrets.telegramChatID && id != secrets.telegramBlockedChatID)
		"Insufficient permissions. This command can only be used in the group chat."
	else handler(this)
}

@SpringBootApplication
class Application

@Component
class PostConstruct(private val commentController: CommentController, private val geoIP: GeoIP)
{
	@OptIn(DelicateCoroutinesApi::class)
	@PostConstruct
	fun init()
	{
		// Create bot
		bot = bot {
			logLevel = LogLevel.Error
			token = secrets.telegramBotToken
			dispatch {
				cmd("start") { "🐈 Running!" }
				callbackQuery("comment-pass", commentController.commentCallback)
				callbackQuery("comment-reject", commentController.commentCallback)
				callbackQuery("comment-ban", commentController.commentCallback)
			}
		}
		bot.sendMessage(ChatId.fromId(secrets.telegramChatID), "Server Started.")
		GlobalScope.launch {
			println(geoIP.info("127.0.0.1"))
		}
		bot.startPolling()
	}
}

fun main(args: Array<String>)
{
	runApplication<Application>(*args)
}
