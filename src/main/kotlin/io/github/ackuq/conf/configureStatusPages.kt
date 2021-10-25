package io.github.ackuq.conf

import io.bkbn.kompendium.Notarized.notarizedException
import io.bkbn.kompendium.models.meta.ResponseInfo
import io.github.ackuq.utils.ApiError
import io.github.ackuq.utils.handleApiError
import io.github.ackuq.utils.handleApiException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException

inline fun <reified T : Throwable> StatusPages.Configuration.exceptionHandle(
    status: HttpStatusCode,
    doThrow: Boolean = false
) =
    notarizedException<T, ApiError>(
        info = ResponseInfo(
            status,
            status.description,
            examples = mapOf("example" to ApiError(status.value, status.description))
        )
    ) {
        handleApiException(it, status, call)
        if (doThrow) {
            throw it
        }
    }

fun Application.configureStatusPages() {
    install(StatusPages) {
        /**
         * Exceptions
         */
        // When the entity is not found
        exceptionHandle<NotFoundException>(HttpStatusCode.NotFound)
        // When the request is bad
        exceptionHandle<BadRequestException>(HttpStatusCode.BadRequest)
        // No or expired credentials
        exceptionHandle<AuthenticationException>(HttpStatusCode.Unauthorized)
        // When not enough permission
        exceptionHandle<AuthorizationException>(HttpStatusCode.Forbidden)
        // Serialization errors due to payload
        exceptionHandle<SerializationException>(HttpStatusCode.BadRequest)
        // Something unexpected
        exceptionHandle<Throwable>(
            HttpStatusCode.InternalServerError,
            doThrow = true
        )
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