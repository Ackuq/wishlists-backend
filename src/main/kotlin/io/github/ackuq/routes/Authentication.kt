package io.github.ackuq.routes

import io.bkbn.kompendium.Notarized.notarizedPost
import io.github.ackuq.conf.JwtConfig
import io.github.ackuq.dto.UserCredentialsDTO
import io.github.ackuq.utils.SimpleResponseInfo
import io.github.ackuq.utils.handleApiSuccess
import io.github.ackuq.utils.postInfo
import io.ktor.application.Application
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.features.BadRequestException
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.routing

fun Route.login() {
    route("/login") {
        notarizedPost(
            postInfo(
                "Login",
                "Login new user",
                UserCredentialsDTO("test@testsson.com", "password"),
                SimpleResponseInfo(
                    HttpStatusCode.Created,
                    "JWT_TOKEN"
                ),
                setOf(BadRequestException::class, NotFoundException::class)
            )
        ) {
            val userCredentials = call.receive<UserCredentialsDTO>()
            val token = JwtConfig.loginUser(userCredentials)
            application.log.debug("User ${userCredentials.email} logged in")
            handleApiSuccess(token, HttpStatusCode.OK, call)
        }
    }
}

fun Route.register() {
    route("/register") {
        notarizedPost(
            postInfo(
                "Register",
                "Register new customer",
                UserCredentialsDTO("test@testsson.com", "password"),
                SimpleResponseInfo(
                    HttpStatusCode.Created,
                    "JWT_TOKEN"
                ),
                setOf(BadRequestException::class)
            )
        ) {
            val userCredentials = call.receive<UserCredentialsDTO>()
            val token = JwtConfig.registerCustomer(userCredentials)
            application.log.debug("Registered new user ${userCredentials.email}")
            handleApiSuccess(token, HttpStatusCode.Created, call)
        }
    }
}

fun Application.authenticationRoutes() {
    routing {
        route("api/v1/auth") {
            login()
            register()
        }
    }
}
