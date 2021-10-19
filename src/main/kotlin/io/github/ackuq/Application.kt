package io.github.ackuq

import io.github.ackuq.conf.DatabaseFactory
import io.github.ackuq.conf.configureCORS
import io.github.ackuq.conf.configureJWT
import io.github.ackuq.routes.authenticationRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    configureCORS()
    configureJWT()

    DatabaseFactory.init()

    routing {
        authenticationRoutes()
    }
}


