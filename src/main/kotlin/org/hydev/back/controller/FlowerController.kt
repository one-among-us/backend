package org.hydev.back.controller

import org.hydev.back.H
import org.hydev.back.db.Flower
import org.hydev.back.db.FlowerRepo
import org.hydev.back.str
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/flowers")
@CrossOrigin(origins = ["*"])
class FlowerController(
    private val flowerRepo: FlowerRepo
)
{
    @GetMapping("/get")
    fun get(@H id: str): Any
    {
        val id = id.lowercase()

        val flower = flowerRepo.queryByPersonId(id) ?: return "0"
        return flower.flowers
    }

    @GetMapping("/give")
    fun give(@H id: str): Any
    {
        val id = id.lowercase()

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
