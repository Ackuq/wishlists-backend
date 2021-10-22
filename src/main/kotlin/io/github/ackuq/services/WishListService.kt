package io.github.ackuq.services

import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.dao.*
import io.github.ackuq.dto.CreateWishListPayload
import io.github.ackuq.dto.CreateWishListProductPayload
import io.github.ackuq.dto.Role
import io.github.ackuq.dto.EditWishListPayload
import io.ktor.features.*
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object WishListService {
    fun createWishList(newOwner: User, createPayload: CreateWishListPayload): WishList = transaction {
        WishList.new {
            owner = newOwner
            title = createPayload.title
            description = createPayload.description
        }
    }

    fun updateWishList(user: User, id: Int, payload: EditWishListPayload): WishList = transaction {
        val wishList = WishList.findById(id) ?: throw NotFoundException("Wishlist not found")
        if(hasWriteAccess(user, wishList)) {
            if(payload.users != null) {
                wishList.users = SizedCollection(UserService.getUsers(payload.users.map { UUID.fromString(it) }))
            }
            if(payload.title != null) {
                wishList.title = payload.title
            }
            if(payload.description != null) {
                wishList.description = payload.description
            }
            wishList
        } else {
            throw AuthorizationException("Not authorized to update this")
        }
    }

    fun getUsersLists(user: User): List<WishList> = transaction {
        WishList.find { WishLists.owner eq user.id.value }.with(WishList::owner).toList()
    }

    fun getWishList(user: User, id: Int): WishList = transaction {
        val wishList = WishList.findById(id) ?: throw NotFoundException("Wishlist not found")
        if (hasReadAccess(user, wishList)) {
            wishList
        } else {
            throw AuthorizationException("Not authorized to view this page")
        }
    }

    fun deleteWishList(user: User, id: Int) = transaction {
        val wishList = WishList.findById(id) ?: throw NotFoundException("Wishlist not found")
        if(hasWriteAccess(user, wishList)) {
            wishList.delete()
        } else {
            throw AuthorizationException("Not authorized to view this page")
        }
    }

    fun addProductToWishList(user: User, id: Int, createProductPayload: CreateWishListProductPayload): WishListProduct = transaction {
        val wishList = WishList.findById(id) ?: throw BadRequestException("Wishlist not found")
        if(hasWriteAccess(user, wishList)) {
            WishListProduct.new {
                wishListId = wishList.id
                title = createProductPayload.title
                description = createProductPayload.description
                link = createProductPayload.link
            }
        } else {
            throw AuthorizationException("Not authorized to add products")
        }
    }

    fun hasReadAccess(user: User, wishList: WishList) =
        (user.role == Role.Admin) || user.id.value == wishList.owner.id.value || user.id.value in wishList.users.map { it.id.value }
    fun hasWriteAccess(user: User, wishList: WishList) = (user.role == Role.Admin || user.id.value == wishList.owner.id.value)
}