package io.github.ackuq.routes

import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.controllers.UserController
import io.github.ackuq.models.UserPayload
import io.github.ackuq.services.UserService
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*
import java.util.*

fun Application.authenticationRoutes() {


    routing {
        route("/auth") {
            route("/login/") {
                post {
                    val userPayload = call.receive<UserPayload>()
                    val token = UserController.login(userPayload)
                    handleApiSuccess(token, HttpStatusCode.OK, call)
                }
            }
            route("/register/") {
                post {
                    val newUser = call.receive<UserPayload>()
                    val token = UserController.register(newUser)
                    handleApiSuccess(token, HttpStatusCode.Created, call)
                }
            }
        }
    }
}