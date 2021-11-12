package org.hydev

import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main()
{
    startDatabase()
    println("Started")

    embeddedServer(Netty, port = 43482, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}
