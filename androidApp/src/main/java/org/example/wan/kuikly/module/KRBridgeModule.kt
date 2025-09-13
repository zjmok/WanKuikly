package org.example.wan.kuikly.module

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.hjq.toast.ToastLogInterceptor
import com.hjq.toast.ToastParams
import com.hjq.toast.Toaster
import com.tencent.kuikly.core.render.android.export.KuiklyRenderBaseModule
import com.tencent.kuikly.core.render.android.export.KuiklyRenderCallback
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

class KRBridgeModule : KuiklyRenderBaseModule() {

    override fun call(method: String, params: String?, callback: KuiklyRenderCallback?): Any? {
        return when (method) {
            "ssoRequest" -> {
                ssoRequest(params, callback)
            }

            "showAlert" -> {
                showAlert(params, callback)
            }

            "closePage" -> {
                closePage(params)
            }

            "openPage" -> {
                openPage(params)
            }

            "copyToPasteboard" -> {
                copyToPasteboard(params)
            }

            "toast" -> {
                toast(params)
            }

            "currentThread" -> {
                return currentThread(callback)
            }

            "log" -> {
                log(params)
            }

            "reportDT" -> {
                reportDT(params)
            }

            "reportRealtime" -> {
                reportRealtime(params)
            }

            "qqLiveSSORequest" -> {
                qqLiveSSORequest(params, callback)
            }

            "localServeTime" -> {
                localServeTime(params, callback)
            }

            "currentTimestamp" -> {
                currentTimestamp(params)
            }

            "dateFormatter" -> {
                dateFormatter(params)
            }

            else -> callback?.invoke(
                mapOf(
                    "code" to -1,
                    "message" to "方法不存在"
                )
            )
        }
    }

    private fun reportRealtime(params: String?) {
    }

    private fun reportDT(params: String?) {
    }

    private fun log(params: String?) {
        if (params == null) {
            return
        }

        val paramJSON = JSONObject(params)
        Log.i("KuiklyRender", paramJSON.optString("content"))
    }

    private fun toast(params: String?) {
        if (params == null) {
            return
        }
        val paramJSON = JSONObject(params)
        val message = paramJSON.optString("content")
        Toaster.show(ToastParams().apply {
            text = message.toString()
            interceptor = ToastLogInterceptor(-1)
        })
    }

    private fun currentThread(callback: KuiklyRenderCallback?):Any {
        val currentThread = Thread.currentThread().name
        // 异步回调结果
        callback?.invoke(JSONObject().apply {
            put("currentThread", currentThread)
        })
        // 同步返回结果
        return JSONObject().apply {
            put("currentThread", currentThread)
        }
    }

    private fun copyToPasteboard(params: String?) {
        if (params == null) {
            return
        }

        val paramJSON = JSONObject(params)
        (context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.also {
            it.setPrimaryClip(ClipData.newPlainText(MODULE_NAME, paramJSON.optString("content")))
        }
    }

    private fun openPage(params: String?) {
        if (params == null) {
            return
        }
        val ctx = context ?: return
        val paramJSON = JSONObject(params)
        val url = paramJSON.optString("url")

        // todo

    }

    private fun closePage(params: String?) {
        activity?.finish()
    }

    private fun showAlert(params: String?, callback: KuiklyRenderCallback?) {
        if (params == null) {
            return
        }
        val paramJSON = JSONObject(params)
        val titleText = paramJSON.optString("title")
        val message = paramJSON.optString("message")
        val buttons = paramJSON.optJSONArray("buttons") ?: JSONArray()
        AlertDialog.Builder(context ?: return).apply {
            setTitle(titleText)
            setMessage(message)
            if (buttons.length() == 0) {
                setPositiveButton("确定") { dialog, _ ->
                    dialog.dismiss()
                    callback?.invoke(JSONObject().apply {
                        put("confirm", true)
                    })
                }
            } else if (buttons.length() == 1) {
                val btn = buttons.optString(0)
                setPositiveButton(btn) { dialog, _ ->
                    dialog.dismiss()
                    callback?.invoke(JSONObject().apply {
                        put("confirm", true)
                    })
                }
            } else {
                val positiveBtn = buttons.optString(1)
                val negativeBtn = buttons.optString(0)
                setNegativeButton(negativeBtn) { dialog, _ ->
                    dialog.dismiss()
                    callback?.invoke(JSONObject().apply {
                        put("cancel", true)
                    })
                }
                setPositiveButton(positiveBtn) { dialog, _ ->
                    dialog.dismiss()
                    callback?.invoke(JSONObject().apply {
                        put("confirm", true)
                    })
                }
            }
//            setCancelable(false)
            setOnCancelListener { callback?.invoke(JSONObject().apply {
                put("cancel", true)
            }) }
        }.create().show()
    }

    private fun ssoRequest(params: String?, callback: KuiklyRenderCallback?) {}

    private fun qqLiveSSORequest(params: String?, callback: KuiklyRenderCallback?) {
    }

    private fun localServeTime(params: String?, callback: KuiklyRenderCallback?) {
        val time = (System.currentTimeMillis() / 1000.0)
        callback?.invoke(
            mapOf(
                "time" to time
            )
        )
    }

    private fun currentTimestamp(params: String?): String {
        return (System.currentTimeMillis()).toString()
    }

    private fun dateFormatter(params: String?): String {
        val paramJSONObject = JSONObject(params ?: "{}")
        val data = Date(paramJSONObject.optLong("timeStamp"))
        val format = SimpleDateFormat(paramJSONObject.optString("format"))
        return format.format(data)
    }

    companion object {
        const val MODULE_NAME = "HRBridgeModule"
    }
}

private fun JSONObject.toMap(): Map<Any, Any> {
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