package org.hydev.back

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.text.SimpleDateFormat
import java.util.*

typealias P = RequestParam
typealias B = RequestBody
typealias H = RequestHeader

// Python aliases
typealias str = String
typealias int = Long
typealias list<T> = ArrayList<T>
typealias bool = Boolean

fun <T> T.http(code: Int): ResponseEntity<T> = ResponseEntity.status(code).body<T>(this)
fun date(f: str = "yyyy-MM-dd"): str = SimpleDateFormat(f).format(Date())

// https://www.baeldung.com/java-email-validation-regex
val emailRegex = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$".toRegex()

/**
 * Check if the string is a valid email
 *
 * @receiver str
 * @return bool
 */
fun str.isValidEmail(): bool = emailRegex.matches(this)
