package org.hydev.back.controller

import org.hydev.back.H
import org.hydev.back.db.Flower
import org.hydev.back.db.FlowerRepo
import org.hydev.back.str
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/flowers")
class FlowerController(
    private val flowerRepo: FlowerRepo
)
{
    @GetMapping("/get")
    fun get(@H id: str): Any
    {
        val flower = flowerRepo.queryByPersonId(id) ?: return "None"
        return flower.flowers
    }

    @GetMapping("/give")
    fun give(@H id: str): Any
    {
        var flower = flowerRepo.queryByPersonId(id)
        if (flower == null)
        {
            flower = Flower(personId = id, flowers = 1)
            flowerRepo.save(flower)
        }
        else
        {
            flower.flowers += 1
            flowerRepo.save(flower)
        }
        return "{}"
    }
}
