package io.github.ackuq.routes

import io.bkbn.kompendium.Notarized.notarizedPost
import io.github.ackuq.conf.JwtConfig
import io.github.ackuq.dto.UserCredentialsDTO
import io.github.ackuq.utils.SimpleResponseInfo
import io.github.ackuq.utils.handleApiSuccess
import io.github.ackuq.utils.postInfo
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*

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