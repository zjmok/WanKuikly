package org.example.wan.kuikly.data.remote

import com.tencent.kuikly.core.module.NetworkResponse
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import org.example.wan.kuikly.data.BaseData
import org.example.wan.kuikly.utils.json

sealed class NetworkResult {

    /**
     * 在 onSuccess 和 onFailure 的行为，默认 true
     * - `true` onSuccess 解析 BaseData<T> 然后提取 data (errorCode != 0 走 onFailure)
     * - `false` onSuccess 直接解析 T，不处理 data (errorCode != 0 走 onSuccess)
     */
    var parseWrapped = true

    /**
     * @param result `Pair` 类型
     * - `first` NetworkResponse 包含响应状态（但它没有数据）
     * - `second` JSONObject 响应数据
     */
    data class Success(val result: Pair<NetworkResponse, JSONObject>) : NetworkResult()

    data class Failure(val exception: Throwable) : NetworkResult()

}

// networkModule 不需要 catch，跟 Ktor 不一样
inline fun runResponseData(block: () -> Pair<NetworkResponse, JSONObject>): NetworkResult {
    return try {
        NetworkResult.Success(block())
    } catch (e: Throwable) {
        NetworkResult.Failure(e)
    }
}

inline val Pair<NetworkResponse, JSONObject>.result
    get(): NetworkResult {
        return try {
            NetworkResult.Success(this)
        } catch (e: Throwable) {
            NetworkResult.Failure(e)
        }
    }

fun NetworkResponse.isSuccess(): Boolean = statusCode in (200 until 300)

/**
 * @param action 解析失败时，回调参数为 null
 */
inline fun <reified T> NetworkResult.onSuccess(action: (value: T?) -> Unit): NetworkResult {
    if (this is NetworkResult.Success) {
        if (this.result.first.isSuccess()) {
            if (parseWrapped) {
                // 需要处理 errorCode
                runCatching {
                    json.decodeFromString<BaseData<T>>(this.result.second.toString())
                }.onFailure {
                    action(null)
                }.onSuccess {
                    if (it.errorCode == 0) {
                        action(it.data)
                    }
                    // errorCode != 0 onFailure
                }
            } else {
                runCatching {
                    json.decodeFromString<T>(this.result.toString())
                }.onFailure {
                    action(null)
                }.onSuccess {
                    action(it)
                }
            }
        }
    }
    return this
}

/**
 * 异常 + code 不在 [200, 300) 范围内
 */
inline fun NetworkResult.onFailure(action: (exception: RemoteException) -> Unit): NetworkResult {
    if (this is NetworkResult.Failure) {
        val exception = RemoteException(-999, this.exception)
        action(exception)
    } else {
        if (this is NetworkResult.Success) {
            if (this.result.first.isSuccess().not()) {
                val statusCode = this.result.first.statusCode ?: -998
                val exception = RuntimeException("HTTP error with status code: $statusCode")
                val ktorException = RemoteException(statusCode, exception)
                action(ktorException)
            } else {
                if (parseWrapped) {
                    // 需要处理 errorCode
                    val errorCode = this.result.second.optInt("errorCode")
                    val errorMsg = this.result.second.optString("errorMsg")
                    // 解析数据，若 errorCode != 0 代表业务失败，则返回 BizException
                    if (errorCode != 0) {
                        val exception = RuntimeException("服务器返回: errorCode = $errorCode, errorMsg = $errorMsg")
                        val bizException = RemoteException(errorCode, exception)
                        action(bizException)
                    }
                    // errorCode == 0 onSuccess
                }
                // !parseWrapped onSuccess
            }
        }
    }
    return this
}
