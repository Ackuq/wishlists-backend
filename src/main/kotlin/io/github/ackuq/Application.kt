package io.github.ackuq

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.github.ackuq.conf.DatabaseFactory
import io.github.ackuq.conf.IDatabaseFactory
import io.github.ackuq.conf.configureCORS
import io.github.ackuq.conf.configureJWT
import io.github.ackuq.conf.configureStatusPages
import io.github.ackuq.routes.authenticationRoutes
import io.github.ackuq.routes.usersRoutes
import io.github.ackuq.routes.wishListRoutes
import io.github.ackuq.utils.setupDemo
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.routing.routing
import io.ktor.serialization.json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(databaseFactory: IDatabaseFactory = DatabaseFactory) {
    install(ContentNegotiation) {
        json()
    }
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(Kompendium) {
        spec = oas
    }

    configureCORS()
    configureJWT()
    configureStatusPages()

    databaseFactory.init()

    val setupDemo = environment.config.propertyOrNull("kotlin.setupDemo")?.getString().toBoolean()

    if (setupDemo) {
        setupDemo()
    }

    routing {
        redoc(pageTitle = "Whishlist API")
        authenticationRoutes()
        usersRoutes()
        wishListRoutes()
    }
}

val oas = OpenApiSpec(
    info = Info(
        title = "Wishlist API",
        version = "0.1.0",
        license = License(
            name = "MIT",
        )
    ),
    servers = mutableListOf()
)
