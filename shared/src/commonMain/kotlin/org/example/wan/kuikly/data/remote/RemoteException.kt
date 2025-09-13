package org.example.wan.kuikly.data.remote

import com.tencent.kuikly.core.base.PagerScope
import org.example.wan.kuikly.utils.toast

class RemoteException(val statusCode: Int, cause: Throwable?) : Exception(cause) {
    override fun toString(): String {
        return "RemoteException(statusCode=$statusCode, message=${message})"
    }
}

/**
 * 错误码统一处理方案
 */
fun RemoteException.biz(page: PagerScope) {
    printStackTrace() // 打印堆栈追踪
    when (statusCode) {
        // 根据业务错误码调整

        // 其他错误码
        -1 -> {
            page.toast("$message")
        }
        // 未登录
        -1001 -> {
            page.toast("请先登录")
        }
        // 2xx 成功，请求已成功被服务器接收、理解并接受
        in 200 until 300 -> {
            // 业务成功，代码逻辑不会走这里
        }
        // 1xx 信息响应，请求已被接收，需要继续处理或等待进一步指令
        // 3xx 重定向，需要客户端采取进一步的操作才能完成请求，通常与资源位置变动有关。
        // 4xx 客户端错误，请求包含语法错误、无法完成或缺乏权限，问题通常出现在客户端
        in 100 until 200,
        in 300 until 400,
        in 400 until 500 -> {
            page.toast("网络错误")
        }
        // 5xx 服务器错误，服务器在处理看似有效的请求时发生了错误，问题出在服务器端
        in 500 until 600 -> {
            page.toast("服务器错误")
        }
        // 未知错误
        // 自定义 Exception
        else -> {
            page.toast("未知错误")
        }
    }
}