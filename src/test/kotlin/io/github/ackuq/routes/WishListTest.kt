package io.github.ackuq.routes

import io.github.ackuq.dto.CreateWishListPayload
import io.github.ackuq.dto.WishListDTO
import io.github.ackuq.utils.ApiSuccess
import io.github.ackuq.utils.TestExtension
import io.github.ackuq.utils.withTestServer
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class)
class WishListTest : TestExtension() {
    @Test
    fun createWishList() = withTestServer {
        // Given
        val tokens = setUpUsers()
        val payload = CreateWishListPayload(
            title = "My wish list",
            description = "Birthday"
        )
        val payloadString = Json.encodeToString(
            payload
        )
        // When
        with(handleRequest(HttpMethod.Post, "/api/v1/wish-list") {
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Authorization, "Bearer ${tokens.user.accessToken}")
            setBody(payloadString)
        }) {
            val data = Json.decodeFromString<ApiSuccess<WishListDTO>>(
                response.content!!
            )
            // Then
            assertEquals(HttpStatusCode.Created, response.status())
            assertEquals(HttpStatusCode.Created.value, data.status)
            assertEquals(data.result.title, payload.title)
            assertEquals(data.result.description, payload.description)
            assertEquals(data.result.products, emptyList())
            assertEquals(data.result.users, emptyList())
        }
    }
}