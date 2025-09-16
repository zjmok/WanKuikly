package org.example.wan.kuikly.data

import kotlinx.serialization.Serializable

@Serializable
data class ApiResult<T>(
    val `data`: T?,
    val errorCode: Int,
    val errorMsg: String
)
