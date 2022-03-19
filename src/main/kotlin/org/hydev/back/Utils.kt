package org.hydev.back

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.responseObject
import com.google.gson.Gson
import java.io.File
import java.lang.System.getenv

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

    val (req, res, result) = "https://www.recaptcha.net/recaptcha/api/siteverify"
        .httpGet(listOf("secret" to secret, "response" to response))
        .responseObject<CaptchaResponse>()

    return result.get().success
}

data class Secrets(
    val githubToken: str,
    val githubRepo: str,
    val recaptchaSecret: str,
    val telegramBotToken: str,
    val telegramChatID: int
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
