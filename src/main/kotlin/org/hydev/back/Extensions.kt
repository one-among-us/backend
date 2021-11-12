package org.hydev.back

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam

typealias H = RequestParam

// Python aliases
typealias str = String
typealias int = Long
typealias list<T> = ArrayList<T>


fun <T> T.http(code: Int): ResponseEntity<T> = ResponseEntity.status(code).body<T>(this)
