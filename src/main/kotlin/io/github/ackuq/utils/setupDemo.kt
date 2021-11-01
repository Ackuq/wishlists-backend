package io.github.ackuq.utils

import io.github.ackuq.dto.CreateWishListPayload
import io.github.ackuq.dto.CreateWishListProductPayload
import io.github.ackuq.dto.Role
import io.github.ackuq.dto.UserCredentialsDTO
import io.github.ackuq.services.UserService
import io.github.ackuq.services.WishListService

val demoAdminCredentials = UserCredentialsDTO(email = "admin@wishlists.com", password = "password")
val demoCustomerCredentials = UserCredentialsDTO(email = "customer@wishlists.com", password = "password")

val demoWishlistPayload = CreateWishListPayload(title = "Demo wishlist")
val productPayload = CreateWishListProductPayload(title = "Demo product")

fun setupDemo() {
    // Setup admin and customer
    UserService.createUser(demoAdminCredentials, Role.Admin)
    val customerUser = UserService.createUser(demoCustomerCredentials, Role.Customer)

    // Setup wishlist
    val wishList = WishListService.createWishList(customerUser, demoWishlistPayload)
    WishListService.addProductToWishList(customerUser, wishList.id.value, productPayload)
}
