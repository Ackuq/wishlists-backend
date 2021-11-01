package io.github.ackuq

import io.bkbn.kompendium.Kompendium
import io.bkbn.kompendium.models.oas.OpenApiSpecInfo
import io.bkbn.kompendium.models.oas.OpenApiSpecInfoLicense
import io.bkbn.kompendium.routes.openApi
import io.bkbn.kompendium.routes.redoc
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
    configureCORS()
    configureJWT()
    configureStatusPages()

    databaseFactory.init()

    val setupDemo = environment.config.propertyOrNull("kotlin.setupDemo")?.getString().toBoolean()

    if (setupDemo) {
        setupDemo()
    }

    routing {
        openApi(oas)
        redoc(oas)
        authenticationRoutes()
        usersRoutes()
        wishListRoutes()
    }
}

val oas = Kompendium.openApiSpec.copy(
    info = OpenApiSpecInfo(
        title = "Wishlist API",
        version = "0.1.0",
        license = OpenApiSpecInfoLicense(
            name = "MIT",
        )
    ),
    servers = mutableListOf()
)
