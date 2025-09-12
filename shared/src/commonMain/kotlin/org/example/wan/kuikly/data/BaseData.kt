package org.example.wan.kuikly.data

import kotlinx.serialization.Serializable

@Serializable
data class BaseData<T>(
    val `data`: T,
    val errorCode: Int,
    val errorMsg: String
)
