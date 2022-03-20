package org.hydev.back

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.HandleCallbackQuery
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.network.fold
import org.hydev.back.db.PendingCommentRepo
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

val secrets = getSecrets()
lateinit var bot: Bot

@SpringBootApplication
class Application

@Component
class PostConstruct(private val commentRepo: PendingCommentRepo)
{
	@PostConstruct
	fun init()
	{
		// Callback when the administrator clicks pass or reject
		val commentCallback: HandleCallbackQuery = {
			val pass = callbackQuery.data == "comment-pass"
			val chatId = ChatId.fromId(callbackQuery.message!!.chat.id)
			val msgId = callbackQuery.message!!.messageId
			val inlId = callbackQuery.inlineMessageId
			val message = callbackQuery.message!!.text!!
			val id = message.split(" ")[0].substring(1).toLong()

			bot.editMessageReplyMarkup(chatId, msgId, inlId, null)

			if (pass)
			{
				// Commit changes
				var statusMsgId = 0L
				bot.sendMessage(chatId, "正在提交更改...").fold({ statusMsgId = it!!.result!!.messageId })
				val comment = commentRepo.queryById(id)!!
				val fName = date("yyyy-MM-dd") + "-${comment.id}.txt"
				val fPath = "people/${comment.personId}/comments/${fName}"
				val cMsg = "[+] Comment added by ${comment.submitter} for ${comment.personId}"
				val url = commitDirectly(comment.submitter, DataEdit(fPath, comment.content), cMsg)
				bot.deleteMessage(chatId, statusMsgId)

				// Update database
				comment.approved = true
				commentRepo.save(comment)

				// Attach URL
				bot.editMessageText(chatId, msgId, inlId, "$message\n- 已通过审核✅", replyMarkup =
					InlineKeyboardMarkup.createSingleRowKeyboard(
						InlineKeyboardButton.Url(
							text = "查看 Commit",
							url = url
						)
					)
				)
			}
			else bot.editMessageText(chatId, msgId, inlId, "$message\n- 已删除❌")
		}

		// Create bot
		bot = bot {
			token = secrets.telegramBotToken
			dispatch {
				logLevel = LogLevel.Error
				command("start") { bot.sendMessage(ChatId.fromId(message.chat.id), "🐱") }
				callbackQuery("comment-pass", commentCallback)
				callbackQuery("comment-reject", commentCallback)
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
