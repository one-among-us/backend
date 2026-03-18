@file:Suppress("NAME_SHADOWING")

package org.hydev.back.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.github.kotlintelegrambot.entities.ChatId
import org.hydev.back.*
import org.hydev.back.ai.HarmLevel
import org.hydev.back.ai.IHarmClassifier
import org.hydev.back.db.BanRepo
import org.hydev.back.geoip.AcceptLanguage
import org.hydev.back.geoip.GeoIP
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/edit")
@CrossOrigin(origins = ["*"])
class EditController(
    private val banRepo: BanRepo,
    private val geoIP: GeoIP,
    private val harmClassifier: IHarmClassifier
)
{
    @PostMapping("/info")
    suspend fun get(@P id: str, @P content: str, @P captcha: str, @P name: str, @P email: str,
            request: HttpServletRequest): Any
    {
        val ip = request.getIP()
        println("""
[+] Info edit received. 
> IP: $ip
> ID: $id
> Name: $name
> Email: $email
> Content: $content
> Accept-Language: ${request.getHeader("accept-language")}
> User-Agent: ${request.getHeader("user-agent")}
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
        val obj = ObjectMapper().readTree(content)
        val yml = ObjectMapper(YAMLFactory()).writeValueAsString(obj)

        var notif = """
$id 收到了信息编辑请求：

$yml

- IP: $ip"""

        if (name != "Anonymous")
            notif += "\n- 姓名: $name"
        if (email != "anonymous@example.com")
            notif += "\n- 邮箱: $email"
        geoIP.info(ip)?.let { notif += "\n$it" }

        // Check if ip is banned. If it is, send it to the blocked chat instead.
        val ban = banRepo.queryByIp(ip)
        val chatId = if (ban != null) {
            notif += "\n- ❌ IP 已被封禁！"
            secrets.telegramBlockedChatID
        }
        else {
            // Check if AI think it's inappropriate
            val clas = harmClassifier.classify(content)
            clas?.msg?.let { notif += "\n- $it" }

            if (clas == HarmLevel.HARMFUL) secrets.telegramBlockedChatID
            else secrets.telegramChatID
        }

        request.getHeader("accept-language")?.let { notif += "\n- 请求语言: ${AcceptLanguage.parse(it)}" }
        request.getHeader("user-agent")?.let { notif += "\n- 浏览器: $it" }

        return try
        {
            bot.sendMessage(ChatId.fromId(chatId), notif, disableWebPagePreview = true)

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
