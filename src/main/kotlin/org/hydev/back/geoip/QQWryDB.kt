package org.hydev.back.geoip

import com.github.jarod.qqwry.QQWry
import org.hydev.back.div
import org.hydev.back.downloadFromUrl
import java.io.File

/**
 * TODO: Write a description for this class!
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2023-01-07
 */
class QQWryDB(
    val baseDir: File = GeoIP.DEFAULT_PATH,
    val minDt: Long = 1000 * 60 * 60 * 24 * 7   // 1 week
) : IGeoDB
{
    val dbTime get() = baseDir / "qqwry.last_update"
    val dbFile get() = baseDir / "qqwry.dat"

    var db: QQWry? = null

    fun shouldUpdate(): Boolean
    {
        if (!dbTime.isFile || !dbFile.isFile) return true

        // Check if the time interval is long enough
        val time = dbTime.readText().trim().toLong()
        val dt = System.currentTimeMillis() - time
        return dt > minDt
    }

    override suspend fun update()
    {
        if (shouldUpdate())
        {
            // Download database file
            dbFile.downloadFromUrl("https://github.com/metowolf/qqwry.dat/releases/latest/download/qqwry.dat")
            dbTime.writeText(System.currentTimeMillis().toString())
        }

        db = QQWry(dbFile.toPath())
    }

    override suspend fun info(ip: String): String
    {
        update()

        return db!!.findIP(ip).run { mainInfo +
            if (subInfo != null && subInfo.lowercase() != "cz88.net") " $subInfo" else "" }
    }
}
