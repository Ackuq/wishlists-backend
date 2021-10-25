package io.github.ackuq.routes

import io.github.ackuq.TestDatabaseFactory
import io.github.ackuq.conf.JwtConfig
import io.github.ackuq.dto.TokenDTO
import io.github.ackuq.dto.UserCredentialsDTO
import io.github.ackuq.dto.UserDTO
import io.github.ackuq.services.UserService
import io.github.ackuq.utils.ApiError
import io.github.ackuq.utils.ApiSuccess
import io.github.ackuq.withTestServer
import io.ktor.http.*
import io.ktor.server.testing.*
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

    private val user = UserCredentialsDTO("testUser@test.com", "password")
    private val otherUser = UserCredentialsDTO("otherTestUser@test.com", "password")

    @BeforeTest
    fun setup() {
        databaseFactory.init()
    }

    @AfterTest
    fun tearDown() {
        databaseFactory.close()
    }

    private fun setUpUsers(): TokenDTO {
        val token = JwtConfig.registerCustomer(user)
        JwtConfig.registerCustomer(otherUser)
        return token
    }

    @Test
    fun register() = withTestServer {
        // Given
        val payload = Json.encodeToString(user)

        // When
        with(handleRequest(HttpMethod.Post, "api/v1/auth/register") {
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(payload)
        }) {
            val data = Json.decodeFromString<ApiSuccess<TokenDTO>>(
                response.content!!
            )
            // Then
            assertEquals(HttpStatusCode.Created, response.status())
            assertEquals(HttpStatusCode.Created.value, data.status)
        }
    }

    @Test
    fun registerConflict() = withTestServer {
        // Given
        val payload = Json.encodeToString(user)
        setUpUsers()

        // When
        with(handleRequest(HttpMethod.Post, "api/v1/auth/register") {
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(payload)
        }) {
            val data = Json.decodeFromString<ApiError>(
                response.content!!
            )
            // Then
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals(HttpStatusCode.BadRequest.value, data.status)
        }
    }

    @Test
    fun login() = withTestServer {
        // Given
        setUpUsers()
        val payload = Json.encodeToString(user)
        // When
        with(handleRequest(HttpMethod.Post, "api/v1/auth/login") {
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(payload)
        }) {
            val data = Json.decodeFromString<ApiSuccess<TokenDTO>>(
                response.content!!
            )

            // Then
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(HttpStatusCode.OK.value, data.status)
        }
    }

    @Test
    fun getMe() = withTestServer {
        // Given
        val token = setUpUsers()
        val userUUID = UserService.getUserByEmail(user.email)!!.id.value.toString()

        // When
        with(handleRequest(HttpMethod.Get, "api/v1/users/me") {
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Authorization, "Bearer ${token.accessToken}")
        }) {
            val data = Json.decodeFromString<ApiSuccess<UserDTO>>(
                response.content!!
            )
            // Then
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(HttpStatusCode.OK.value, data.status)
            assertEquals(userUUID, data.result.uuid)
            assertEquals(user.email, data.result.email)
        }
    }

    @Test
    fun getUUID() = withTestServer {
        // Given
        val token = setUpUsers()
        val userUUID = UserService.getUserByEmail(user.email)!!.id.value.toString()

        // When

        // Should be able to get own profile
        with(handleRequest(HttpMethod.Get, "api/v1/users/${userUUID}") {
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Authorization, "Bearer ${token.accessToken}")
        }) {
            val data = Json.decodeFromString<ApiSuccess<UserDTO>>(
                response.content!!
            )
            // Then
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(HttpStatusCode.OK.value, data.status)
            assertEquals(userUUID, data.result.uuid)
            assertEquals(user.email, data.result.email)
        }

        val otherUUID = UserService.getUserByEmail(otherUser.email)!!.id.value.toString()
        // Should not be able to get other peoples profiles
        with(handleRequest(HttpMethod.Get, "api/v1/users/${otherUUID}") {
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Authorization, "Bearer ${token.accessToken}")
        }) {
            assertEquals(HttpStatusCode.Forbidden, response.status())
        }
    }
}