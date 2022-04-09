@file:Suppress("NAME_SHADOWING")

package org.hydev.back.controller

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
    fun get(@P id: str, @P content: str, @P captcha: str, @P name: str, @P email: str,
            request: HttpServletRequest): Any
    {
        // Verify captcha
        if (!verifyCaptcha(secrets.recaptchaSecret, captcha))
            return "没有查到验证码".http(400)

        // TODO: Check if id exists
        val name = name.ifBlank { "Anonymous" } + " ${request.remoteAddr}"
        val email = if (email.isBlank() || !email.isValidEmail())
            "anonymous@example.com" else email

        return try
        {
            createPullRequest(name, email,
                arrayListOf(DataEdit("people/$id/info.json5", content)))
        }
        catch (e: Exception) { "创建更改请求失败（${e.message}）".http(500) }
    }
}
