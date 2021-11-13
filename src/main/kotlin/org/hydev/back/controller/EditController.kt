package org.hydev.back.controller

import org.hydev.back.DataEdit
import org.hydev.back.H
import org.hydev.back.createPullRequest
import org.hydev.back.str
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URLDecoder

@RestController
@RequestMapping("/edit")
@CrossOrigin(origins = ["*"])
class EditController
{
    @PostMapping("/info")
    fun get(@H id: str, @H content: str): Any
    {
        // TODO: Check if id exists
        val id = id.lowercase()
        return createPullRequest("Web User", "web@example.com",
            arrayListOf(DataEdit("people/$id/info.json5", URLDecoder.decode(content, "UTF-8"))))
    }
}
