package io.github.ackuq.utils

import kotlinx.serialization.Serializable

@Serializable
data class ApiSuccess<T : Any>(val status: Int, val result: T)
