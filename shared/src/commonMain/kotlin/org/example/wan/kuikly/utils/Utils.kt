package org.example.wan.kuikly.utils

import com.tencent.kuikly.core.manager.BridgeManager
import com.tencent.kuikly.core.manager.PagerManager
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import org.example.wan.kuikly.base.BridgeModule

/**
 * currentPageId的值仅在Pager上下文中有效，错误使用可能导致observable不更新、PagerNotFoundException、
 * ReactiveObserverNotFoundException等问题，建议使用Pager上下文的pagerId代替。
 */
internal fun bridgeModule(pageId: String = BridgeManager.currentPageId): BridgeModule {
    return PagerManager.getPager(pageId)
        .acquireModule(BridgeModule.MODULE_NAME)
}

fun JSONObject.toMap(): Map<Any, Any> {
    val map = mutableMapOf<Any, Any>()
    val keys = keys()
    while (keys.hasNext()) {
        val key = keys.next()
        when (val v = opt(key)) {
            is JSONObject -> {
                map[key] = v.toMap()
            }

            else -> {
                v?.also {
                    map[key] = it
                }
            }
        }
    }
    return map
}
