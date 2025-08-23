@file:Suppress("NAME_SHADOWING")

package org.hydev.back.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.github.kotlintelegrambot.entities.ChatId
import org.hydev.back.*
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/edit")
@CrossOrigin(origins = ["*"])
class EditController
{
    @PostMapping("/info")
    fun get(@P id: str, @P content: str?, @P captcha: str, @P name: str, @P email: str,
            request: HttpServletRequest): Any
    {
        val text = content ?: request.reader.readText()
        if (text.isBlank()) return "没有收到内容".http(400)

        val ip = request.getIP()
        println("""
[+] Info edit received. 
> IP: $ip
> ID: $id
> Name: $name
> Email: $email
> Content: $text
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

        // Convert json to yml
        val obj = ObjectMapper().readTree(text)
        val yml = ObjectMapper(YAMLFactory()).writeValueAsString(obj)

        val notif = """
$id 收到了信息编辑请求：

$yml

- IP: $ip
- 姓名: $name
- 邮箱: $email"""

        return try
        {
            bot.sendMessage(ChatId.fromId(secrets.telegramChatID), notif, disableWebPagePreview = true)

            // This fails for some reason:
            // createPullRequest(name, email,
            //     arrayListOf(DataEdit("people/$id/info.json5", content)))

            "Success".http(200)
        }
        catch (e: Exception) {
            println("> Error: ${e.message}")
            e.printStackTrace()

            "创建更改请求失败（${e.message}）".http(500)
        }
    }
}
