package io.github.ackuq

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.github.ackuq.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSockets()
        configureSerialization()
        configureHTTP()
        configureSecurity()
    }.start(wait = true)
}
