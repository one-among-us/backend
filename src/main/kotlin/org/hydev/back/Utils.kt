package org.hydev.back

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.responseObject
import com.google.gson.Gson
import java.io.File
import javax.servlet.http.HttpServletRequest

data class CaptchaResponse(val success: bool)

/**
 * Verify captcha from a user
 *
 * @param secret String
 * @param response String?
 * @return bool
 */
fun verifyCaptcha(secret: str, response: str?): bool
{
    if (response == null) return false

    val (_, _, result) = "https://www.recaptcha.net/recaptcha/api/siteverify"
        .httpGet(listOf("secret" to secret, "response" to response))
        .responseObject<CaptchaResponse>()

    return result.get().success
}

data class Secrets(
    val githubToken: str,
    val githubRepo: str,
    val recaptchaSecret: str,
    val telegramBotToken: str,
    val telegramChatID: int,
    val telegramBlockedChatID: int,
    val harmClassifierUrl: str?,
    val harmClassifierToken: str?
)

fun getSecrets(): Secrets
{
    val file = File("./secrets/secrets.json")
    if (!file.exists() || !file.isFile)
    {
        val dir = File("./secrets/secrets.json").absolutePath
        throw RuntimeException("No secrets defined in $dir")
    }
    return Gson().fromJson(file.readText(), Secrets::class.java)
}

fun HttpServletRequest.getIP(): str = getHeader("CF-Connecting-IP") ?: getHeader("X-Forwarded-For") ?:
    getHeader("X-Real-IP") ?: remoteAddr
