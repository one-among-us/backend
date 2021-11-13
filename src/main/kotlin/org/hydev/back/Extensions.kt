package org.hydev.back

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam
import java.text.SimpleDateFormat
import java.util.*

typealias H = RequestParam

// Python aliases
typealias str = String
typealias int = Long
typealias list<T> = ArrayList<T>


fun <T> T.http(code: Int): ResponseEntity<T> = ResponseEntity.status(code).body<T>(this)
fun date(f: str = "yyyy-MM-dd"): str = SimpleDateFormat(f).format(Date())
