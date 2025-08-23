package org.hydev.back

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.github.kittinunf.fuel.Fuel
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.google.gson.Gson
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.io.File
import java.io.FileDescriptor.out
import java.io.FileOutputStream
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

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

fun <T> List<T>.slice(start: Int, end: Int? = null) = slice(start until (end ?: size))

fun String.countryCodeToEmoji(): String
{
    val flagOffset = 0x1F1E6
    val asciiOffset = 0x41

    val firstChar = this.codePointAt(0) - asciiOffset + flagOffset
    val secondChar = this.codePointAt(1) - asciiOffset + flagOffset

    return String(Character.toChars(firstChar) + Character.toChars(secondChar))
}

fun CommandHandlerEnvironment.reply(
    text: String,
    parseMode: ParseMode? = null,
    disableWebPagePreview: Boolean? = null,
    disableNotification: Boolean? = null,
    replyToMessageId: Long? = null,
    allowSendingWithoutReply: Boolean? = null,
    replyMarkup: ReplyMarkup? = null) = bot.sendMessage(ChatId.fromId(message.chat.id), text, parseMode, disableWebPagePreview, disableNotification, replyToMessageId, allowSendingWithoutReply, replyMarkup)


typealias CmdHandler = CommandHandlerEnvironment.() -> String?

fun Dispatcher.cmd(name: String, handler: CmdHandler) = command(name) {
    handler(this)?.let { reply(it) }
}

val dateFormat = SimpleDateFormat("yyyy-MM-dd")
fun Date.yyyymmdd() = dateFormat.format(this)
fun today() = Calendar.getInstance().time



infix fun <T> T.cons(other: T) = listOf(this, other)
infix fun <T, R> List<T>.fmap(f: (T) -> R) = this.map(f)
infix fun <T> List<T>.ffilter(f: (T) -> Boolean) = this.filter(f)
infix fun String.transform(t: String) = "$this -> $t"
infix fun String.check(l: Int) = this.length > l
infix fun PrintStream.println(s: Any) = println(s)

infix fun <T, R> T?.or(other: R?) = this?.left() ?: other?.right()

class Promise<T, E>(f: (((T) -> Unit), ((E) -> Unit)) -> Unit)
{
    var exc: Throwable? = null
    var result: T? = null
    var error: E? = null

    init {
        try {
            f({ result = it }, { error = it })
        } catch (e: Throwable) {
            exc = e
        }
    }

    fun then(f: (T) -> Unit) = also { result?.let(f) }
    fun catch(f: (Either<Throwable, E>) -> Unit) = also { (exc or error)?.let(f) }
    fun finally(f: () -> Unit) = also { f() }
}


fun main(args: Array<String>) {
    Promise { resolve, reject ->
        if (Random.nextBoolean()) resolve("Success")
        else reject("Failed")
    }.then {
        println(it)
    }.catch {
        println(it)
    }.finally {
        println("Done")
    }
}

