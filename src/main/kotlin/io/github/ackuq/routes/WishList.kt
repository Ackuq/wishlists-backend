package io.github.ackuq.routes

import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.controllers.WishListController
import io.github.ackuq.models.NewWishListPayload
import io.github.ackuq.models.User
import io.github.ackuq.models.toWishListDTO
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.routing.*

fun Route.wishLists() {
    authenticate {
        route("") {
            get {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val wishLists = WishListController.getUserWishLists(user)
                handleApiSuccess(wishLists.map { it.toWishListDTO() }, HttpStatusCode.OK, call)
            }
            post {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val newWishListPayload = NewWishListPayload(user.uuid.toString())
                val wishList = WishListController.createWishList(newWishListPayload)
                handleApiSuccess(wishList.toWishListDTO(), HttpStatusCode.Created, call)
            }
        }
    }
}

fun Application.wishListRoutes() {
    routing {
        route("/api/v1/wish-list") {
            wishLists()
        }
    }
}