package io.github.ackuq.utils

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun handleApiException(exception: Throwable, status: HttpStatusCode, call: ApplicationCall) {
    exception.message?.let { message ->
        call.respond(status, ApiError(status.value, message))
    }
}

suspend fun handleApiError(message: String, status: HttpStatusCode, call: ApplicationCall) {
    call.respond(status, ApiError(status.value, message))
}

suspend inline fun <reified T : Any> handleApiSuccess(result: T, status: HttpStatusCode, call: ApplicationCall) {
    call.respond(status, ApiSuccess(status.value, result))
}