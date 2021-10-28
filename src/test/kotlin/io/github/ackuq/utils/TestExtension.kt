package io.github.ackuq.utils

import io.github.ackuq.conf.JwtConfig
import io.github.ackuq.dao.Users
import io.github.ackuq.dao.WishLists
import io.github.ackuq.dto.CreateWishListPayload
import io.github.ackuq.dto.TokenDTO
import io.github.ackuq.dto.UserCredentialsDTO
import io.github.ackuq.services.UserService
import io.github.ackuq.services.WishListService
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.AfterTest

open class TestExtension {

    protected val user = UserCredentialsDTO("testUser@test.com", "password")
    protected val otherUser = UserCredentialsDTO("otherTestUser@test.com", "password")

    protected data class TokenPair(val user: TokenDTO, val otherUser: TokenDTO)
    protected data class WishListPair(val wishList: Int, val otherWishList: Int)

    protected fun setUpUsers(): TokenPair {
        val token = JwtConfig.registerCustomer(user)
        val otherToken = JwtConfig.registerCustomer(otherUser)
        return TokenPair(token, otherToken)
    }

    protected fun setUpWishLists(): WishListPair {
        val primaryUser = UserService.getUserByEmail(user.email)
        val wishList = WishListService.createWishList(primaryUser!!, CreateWishListPayload("First"))
        val secondUser = UserService.getUserByEmail(otherUser.email)
        val secondWishList = WishListService.createWishList(secondUser!!, CreateWishListPayload("Second"))
        return WishListPair(wishList.id.value, secondWishList.id.value)
    }

    @AfterTest
    fun clearDB() {
        transaction {
            Users.deleteAll()
            WishLists.deleteAll()
        }
    }
}

