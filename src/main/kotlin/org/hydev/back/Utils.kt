package org.hydev.back

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.responseObject
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
    val recaptchaSecret: str
)

fun getSecretFromEnv(): Secrets?
{
    if (getenv("github-token") == null) return null
    return Secrets(getenv("github-token"), getenv("github-repo"), getenv("recaptcha-secret"))
}

fun getSecretsFromFile(): Secrets?
{
    val file = File("./secrets/secrets.txt")
    if (!file.exists() || !file.isFile) return null
    val text = file.readText()
    val lines = text.replace("\r\n", "\n").split("\n")
    return Secrets(lines[0], lines[1], lines[2])
}

fun getSecrets(): Secrets
{
    val secrets = getSecretsFromFile() ?: getSecretFromEnv()
    if (secrets == null)
    {
        val dir = File("./secrets/github.txt").absolutePath
        throw RuntimeException("No secrets defined in $dir or in environment variables")
    }
    return secrets
}
