package io.github.ackuq.services

import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.dao.User
import io.github.ackuq.dao.WishList
import io.github.ackuq.dao.WishLists
import io.github.ackuq.dto.Role
import io.github.ackuq.dto.WishListPayload
import io.ktor.features.*
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object WishListService {
    fun createWishList(newOwner: User): WishList = transaction {
        WishList.new {
            owner = newOwner
        }
    }

    fun updateWishList(user: User, id: Int, payload: WishListPayload): WishList = transaction {
        val wishList = WishList.findById(id) ?: throw NotFoundException("Wishlist not found")
        if(hasWriteAccess(user, wishList)) {
            wishList.users = SizedCollection(UserService.getUsers(payload.users.map { UUID.fromString(it) }))
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
        if (hasWriteAccess(user, wishList)) {
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

    private fun hasWriteAccess(user: User, wishList: WishList) = (user.role == Role.Admin || user.id.value == wishList.owner.id.value)
}