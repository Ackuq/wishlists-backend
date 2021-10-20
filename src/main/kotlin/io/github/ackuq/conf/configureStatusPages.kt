package io.github.ackuq.conf

import io.github.ackuq.utils.handleApiException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException

fun Application.configureStatusPages() {
    install(StatusPages) {
        // When the entity is not found
        exception<NotFoundException> {
            handleApiException(it, HttpStatusCode.NotFound, call)
        }
        // When the request is bad
        exception<BadRequestException> {
            handleApiException(it, HttpStatusCode.NotFound, call)
        }
        // No or expired credentials
        exception<AuthenticationException> {
            handleApiException(it, HttpStatusCode.Unauthorized, call)
        }
        // When not enough permission
        exception<AuthorizationException> {
            handleApiException(it, HttpStatusCode.Forbidden, call)
        }
        // Serialization errors due to payload
        exception<SerializationException> {
            handleApiException(it, HttpStatusCode.BadRequest, call)
        }
        exception<Exception> {
            handleApiException(it, HttpStatusCode.InternalServerError, call)
        }
        exception<Throwable> {
            handleApiException(it, HttpStatusCode.InternalServerError, call)
        }
    }
}

class AuthenticationException(message: String) : RuntimeException(message)
class AuthorizationException(message: String) : RuntimeException(message)