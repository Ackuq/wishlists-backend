package io.github.ackuq.routes

import io.bkbn.kompendium.auth.Notarized.notarizedAuthenticate
import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.conf.SecurityConfigurations
import io.github.ackuq.dao.User
import io.github.ackuq.dto.CreateWishListPayload
import io.github.ackuq.dto.CreateWishListProductPayload
import io.github.ackuq.dto.EditWishListPayload
import io.github.ackuq.dto.EditWishListProductPayload
import io.github.ackuq.services.WishListProductService
import io.github.ackuq.services.WishListService
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.features.BadRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.routing.routing

fun Route.wishLists() {
    notarizedAuthenticate(SecurityConfigurations.default) {
        route("") {
            get {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val wishLists = WishListService.getUsersLists(user)
                handleApiSuccess(wishLists.map { it.toDTO() }, HttpStatusCode.OK, call)
            }
            post {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val createPayLoad = call.receive<CreateWishListPayload>()
                val wishList = WishListService.createWishList(user, createPayLoad)
                handleApiSuccess(wishList.toDTO(), HttpStatusCode.Created, call)
            }
        }
    }
}

fun Route.wishListProduct() {
    notarizedAuthenticate(SecurityConfigurations.default) {
        route("/product/{productId}") {
            get {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val productId =
                    call.parameters["productId"]?.toIntOrNull() ?: throw BadRequestException("No product id specified")
                val product = WishListProductService.getWishListProduct(user, productId)
                handleApiSuccess(product.toDTO(), HttpStatusCode.OK, call)
            }
            delete {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val productId =
                    call.parameters["productId"]?.toIntOrNull() ?: throw BadRequestException("No product id specified")
                WishListProductService.deleteWishListProduct(user, productId)
                handleApiSuccess("Successfully deleted product", HttpStatusCode.OK, call)
            }
            put {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val productId =
                    call.parameters["productId"]?.toIntOrNull() ?: throw BadRequestException("No product id specified")
                val editWishListProductPayload = call.receive<EditWishListProductPayload>()
                val product =
                    WishListProductService.editWishListProduct(user, productId, editWishListProductPayload)
                handleApiSuccess(product.toDTO(), HttpStatusCode.Created, call)
            }
        }
    }
}

fun Route.wishList() {
    notarizedAuthenticate(SecurityConfigurations.default) {
        route("/{id}") {
            get {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("No id specified")
                val wishList = WishListService.getWishList(user, id)
                handleApiSuccess(wishList.toDTO(), HttpStatusCode.OK, call)
            }
            put {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("No id specified")
                val wishListPayload = call.receive<EditWishListPayload>()
                val wishList = WishListService.updateWishList(user, id, wishListPayload)
                handleApiSuccess(wishList.toDTO(), HttpStatusCode.OK, call)
            }
            delete {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("No id specified")
                WishListService.deleteWishList(user, id)
                handleApiSuccess("Successfully deleted", HttpStatusCode.OK, call)
            }
            post {
                val user = call.principal<User>() ?: throw AuthorizationException("Invalid credentials")
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("No id specified")
                val createProductPayload = call.receive<CreateWishListProductPayload>()
                val product = WishListService.addProductToWishList(user, id, createProductPayload)
                handleApiSuccess(product.toDTO(), HttpStatusCode.Created, call)
            }
        }
    }
}

fun Application.wishListRoutes() {
    routing {
        route("/api/v1/wish-list") {
            wishLists()
            wishList()
            wishListProduct()
        }
    }
}
