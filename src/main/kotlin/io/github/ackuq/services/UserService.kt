package io.github.ackuq.services

import io.github.ackuq.conf.DatabaseFactory.dbQuery
import io.github.ackuq.models.Role
import io.github.ackuq.models.User
import io.github.ackuq.models.UserPayload
import io.github.ackuq.models.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.insert
import java.util.*

object UserService {
    suspend fun createUser(newUser: UserPayload): UUID = dbQuery {
        Users.insert {
            it[email] = newUser.email
            it[passwordHash] = newUser.password
            it[role] = Role.Customer
        } get Users.uuid
    }

    suspend fun getAllUsers(): List<User> = dbQuery {
        Users.selectAll().map { toUser(it) }
    }

    suspend fun getUserByEmail(email: String): User? = dbQuery {
        Users.select {
            (Users.email eq email)
        }.mapNotNull { toUser(it) }.singleOrNull()
    }

    suspend fun getUserByUUID(uuid: UUID): User? = dbQuery {
        Users.select {
            (Users.uuid eq uuid)
        }.mapNotNull { toUser(it) }.singleOrNull()
    }


    private fun toUser(row: ResultRow): User =
        User(
            uuid = row[Users.uuid].toString(),
            email = row[Users.email],
            passwordHash = row[Users.passwordHash],
            role = row[Users.role]
        )
}