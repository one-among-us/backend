package org.hydev.back

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.responseObject
import org.springframework.web.util.UriComponentsBuilder

data class CaptchaResponse(val success: bool)

fun verifyCaptcha(secret: str, response: str?): bool
{
    if (response == null) return false

    val url = UriComponentsBuilder
        .fromUriString("https://www.recaptcha.net/recaptcha/api/siteverify")
        .queryParam("secret", secret)
        .queryParam("response", response)
        .build().toUri()

    val (req, res, result) = "https://www.recaptcha.net/recaptcha/api/siteverify"
        .httpGet(listOf("secret" to secret, "response" to response))
        .responseObject<CaptchaResponse>()

    return result.get().success
}
