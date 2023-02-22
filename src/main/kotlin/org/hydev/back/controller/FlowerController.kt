package org.hydev.back.controller

import org.hydev.back.*
import org.hydev.back.db.Flower
import org.hydev.back.db.FlowerRepo
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/flowers")
@CrossOrigin(origins = ["*"])
class FlowerController(
    private val flowerRepo: FlowerRepo
)
{
    private final val LIMIT = 5
    private lateinit var day: String
    private lateinit var dayLimit: HashMap<String, Int>

    init
    {
        initLimit()
    }

    final fun initLimit()
    {
        day = today().yyyymmdd()
        dayLimit = HashMap()
    }

    /**
     * Returns true if limit is reached
     */
    final fun checkLimit(token: str): bool
    {
        // Check whether limiter needs an update
        if (day != today().yyyymmdd()) initLimit()

        // Check limit
        dayLimit[token]?.let { if (it >= LIMIT) return true }
        dayLimit[token] = (dayLimit[token] ?: 0) + 1
        return false
    }

    @GetMapping("/get")
    fun get(@P id: str): Any
    {
        val id = id.lowercase()

        val flower = flowerRepo.queryByPersonId(id) ?: return "0"
        return flower.flowers
    }

    @GetMapping("/give")
    fun give(@P id: str, request: HttpServletRequest): Any
    {
        val id = id.lowercase()
        val ip = request.getIP()

        if (checkLimit("$id|$ip"))
        {
            println("[x] Flower rejected for $id by $ip: Daily limit reached")
            return "Limit reached"
        }
        println("[+] Flower added for $id by $ip")

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
        return ""
    }
}
