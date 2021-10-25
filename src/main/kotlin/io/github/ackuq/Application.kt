package io.github.ackuq

import io.bkbn.kompendium.Kompendium
import io.bkbn.kompendium.models.oas.OpenApiSpecInfo
import io.bkbn.kompendium.models.oas.OpenApiSpecInfoLicense
import io.bkbn.kompendium.routes.openApi
import io.bkbn.kompendium.routes.redoc
import io.github.ackuq.conf.*
import io.github.ackuq.routes.authenticationRoutes
import io.github.ackuq.routes.usersRoutes
import io.github.ackuq.routes.wishListRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(databaseFactory: IDatabaseFactory = DatabaseFactory) {
    install(ContentNegotiation) {
        json()
    }
    install(AutoHeadResponse)
    configureCORS()
    configureJWT()
    configureStatusPages()

    databaseFactory.init()

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
    servers = mutableListOf(
    )
)
