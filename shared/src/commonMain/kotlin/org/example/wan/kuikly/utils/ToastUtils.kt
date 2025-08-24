package org.example.wan.kuikly.utils

import com.tencent.kuikly.core.base.PagerScope
import com.tencent.kuikly.core.module.NotifyModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.IPager

fun PagerScope.toast(message: String) {
    getPager().toast(message)
}

fun IPager.toast(message: String) {
    val notifyModule = acquireModule<NotifyModule>(NotifyModule.MODULE_NAME)
    notifyModule.postNotify("toast", JSONObject().apply {
        // TODO 目前接收端只有 Android
        put("message", message)
    })
}
