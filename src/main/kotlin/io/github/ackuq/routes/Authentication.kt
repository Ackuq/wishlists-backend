package io.github.ackuq.routes

import io.github.ackuq.controllers.UserController
import io.github.ackuq.models.UserPayload
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.login() {
    route("/login") {
        post {
            val userPayload = call.receive<UserPayload>()
            val token = UserController.login(userPayload)
            application.log.debug("User ${userPayload.email} logged in")
            handleApiSuccess(token, HttpStatusCode.OK, call)
        }
    }
}

fun Route.register() {
    route("/register") {
        post {
            val userPayload = call.receive<UserPayload>()
            application.log.debug("Received register request from ${userPayload.email}")
            val token = UserController.register(userPayload)
            application.log.debug("Successfully registered user ${userPayload.email}")
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