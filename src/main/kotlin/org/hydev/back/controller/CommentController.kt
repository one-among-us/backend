@file:Suppress("NAME_SHADOWING")

package org.hydev.back.controller

import com.github.kotlintelegrambot.dispatcher.handlers.HandleCallbackQuery
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hydev.back.*
import org.hydev.back.db.Ban
import org.hydev.back.db.BanRepo
import org.hydev.back.db.PendingComment
import org.hydev.back.db.PendingCommentRepo
import org.hydev.back.geoip.GeoIP
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.Date
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = ["*"])
class CommentController(
    private val commentRepo: PendingCommentRepo,
    private val banRepo: BanRepo,
    private val geoIP: GeoIP
) {

    val replyMarkup = InlineKeyboardMarkup.createSingleRowKeyboard(
        InlineKeyboardButton.CallbackData(
            text = "通过",
            callbackData = "comment-pass"
        ),
        InlineKeyboardButton.CallbackData(
            text = "Spoiler 后通过",
            callbackData = "comment-pass-spoiler"
        ),
        InlineKeyboardButton.CallbackData(
            text = "忽略",
            callbackData = "comment-reject"
        ),
        InlineKeyboardButton.CallbackData(
            text = "封禁 IP",
            callbackData = "comment-ban"
        )
    )

    val commentCallback: HandleCallbackQuery = callback@ {
        val pass = callbackQuery.data.startsWith("comment-pass")
        val chatId = ChatId.fromId(callbackQuery.message!!.chat.id)
        val msgId = callbackQuery.message!!.messageId
        val inlId = callbackQuery.inlineMessageId
        val message = callbackQuery.message!!.text!!
        val id = message.split(" ")[0].substring(1).toLong()

        bot.editMessageReplyMarkup(chatId, msgId, inlId, null)

        // Ban ip
        if (callbackQuery.data == "comment-ban")
        {
            val ip = commentRepo.queryById(id)!!.ip
            val entry = Ban(ip = ip, reason = "Bad comment #$id")
            banRepo.save(entry)
            bot.editMessageText(chatId, msgId, inlId, "$id - 已封禁 $ip")
            return@callback
        }

        // Rejected, remove
        if (!pass)
        {
            println("[-] Comment rejected! Comment $id deleted.")
            bot.editMessageText(chatId, msgId, inlId, "$message\n- 已删除❌")
            return@callback
        }

        // Commit changes
        var statusMsgId = 0L
        bot.sendMessage(chatId, "正在提交更改...").fold(
            { statusMsgId = it.messageId },
            { System.err.println("> Failed to send submission message: $it") })
        val comment = commentRepo.queryById(id)!!

        // Spoiler
        if (callbackQuery.data == "comment-pass-spoiler")
            comment.content = "||${comment.content.replace('\n', ' ')}||"

        // Create commit content
        val fPath = "people/${comment.personId}/comments/${date("yyyy-MM-dd")}-C${comment.id}.json"
        val cMsg = "[+] Comment added by ${comment.submitter} for ${comment.personId}"
        val content = json("id" to comment.id, "content" to comment.content,
            "submitter" to comment.submitter, "date" to comment.date)
        println("[+] Comment approved. Adding Comment $id: $content")

        // Write commit
        val url = commitDirectly(comment.submitter, DataEdit(fPath, content), cMsg)
        bot.deleteMessage(chatId, statusMsgId)

        // Update database
        comment.approved = true
        commentRepo.save(comment)

        // Attach URL
        bot.editMessageText(chatId, msgId, inlId, "$message\n- 已通过审核✅", replyMarkup =
            InlineKeyboardMarkup.createSingleRowKeyboard(
                InlineKeyboardButton.Url(text = "查看 Commit", url = url)
            )
        )
    }

    @PostMapping("/add")
    suspend fun addComment(
        @P id: str, @P content: str, @P captcha: str, @P name: str, @P email: str,
        request: HttpServletRequest
    ): Any
    {
        val ip = request.getIP()
        println("""
[+] Comment received. 
> IP: $ip
> ID: $id
> Name: $name
> Email: $email
> Content: $content
<< EOF >>""")

        // Verify captcha
        if (!verifyCaptcha(secrets.recaptchaSecret, captcha))
        {
            println("> Rejected: Cannot verify captcha")
            return "没有查到验证码".http(400)
        }

        // TODO: Check if id exists
        val name = name.ifBlank { "Anonymous" }
        val email = if (email.isBlank() || !email.isValidEmail())
            "anonymous@example.com" else email

        // Add to database
        val comment = withContext(Dispatchers.IO) {
            commentRepo.save(PendingComment(
                personId = id,
                content = content,
                submitter = name,
                email = email,
                date = Date(java.util.Date().time),
                ip = ip
            ))
        }

        var notif = """
#${comment.id} - $id 收到了新的留言：

$content

- IP: $ip"""

        if (name != "Anonymous")
            notif += "\n- 姓名: $name"
        if (email != "anonymous@example.com")
            notif += "\n- 邮箱: $email"
        geoIP.info(ip)?.let { notif += "\n$it" }

        // Check if ip is banned. If it is, send it to the blocked chat instead.
        val ban = banRepo.queryByIp(ip)
        val chatId = if (ban == null) secrets.telegramChatID else
        {
            notif += "\n- ❌ IP 已被封禁！"
            secrets.telegramBlockedChatID
        }

        // Send message on telegram
        bot.sendMessage(ChatId.fromId(chatId), notif, replyMarkup = replyMarkup)

        // Print log
        println("> Accepted, added to database.")
        println(notif)

        return "Success"
    }
}
