package io.github.ackuq.utils

import io.github.ackuq.models.ApiError
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import java.lang.Exception

suspend fun handleApiException(exception: Exception, status: HttpStatusCode, call: ApplicationCall) {
    exception.message?.let {message ->
        call.respond(status, ApiError(status.value, message))
    }
}

suspend fun handleApiError(message: String, status: HttpStatusCode, call: ApplicationCall) {
    call.respond(status, ApiError(status.value, message))
}