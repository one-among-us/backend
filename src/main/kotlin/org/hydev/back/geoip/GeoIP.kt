package org.hydev.back.geoip

import com.maxmind.geoip2.exception.AddressNotFoundException
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

        try {
            notif += "- GeoLite: ${geoLite.info(ip)}"
        }
        catch (_: AddressNotFoundException) {}
        catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            notif += "- QQWry: ${qqWry.info(ip)}"
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        return notif.ifEmpty { null }?.joinToString("\n")
    }

    companion object {
        val DEFAULT_PATH = File("data/geoip")
    }
}
