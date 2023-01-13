package org.hydev.back.ai

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponse
import org.hydev.back.secrets

/**
 * Use HyDEV's external service for classifying harm level, since the deployed server doesn't have a GPU.
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2023-01-13
 */
class UrlHarmClassifier : IHarmClassifier
{
    override suspend fun classify(text: String): HarmLevel?
    {
        if (secrets.harmClassifierUrl == null || secrets.harmClassifierToken == null)
            return null

        return try {
            val (req, resp, result) = Fuel.post(secrets.harmClassifierUrl)
                .header("token", secrets.harmClassifierToken)
                .body(text)
                .awaitStringResponse()
            HarmLevel.valueOf(result.uppercase())
        }
        catch (e: Exception) {
            if (e !is IllegalArgumentException) e.printStackTrace()
            HarmLevel.INVALID
        }
    }
}
