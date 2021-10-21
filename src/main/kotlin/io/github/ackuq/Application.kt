package io.github.ackuq

import io.github.ackuq.conf.*
import io.github.ackuq.routes.authenticationRoutes
import io.github.ackuq.routes.usersRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(databaseFactory: IDatabaseFactory = DatabaseFactory) {
    install(ContentNegotiation) {
        json()
    }
    configureCORS()
    configureJWT()
    configureStatusPages()

    databaseFactory.init()

    routing {
        authenticationRoutes()
        usersRoutes()
    }
}


