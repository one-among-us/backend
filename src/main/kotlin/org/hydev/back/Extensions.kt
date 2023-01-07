package org.hydev.back

import com.github.kittinunf.fuel.Fuel
import com.google.gson.Gson
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

typealias P = RequestParam
typealias B = RequestBody
typealias H = RequestHeader

// Python aliases
typealias str = String
typealias int = Long
typealias list<T> = ArrayList<T>
typealias bool = Boolean

val gson = Gson()

fun <T> T.http(code: Int): ResponseEntity<T> = ResponseEntity.status(code).body<T>(this)
fun date(f: str = "yyyy-MM-dd"): str = SimpleDateFormat(f).format(Date())
fun <K, V> json(vararg pairs: Pair<K, V>): str = gson.toJson(mapOf(*pairs))

// https://www.baeldung.com/java-email-validation-regex
val emailRegex = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$".toRegex()

/**
 * Check if the string is a valid email
 *
 * @receiver str
 * @return bool
 */
fun str.isValidEmail(): bool = emailRegex.matches(this)

operator fun File.div(s: String) = File(this, s)
fun File.ensureParents() = apply { parentFile.mkdirs() }

suspend fun File.downloadFromUrl(url: String) = suspendCoroutine { cont ->
    val outStream = FileOutputStream(this.ensureParents())

    Fuel.download(url)
        .streamDestination { _, req -> Pair(outStream) { req.body.toStream() } }
        .progress { readBytes, totalBytes ->
            val progress = readBytes.toFloat() / totalBytes.toFloat() * 100
            print("\rDownloading $name: $readBytes / $totalBytes ($progress %)")
        }
        .response { result -> result.fold(
            success = { println(); cont.resume(it) },
            failure = { cont.resumeWithException(it) })
        }
}

fun String.countryCodeToEmoji(): String
{
    val flagOffset = 0x1F1E6
    val asciiOffset = 0x41

    val firstChar = this.codePointAt(0) - asciiOffset + flagOffset
    val secondChar = this.codePointAt(1) - asciiOffset + flagOffset

    return String(Character.toChars(firstChar) + Character.toChars(secondChar))
}
