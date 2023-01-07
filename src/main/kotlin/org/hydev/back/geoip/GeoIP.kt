package org.hydev.back.geoip

import org.springframework.stereotype.Component
import java.io.File

@Component
class GeoIP
{
    private val geoLite = GeoLiteDB()
    private val qqWry = QQWryDB()

    suspend fun info(ip: String): String?
    {
        val notif = mutableListOf<String>()

        runCatching { geoLite.info(ip) }.onSuccess { notif += "- GeoLite: $it" }.onFailure { it.printStackTrace() }
        runCatching { qqWry.info(ip) }.onSuccess { notif += "- QQWry: $it" }.onFailure { it.printStackTrace() }

        return notif.ifEmpty { null }?.joinToString("\n")
    }

    companion object {
        val DEFAULT_PATH = File("data/geoip")
    }
}
