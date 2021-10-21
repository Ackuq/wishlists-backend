package io.github.ackuq.controllers

import io.github.ackuq.models.NewWishListPayload
import io.github.ackuq.models.User
import io.github.ackuq.models.WishList
import io.github.ackuq.services.WishListService

object WishListController {

    suspend fun createWishList(newWishListPayload: NewWishListPayload): WishList {
        return WishListService.createTable(newWishListPayload)
    }

    suspend fun getUserWishLists(user: User): List<WishList> {
        return WishListService.getUsersTables(user.uuid)
    }
}