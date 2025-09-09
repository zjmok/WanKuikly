package org.example.wan.kuikly.base

import com.tencent.kuikly.core.module.CallbackFn
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import org.example.wan.kuikly.utils.bridgeModule

class LogModule : Module() {

    /**
     * TODO 功能实现
     * - Android √
     * - iOS ×
     * - 鸿蒙 ×
     * - Web ×
     * - 小程序 ×
     */
    fun log(
        message: String,
        level: String = "d",
        tag: String = "WanKuikly",
    ) {
        // callNativeMethod 异步调用
        // syncCallNativeMethod 同步调用
        val result = callNativeMethod("customLog", JSONObject().apply {
            put("tag", tag)
            put("level", level)
            put("message", message)
        }) {
            // 异步回调
            bridgeModule().log("异步回调: $it")
        }
        // 同步返回
        bridgeModule().log("同步返回: $result")
    }

    /**
     * 异步调用 Native 方法
     * @param methodName 调用方法名
     * @param data 传递给 Native 的数据
     * @param callbackFn Native 回调的函数
     */
    fun callNativeMethod(
        methodName: String,
        data: JSONObject?,
        callbackFn: CallbackFn?,
    ) {
        toNative(
            false,
            methodName,
            data?.toString(),
            callbackFn,
            false
        )
    }

    /**
     * 同步调用 Native 方法
     */
    fun syncCallNativeMethod(
        methodName: String,
        data: JSONObject?,
        callbackFn: CallbackFn?,
    ): String {
        return toNative(
            false,
            methodName,
            data?.toString(),
            callbackFn,
            true
        ).toString()
    }

    override fun moduleName(): String {
        return MODULE_NAME
    }

    companion object {
        // 对应 Native 侧注册的 Module 名称
        const val MODULE_NAME = "HRLogModule"
    }

}