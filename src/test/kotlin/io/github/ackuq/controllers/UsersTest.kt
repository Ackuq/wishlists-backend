package io.github.ackuq.controllers

import io.github.ackuq.TestDatabaseFactory
import io.github.ackuq.models.Role
import io.github.ackuq.models.UserPayload
import io.github.ackuq.services.UserService
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class UsersTest {

    private val databaseFactory: TestDatabaseFactory = TestDatabaseFactory()

    @BeforeTest
    fun setup() {
        databaseFactory.init()
    }

    @AfterTest
    fun tearDown() {
        databaseFactory.close()
    }

    @Test
    fun createUser(): Unit =
        runBlocking {
            val userPayload = UserPayload("test@testson.com", "pass")

            UserController.register(userPayload)

            val user = UserController.getUserByEmail(userPayload.email)
            val users = UserService.getAllUsers()


            assertEquals(1, users.size)
            assertEquals(user, users.first())
            assertEquals(Role.Customer, user.role)
            assertEquals(userPayload.email, user.email)
        }

}