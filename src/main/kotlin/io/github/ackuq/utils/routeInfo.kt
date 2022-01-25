package io.github.ackuq.utils

import io.bkbn.kompendium.core.metadata.ExceptionInfo
import io.bkbn.kompendium.core.metadata.RequestInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.metadata.method.PostInfo
import io.ktor.http.HttpStatusCode
import kotlin.reflect.typeOf

data class SimpleResponseInfo<TResponse>(
    val status: HttpStatusCode,
    val example: TResponse
)

inline fun <reified TRequest, reified TResponse : Any> postInfo(
    summary: String,
    description: String,
    requestExample: TRequest,
    responseInfo: SimpleResponseInfo<TResponse>,
    throws: Set<ExceptionInfo<*>> = emptySet()
) = PostInfo<Unit, TRequest, ApiSuccess<TResponse>>(
    summary = summary,
    description = description,
    requestInfo = RequestInfo(
        description = "Usage",
        examples = mapOf(
            "Basic usage" to requestExample
        )
    ),
    responseInfo = ResponseInfo(
        status = responseInfo.status,
        description = responseInfo.status.description,
        examples = mapOf(
            "Response" to ApiSuccess(responseInfo.status.value, responseInfo.example)
        )
    ),
    canThrow = throws,
)

inline fun <reified TParam, reified TResponse : Any> getInfo(
    summary: String,
    description: String,
    responseInfo: SimpleResponseInfo<TResponse>,
    throws: Set<ExceptionInfo<*>> = emptySet()
) = GetInfo<TParam, ApiSuccess<TResponse>>(
    summary = summary,
    description = description,
    responseInfo = ResponseInfo(
        status = responseInfo.status,
        description = responseInfo.status.description,
        examples = mapOf(
            "Response" to ApiSuccess(responseInfo.status.value, responseInfo.example)
        )
    ),
    canThrow = throws
)

/*
    Exception helper
 */

fun exceptionInfo(
    status: HttpStatusCode,
    description: String
) = ExceptionInfo<ApiError>(
    responseType = typeOf<ApiError>(),
    status = status,
    description = description
)
