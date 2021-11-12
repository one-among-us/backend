package org.hydev

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.hydev.plugins.*

fun main()
{
    embeddedServer(Netty, port = 43482, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}
