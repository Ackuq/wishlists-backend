package io.github.ackuq.services

import io.github.ackuq.conf.AuthorizationException
import io.github.ackuq.dao.User
import io.github.ackuq.dao.WishListProduct
import io.github.ackuq.dto.EditWishListProductPayload
import io.ktor.features.*
import org.jetbrains.exposed.sql.transactions.transaction

object WishListProductService {
    fun getWishListProduct(user: User, productId: Int): WishListProduct = transaction {
        val product = WishListProduct.findById(productId) ?: throw NotFoundException("Product not found")

        if (WishListService.hasReadAccess(user, product.wishList)) {
            product
        } else {
            throw AuthorizationException("Not authorized to do this")
        }
    }

    fun deleteWishListProduct(user: User, productId: Int) = transaction {
        val product = WishListProduct.findById(productId) ?: throw NotFoundException("Product not found")

        if (WishListService.hasWriteAccess(user, product.wishList)) {
            product.delete()
        } else {
            throw AuthorizationException("Not authorized to do this")
        }
    }

    fun editWishListProduct(
        user: User,
        productId: Int,
        editWishListProductPayload: EditWishListProductPayload
    ): WishListProduct = transaction {
        val product = WishListProduct.findById(productId) ?: throw NotFoundException("Product not found")

        if (WishListService.hasWriteAccess(user, product.wishList)) {
            if (editWishListProductPayload.title != null) {
                product.title = editWishListProductPayload.title
            }
            if (editWishListProductPayload.link != product.link) {
                product.link = editWishListProductPayload.link
            }
            if (editWishListProductPayload.description != product.description) {
                product.description = editWishListProductPayload.description
            }
            product
        } else {
            throw AuthorizationException("Not authorized to do this")
        }
    }

}