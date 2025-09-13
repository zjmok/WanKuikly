package org.example.kmp.data

import kotlinx.serialization.Serializable

@Serializable
data class ApiResult<T>(
    val data: T? = null,
    val code: Int = 0,
    val message: String = "ok",
)

fun emptyResult(
    code: Int = 200404,
    message: String = "empty",
) = ApiResult<Unit>(
    code = code,
    message = message,
)

fun emptyResultOk(
    code: Int = 0,
    message: String = "ok",
) = ApiResult<Unit>(
    code = code,
    message = message,
)
