package io.github.ackuq.routes

import io.github.ackuq.controllers.UserController
import io.github.ackuq.models.UserCredentials
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.login() {
    route("/login") {
        post {
            val userCredentials = call.receive<UserCredentials>()
            val token = UserController.login(userCredentials)
            application.log.debug("User ${userCredentials.email} logged in")
            handleApiSuccess(token, HttpStatusCode.OK, call)
        }
    }
}

fun Route.register() {
    route("/register") {
        post {
            val userCredentials = call.receive<UserCredentials>()
            application.log.debug("Received register request from ${userCredentials.email}")
            val token = UserController.register(userCredentials)
            application.log.debug("Successfully registered user ${userCredentials.email}")
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