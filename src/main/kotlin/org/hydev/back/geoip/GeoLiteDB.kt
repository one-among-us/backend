package org.hydev.back.geoip

import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.record.Country
import com.maxmind.geoip2.record.Traits
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hydev.back.countryCodeToEmoji
import org.hydev.back.div
import org.hydev.back.downloadFromUrl
import org.kohsuke.github.GitHub
import org.kohsuke.github.GitHubBuilder
import java.io.File
import java.net.InetAddress

/**
 * TODO: Write a description for this class!
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2023-01-07
 */
class GeoLiteDB(
    val baseDir: File = GeoIP.DEFAULT_PATH
) : IGeoDB
{
    val dbTime get() = baseDir / "GeoLite2.last_update"
    val dbFile get() = baseDir / "GeoLite2-City.mmdb"

    var db: DatabaseReader? = null

    private var lastCheck: Long = 0
    private val checkInterval: Long = 1000 * 60 * 60 * 12
    private val github: GitHub = GitHubBuilder().build()

    fun shouldUpdate(): Boolean
    {
        if (!dbTime.isFile || !dbFile.isFile) return true

        // Only check GitHub updates every 12 hours
        if (System.currentTimeMillis() - lastCheck < checkInterval) return false
        lastCheck = System.currentTimeMillis()

        // Check the latest release date
        val time = dbTime.readText().trim().toLong()
        val release = github.getRepository("P3TERX/GeoLite.mmdb").latestRelease
        return release.createdAt.time > time
    }

    override suspend fun update()
    {
        if (shouldUpdate())
        {
            // Download database file
            val release = github.getRepository("P3TERX/GeoLite.mmdb").latestRelease
            val asset = release.listAssets().find { it.name.equals("GeoLite2-City.mmdb") }!!
            dbFile.downloadFromUrl(asset.browserDownloadUrl.toString())
            dbTime.writeText(release.createdAt.time.toString())
        }

        db = DatabaseReader.Builder(dbFile).build()
    }

    private fun Traits.string(): String?
    {
        val flags = mutableListOf<String>()

        if (isp != null) flags.add("ISP: $isp")
        if (domain != null) flags.add("Domain: $domain")
        if (organization != null) flags.add("Org: $organization")
        if (userType != null) flags.add("UserType: $userType")

        if (isAnonymous) flags.add("Anonymous")
        if (isAnonymousVpn) flags.add("VPN")
        if (isHostingProvider) flags.add("Hosting")
        if (isLegitimateProxy || isPublicProxy || isResidentialProxy) flags.add("Proxy")
        if (isTorExitNode) flags.add("Tor")

        return if (flags.isNotEmpty()) flags.joinToString(", ", "(", ")") else null
    }

    private fun Country.string() =
        listOf(isoCode?.countryCodeToEmoji(), name).mapNotNull { it }.ifEmpty { null }?.joinToString(" ")

    override suspend fun info(ip: String): String
    {
        update()

        val ip = withContext(Dispatchers.IO) {
            InetAddress.getByName(ip)
        }
        val r = db!!.city(ip)
        val results = listOf(
            r.country?.string()
        ) + (r.subdivisions?.map { it.name } ?: emptyList()) + listOf(
            r.city?.name,
            r.postal?.code,
            r.traits?.string()
        ).mapNotNull { it }

        return results.joinToString(" | ")
    }
}
