package org.example.wan.kuikly.utils

import com.tencent.kuikly.core.base.PagerScope

/**
 * 将 Map 转换为查询字符串。
 * 例如：mapOf("name" to "John Doe", "age" to "30") 转换为 "name=John%20Doe&age=30"。
 *
 * @param encode 是否对键和值进行 URL 编码。默认为 true，在绝大多数情况下都应启用。
 * @return 构造好的查询字符串。如果 Map 为空，则返回空字符串 ""。
 */
fun Map<String, String>.toParamsString(page: PagerScope, encode: Boolean = true): String {
    // 处理空 Map
    if (this.isEmpty()) return ""

    return this.entries
        .joinToString("&") { (key, value) ->
            // 关键步骤：对键和值进行 URL 编码，确保特殊字符（如 &, %, 空格, 中文）被正确转义
            // TODO 模板生成的项目 这两个方法在 Native 都是未实现
//            val encodedKey = if (encode) page.bridgeModule.urlEncode(key) else key
//            val encodedValue = if (encode) page.bridgeModule.urlEncode(value) else value
            val encodedKey = key
            val encodedValue = value
            "$encodedKey=$encodedValue" // 拼接成 "key=value" 格式
        }
}
