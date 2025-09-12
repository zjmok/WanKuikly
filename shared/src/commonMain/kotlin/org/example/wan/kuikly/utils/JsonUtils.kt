package org.example.wan.kuikly.utils

import kotlinx.serialization.json.Json

val json = Json {
    explicitNulls = false // 将 JSON 中的 null 视为字段缺失
    encodeDefaults = true // 是否应对 Kotlin 属性的默认值进行编码，不影响解码
    coerceInputValues = true // JSON 缺失字段，或类型不对应，则使用默认值
    ignoreUnknownKeys = true // 忽略 JSON 中存在的未知字段，避免因此报错
    prettyPrint = false // Json 格式化
}

// kotlinx-serialization 不能序列化 Any，(Gson 可以使用 Any，Gson 是通过反射获取实际类型)
// 所以这里只能使用泛型 T，并且是 reified，确保是实际类型
inline fun <reified T> T.toJson(format: Boolean = true): String {
    return try {
        val json = Json(json) {
            prettyPrint = format
        }
        val encodeToString = json.encodeToString(this)
        encodeToString
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

inline fun <reified T> fromJson(jsonString: String): T? {
    return try {
        json.decodeFromString<T>(jsonString)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
