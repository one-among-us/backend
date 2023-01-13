package org.hydev.back.ai

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponse
import org.hydev.back.secrets
import org.springframework.stereotype.Component

/**
 * Use HyDEV's external service for classifying harm level, since the deployed server doesn't have a GPU.
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2023-01-13
 */
@Component
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
            if (e is IllegalArgumentException) return HarmLevel.INVALID

            System.err.println("[AI] Error: Cannot connect to ${secrets.harmClassifierUrl}: ${e.message}")
            HarmLevel.OFFLINE
        }
    }
}

suspend fun main(args: Array<String>) {
    // HARMFUL
    println(UrlHarmClassifier().classify("你去死"))
    // SAFE
    println(UrlHarmClassifier().classify("猫猫很可爱"))
}
