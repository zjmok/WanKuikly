package org.example.wan.kuikly.module

import com.tencent.kuikly.core.render.android.export.KuiklyRenderBaseModule
import com.tencent.kuikly.core.render.android.export.KuiklyRenderCallback
import org.json.JSONObject

class KRLogModule : KuiklyRenderBaseModule() {

    override fun call(method: String, params: String?, callback: KuiklyRenderCallback?): Any? {
        when (method) {
            "customLog" -> {
                val jsonObject = JSONObject(params ?: return null)
                val tag = jsonObject.optString("tag")
                val level = jsonObject.optString("level")
                val message = jsonObject.optString("message")
                when (level) {
                    "d" -> android.util.Log.d(tag, message)
                    "e" -> android.util.Log.e(tag, message)
                    "i" -> android.util.Log.i(tag, message)
                    "v" -> android.util.Log.v(tag, message)
                    "w" -> android.util.Log.w(tag, message)
                    else -> android.util.Log.d(tag, message)
                }
                // 异步回调结果
                callback?.invoke(JSONObject().apply {
                    put("result", "callback")
                })
                // 同步返回结果
                return JSONObject().apply {
                    put("result", "return")
                }

            }

            else -> {
                return null
            }
        }
    }

    companion object {
        // 对应 Kuikly 的 Module 名称
        const val MODULE_NAME = "HRLogModule"
    }

}