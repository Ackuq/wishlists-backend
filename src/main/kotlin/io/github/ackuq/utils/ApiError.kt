package io.github.ackuq.utils

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(val status: Int, val message: String)

