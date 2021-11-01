package io.github.ackuq.utils

import io.bkbn.kompendium.models.meta.MethodInfo
import io.bkbn.kompendium.models.meta.RequestInfo
import io.bkbn.kompendium.models.meta.ResponseInfo
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass

data class SimpleResponseInfo<TResponse>(
    val status: HttpStatusCode,
    val example: TResponse
)

inline fun <reified TRequest, reified TResponse : Any> postInfo(
    summary: String,
    description: String,
    requestExample: TRequest,
    responseInfo: SimpleResponseInfo<TResponse>,
    throws: Set<KClass<*>> = emptySet()
) = MethodInfo.PostInfo<Unit, TRequest, ApiSuccess<TResponse>>(
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
    throws: Set<KClass<*>> = emptySet()
) = MethodInfo.GetInfo<TParam, ApiSuccess<TResponse>>(
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
