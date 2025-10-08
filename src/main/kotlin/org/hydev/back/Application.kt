package org.hydev.back

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.logging.LogLevel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hydev.back.ai.getMorningMsg
import org.hydev.back.controller.CommentController
import org.hydev.back.db.Ban
import org.hydev.back.db.BanRepo
import org.hydev.back.geoip.GeoIP
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

val secrets = getSecrets()
lateinit var bot: Bot
private val COMMENT_ID_REGEX = Regex("^#(\\d+)\\b")

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
	private val geoIP: GeoIP
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
					/listban
					/note <note>""".trimIndent()
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
				secureCmd("note") {
					val replyTo = message.replyToMessage ?: return@secureCmd "Please reply to a pending comment to use this command"

					
					val text = replyTo.text ?: return@secureCmd "Cannot read original message content"
					val idMatch = COMMENT_ID_REGEX.find(text) ?: return@secureCmd "Cannot extract comment ID from message"

					val commentId = idMatch.groupValues[1].toLong()

					val noteContent = (message.text ?: "").substringAfter("/note").trim()
						.ifBlank { null } ?: return@secureCmd "Usage: /note <note> or /note clear"

					commentController.addNote(commentId, noteContent)
				}
				callbackQuery("comment-pass", commentController.commentCallback)
				callbackQuery("comment-reject", commentController.commentCallback)
				callbackQuery("comment-ban", commentController.commentCallback)
			}
		}
		bot.sendMessage(ChatId.fromId(secrets.telegramChatID), getMorningMsg() + "\n\n（服务器已起床）")
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
