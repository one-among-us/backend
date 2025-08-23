package org.hydev.back

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.logging.LogLevel
import kotlinx.coroutines.*
import org.hibernate.SessionFactory
import org.hydev.back.controller.CommentController
import org.hydev.back.db.Ban
import org.hydev.back.db.BanRepo
import org.hydev.back.geoip.GeoIP
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.persistence.EntityManager

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

/**
 * Initialize bot after Spring application is constructed
 */
@Component
class PostConstruct(
	private val commentController: CommentController,
	private val banRepo: BanRepo,
	private val geoIP: GeoIP,
    private val em: EntityManager,
    private val sf: SessionFactory
) {
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
				secureCmd("help") { """
					/ban <ip> [reason]
					/unban <ip>
					/listban""".trimIndent()
				}
				secureCmd("ban") {
					val args = (message.text ?: "").split(" ").slice(1)
					if (args.isEmpty()) return@secureCmd "Usage: /ban <ip> [reason]"

					val entry = Ban(ip = args[0], reason = args.slice(1).joinToString(" "))
					banRepo.save(entry)
					"Banned ${entry.ip}"
				}
				secureCmd("unban") {
					val args = (message.text ?: "").split(" ").slice(1)
					if (args.size != 1) return@secureCmd "Usage: /unban <ip>"

					val entry = banRepo.queryByIp(args[0]) ?: return@secureCmd "Cannot find entry by ip ${args[0]}"
					banRepo.delete(entry)
					"Unbanned ${entry.ip}"
				}
				secureCmd("listban") {
					"Banned IPs:\n" + banRepo.findAll().joinToString("\n") { it.ip }
				}
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
