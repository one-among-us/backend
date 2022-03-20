@file:Suppress("NAME_SHADOWING")

package org.hydev.back.controller

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import org.hydev.back.*
import org.hydev.back.db.FlowerRepo
import org.hydev.back.db.PendingComment
import org.hydev.back.db.PendingCommentRepo
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = ["*"])
class CommentController(private val commentRepo: PendingCommentRepo)
{
    val replyMarkup = InlineKeyboardMarkup.createSingleRowKeyboard(
        InlineKeyboardButton.CallbackData(
            text = "通过",
            callbackData = "comment-pass"
        ),
        InlineKeyboardButton.CallbackData(
            text = "删除",
            callbackData = "comment-reject"
        )
    )

    @PostMapping("/add")
    fun addComment(
        @H id: str, @H content: str, @H captcha: str, @H name: str, @H email: str,
        request: HttpServletRequest
    ): Any
    {
        // Verify captcha
        //        if (!verifyCaptcha(secrets.recaptchaSecret, captcha.dec()))
        //            return "没有查到验证码".http(400)

        // TODO: Check if id exists
        val content = content.dec()
        val id = id.dec()
        val name = name.dec().ifBlank { "Anonymous" }
        val email = if (email.isBlank() || !email.isValidEmail())
            "anonymous@example.com" else email

        // Add to database
        val comment = commentRepo.save(PendingComment(0, id, content, name, email))

        // Send message on telegram
        bot.sendMessage(ChatId.fromId(secrets.telegramChatID), """
            #${comment.id} - $id 收到了新的留言：
            
            $content
            
            - IP: ${request.getIP()}
            - 姓名: $name
            - 邮箱: $email
        """.trimIndent(), replyMarkup = replyMarkup)

        return "Success"
    }
}
