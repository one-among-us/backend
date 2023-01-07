package org.hydev.back.geoip

/**
 * TODO: Write a description for this class!
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2023-01-07
 */
interface IGeoDB
{
    suspend fun update()

    suspend fun info(ip: String): String
}
