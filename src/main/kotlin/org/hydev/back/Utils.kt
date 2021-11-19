package org.hydev.back

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.responseObject

data class CaptchaResponse(val success: bool)

fun verifyCaptcha(secret: str, response: str?): bool
{
    if (response == null) return false

    val (req, res, result) = "https://www.recaptcha.net/recaptcha/api/siteverify"
        .httpGet(listOf("secret" to secret, "response" to response))
        .responseObject<CaptchaResponse>()

    return result.get().success
}
