package org.example.wan.kuikly.utils

import com.tencent.kuikly.core.base.PagerScope
import com.tencent.kuikly.core.module.CallbackFn
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.module.NotifyModule
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import org.example.wan.kuikly.base.BridgeModule

internal val PagerScope.bridgeModule
    get(): BridgeModule = getPager().acquireModule(BridgeModule.MODULE_NAME)

internal val PagerScope.routerModule
    get(): RouterModule = getPager().acquireModule(RouterModule.MODULE_NAME)

internal val PagerScope.notifyModule
    get(): NotifyModule = getPager().acquireModule(NotifyModule.MODULE_NAME)

internal val PagerScope.networkModule
    get(): NetworkModule = getPager().acquireModule(NetworkModule.MODULE_NAME)

internal fun PagerScope.setTimeout(delay: Int, callback: () -> Unit): String {
    return com.tencent.kuikly.core.timer.setTimeout(pagerId, delay, callback)
}

/**
 * eg.
 * ```
 * postNotify("toast") {
 *     put("message", "value")
 * }
 * ```
 */
fun PagerScope.postNotify(eventName: String, eventData: JSONObject.() -> Unit) {
    notifyModule.postNotify(eventName, JSONObject().apply {
        eventData()
    })
}

fun PagerScope.toast(message: String) {
    bridgeModule.toast(message)
}

fun PagerScope.log(message: String) {
    bridgeModule.log(message)
}

fun PagerScope.log(
    title: String?,
    message: String?,
    leftBtnTitle: String?,
    rightBtnTitle: String?,
    responseCallbackFn: CallbackFn
) {
    bridgeModule.showAlert(title, message, leftBtnTitle, rightBtnTitle, responseCallbackFn)
}
