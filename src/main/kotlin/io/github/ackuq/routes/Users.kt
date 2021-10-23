package io.github.ackuq.routes

import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.dao.User
import io.github.ackuq.dto.Role
import io.github.ackuq.dto.UpdateUserDTO
import io.github.ackuq.services.UserService
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*
import java.util.*


fun Route.users() {
    authenticate("admin") {
        route("") {
            get {
                val users = UserService.getAllUsers()
                handleApiSuccess(users.map { it.toDTO() }, HttpStatusCode.OK, call)
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
                if (user.id.value.toString() != uuid && user.role != Role.Admin) {
                    throw AuthorizationException("Not authorized to see this page")
                } else {
                    val uuidUser =
                        UserService.getUserByUUID(UUID.fromString(uuid)) ?: throw NotFoundException("User not found")
                    handleApiSuccess(uuidUser.toDTO(), HttpStatusCode.OK, call)
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
                handleApiSuccess(user.toDTO(), HttpStatusCode.OK, call)
            }
            put {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val updateUser = call.receive<UpdateUserDTO>()
                val newUser = UserService.updateUser(user, updateUser)
                handleApiSuccess(newUser.toDTO(), HttpStatusCode.OK, call)
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