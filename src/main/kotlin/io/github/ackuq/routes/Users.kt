package io.github.ackuq.routes

import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.controllers.UserController
import io.github.ackuq.models.*
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*


fun Route.users() {
    authenticate("admin") {
        route("") {
            get {
                val users = UserController.getAllUsers()
                handleApiSuccess(users.map { it.toUserDTO() }, HttpStatusCode.OK, call)
            }
        }
    }
}

fun Route.user() {
    authenticate {
        route("/{uuid}") {
            get {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val uuid = call.parameters["uuid"] ?: throw BadRequestException("No UUID specified")
                if (user.uuid.toString() != uuid && user.role != Role.Admin) {
                    throw AuthorizationException("Not authorized to see this page")
                } else {
                    val uuidUser = UserController.getUserByUUID(uuid)
                    handleApiSuccess(uuidUser.toUserDTO(), HttpStatusCode.OK, call)
                }
            }
        }
    }
}

fun Route.me() {
    authenticate {
        route("/me") {
            get {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                handleApiSuccess(user.toUserDTO(), HttpStatusCode.OK, call)
            }
            put {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val updateUser = call.receive<UpdateUserDTO>()
                val newUser = UserController.updateUser(updateUser, user)
                handleApiSuccess(newUser.toUserDTO(), HttpStatusCode.OK, call)
            }
        }
    }
}


fun Application.usersRoutes() {
    routing {
        route("api/v1/users") {
            users()
            user()
            me()
        }
    }
}