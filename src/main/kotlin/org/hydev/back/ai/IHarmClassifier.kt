package org.hydev.back.ai

/**
 * Interface for AI hate-speech/harmful language classifier
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2023-01-13
 */
interface IHarmClassifier
{
    suspend fun classify(text: String): HarmLevel?
}
