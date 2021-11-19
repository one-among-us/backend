package org.hydev.back

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLDecoder
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

/**
 * URL Decode a string
 *
 * @receiver str Url-encoded string
 * @return String Decoded string
 */
fun str.dec(): String = URLDecoder.decode(this, "UTF-8")
