package io.github.ackuq.services

import io.github.ackuq.conf.DatabaseFactory.dbQuery
import io.github.ackuq.models.NewWishListPayload
import io.github.ackuq.models.WishList
import io.github.ackuq.models.WishLists
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

object WishListService {
    suspend fun createTable(newWishList: NewWishListPayload): WishList = dbQuery {
        WishLists.insert {
            it[owner] = UUID.fromString(newWishList.owner)
        }.resultedValues!!.first().let { toWishList(it) }
    }

    suspend fun getTableById(id: Int): WishList? = dbQuery {
        WishLists.select { WishLists.id eq id }.mapNotNull { toWishList(it) }.singleOrNull()
    }

    suspend fun getUsersTables(uuid: UUID): List<WishList> = dbQuery {
        WishLists.select { WishLists.owner eq uuid }.mapNotNull { toWishList(it) }
    }

    private fun toWishList(row: ResultRow): WishList =
        WishList(
            id = row[WishLists.id],
            owner = row[WishLists.owner],
        )
}