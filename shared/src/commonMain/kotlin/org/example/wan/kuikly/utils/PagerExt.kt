package org.example.wan.kuikly.utils

import com.tencent.kuikly.core.base.PagerScope
import com.tencent.kuikly.core.coroutines.CoroutineScope
import com.tencent.kuikly.core.module.CallbackFn
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.module.NotifyModule
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.timer.setTimeout
import org.example.wan.kuikly.base.BridgeModule

internal fun <T : Module> PagerScope.getModule(moduleName: String): T? = getPager().getModule(moduleName)

internal fun <T : Module> PagerScope.acquireModule(moduleName: String): T = getPager().acquireModule(moduleName)

internal val PagerScope.bridgeModule get(): BridgeModule = acquireModule(BridgeModule.MODULE_NAME)

internal val PagerScope.routerModule get(): RouterModule = acquireModule(RouterModule.MODULE_NAME)

internal val PagerScope.notifyModule get(): NotifyModule = acquireModule(NotifyModule.MODULE_NAME)

internal val PagerScope.networkModule get(): NetworkModule = acquireModule(NetworkModule.MODULE_NAME)

internal fun PagerScope.setTimeout(delay: Int, callback: () -> Unit): String {
    return setTimeout(pagerId, delay, callback)
}

internal val PagerScope.lifecycleScope get(): CoroutineScope = getPager().lifecycleScope

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
