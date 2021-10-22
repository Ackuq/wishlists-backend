package io.github.ackuq.routes

import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.dao.User
import io.github.ackuq.services.WishListService
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*

fun Route.wishLists() {
    authenticate {
        route("") {
            get {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val wishLists = WishListService.getUsersLists(user)
                handleApiSuccess(wishLists.map { it.toDTO() }, HttpStatusCode.OK, call)
            }
            post {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val wishList = WishListService.createWishList(user)
                handleApiSuccess(wishList.toDTO(), HttpStatusCode.Created, call)
            }
        }
    }
}

fun Route.wishList() {
    authenticate {
        route("/{id}") {
            get {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("No id specified")
                val wishList = WishListService.getWishList(user, id)
                handleApiSuccess(wishList.toDTO(), HttpStatusCode.OK, call)
            }
        }
    }
}

fun Application.wishListRoutes() {
    routing {
        route("/api/v1/wish-list") {
            wishLists()
            wishList()
        }
    }
}