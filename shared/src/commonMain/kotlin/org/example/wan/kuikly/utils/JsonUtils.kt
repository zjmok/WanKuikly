package org.example.wan.kuikly.utils

import kotlinx.serialization.json.Json

val json = Json {
    explicitNulls = false // 将 JSON 中的 null 视为字段缺失
    encodeDefaults = true // 是否应对 Kotlin 属性的默认值进行编码，不影响解码
    coerceInputValues = true // JSON 缺失字段，或类型不对应，则使用默认值
    ignoreUnknownKeys = true // 忽略 JSON 中存在的未知字段，避免因此报错
}
