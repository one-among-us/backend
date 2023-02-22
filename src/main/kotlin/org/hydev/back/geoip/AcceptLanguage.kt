package org.hydev.back.geoip

/**
 * Utility for parsing accept-language
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2023-02-21
 */
data class AcceptLanguage(val lang: String, val priority: Double?)
{
    companion object
    {
        private val RE = Regex("([\\w-]+)(;q=([\\d.]+))?")

        fun parse(raw: String) = runCatching { RE.findAll(raw)
            .map { AcceptLanguage(it.groupValues[1], it.groupValues[3].toDoubleOrNull()) }.toList().joinToString(", ") }
            .getOrElse { raw }
    }

    override fun toString() = this.lang + (this.priority?.let { " ($it)" } ?: "")
}

fun main(args: Array<String>)
{
    println(AcceptLanguage.parse("en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7"))
}
