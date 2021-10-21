package io.github.ackuq.conf

import io.github.ackuq.utils.handleApiError
import io.github.ackuq.utils.handleApiException
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException
import java.nio.file.ClosedFileSystemException

fun Application.configureStatusPages() {
    install(StatusPages) {
        /**
         * Exceptions
         */
        // When the entity is not found
        exception<NotFoundException> {
            handleApiException(it, HttpStatusCode.NotFound, call)
        }
        // When the request is bad
        exception<BadRequestException> {
            handleApiException(it, HttpStatusCode.BadRequest, call)
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
        /**
         * Statuses
         */
        status(HttpStatusCode.NotFound) {
            handleApiError("The resource was not found", it, call)
        }
        status(HttpStatusCode.BadRequest) {
            handleApiError("Bad request", it, call)
        }
        status(HttpStatusCode.Conflict) {
            handleApiError("Conflicting request", it, call)
        }
        status(HttpStatusCode.Forbidden) {
            handleApiError("You do not have permission to access this", it, call)
        }
        status(HttpStatusCode.Unauthorized) {
            handleApiError("Not authorized to view this page", it, call)
        }
    }
}

class AuthenticationException(message: String) : RuntimeException(message)
class AuthorizationException(message: String) : RuntimeException(message)