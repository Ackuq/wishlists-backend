package io.github.ackuq.routes

import io.github.ackuq.TestDatabaseFactory
import io.github.ackuq.controllers.UserController
import io.github.ackuq.models.User
import io.github.ackuq.models.UserPayload
import io.github.ackuq.utils.ApiSuccess
import io.github.ackuq.withTestServer
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class)
class UsersTest {

    private val databaseFactory: TestDatabaseFactory = TestDatabaseFactory()

    private val testUser = UserPayload("testUser@test.com", "password")

    @BeforeTest
    fun setup() {
        databaseFactory.init()
    }

    @AfterTest
    fun tearDown() {
        databaseFactory.close()
    }

    @Test
    fun register() = withTestServer {
        val payload = Json.encodeToString(
            UserPayload("test@testson.com", "password")
        )
        with(handleRequest(HttpMethod.Post, "api/v1/auth/register") {
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(payload)
        }) {
            assertEquals(HttpStatusCode.Created, response.status())
            val data = Json.decodeFromString<ApiSuccess<String>>(
                response.content!!
            )
            assertEquals(HttpStatusCode.Created.value, data.status)
        }
    }

    @Test
    fun login() = withTestServer {
        runBlocking {
            UserController.register(testUser)
        }
        val payload = Json.encodeToString(
            testUser
        )
        with(handleRequest(HttpMethod.Post, "api/v1/auth/login") {
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(payload)
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            val data = Json.decodeFromString<ApiSuccess<String>>(
                response.content!!
            )
            assertEquals(HttpStatusCode.OK.value, data.status)
        }
    }

    @Test
    fun getMe() = withTestServer {
        val token = runBlocking {
            UserController.register(testUser)
        }
        with(handleRequest(HttpMethod.Get, "api/v1/users/me") {
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Authorization, "Bearer $token")
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            val data = Json.decodeFromString<ApiSuccess<User>>(
                response.content!!
            )
            assertEquals(HttpStatusCode.OK.value, data.status)
            assertEquals(testUser.email, data.result.email)
        }
    }

}