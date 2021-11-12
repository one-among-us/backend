package org.hydev.back.controller

import org.hydev.back.H
import org.hydev.back.str
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/flowers")
class FlowerController
{
    @GetMapping("/get")
    fun get(@H username: str): Any
    {
        return "Not implemented"
    }
}
