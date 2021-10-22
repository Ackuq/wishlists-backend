package io.github.ackuq.services

import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.dao.User
import io.github.ackuq.dao.WishList
import io.github.ackuq.dao.WishLists
import io.github.ackuq.dto.Role
import io.ktor.auth.*
import io.ktor.features.*
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object WishListService {
    fun createWishList(newOwner: User): WishList = transaction {
        WishList.new {
            owner = newOwner
        }
    }

    fun getTableById(id: Int): WishList? = transaction {
        WishList.findById(id)?.load(WishList::owner)
    }

    fun getUsersLists(user: User): List<WishList> = transaction {
        WishList.find { WishLists.owner eq user.id.value }.with(WishList::owner).toList()
    }

    fun getWishList(user: User, id: Int): WishList = transaction {
        val wishList = WishList.findById(id) ?: throw NotFoundException("Wishlist not found")
        if(user.role == Role.Admin || user.id.value == wishList.owner.id.value) {
            wishList
        } else {
            throw AuthorizationException("Not authorized to view this page")
        }
    }
}