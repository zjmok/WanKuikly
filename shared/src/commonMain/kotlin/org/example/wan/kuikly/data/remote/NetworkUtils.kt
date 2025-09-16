package org.example.wan.kuikly.data.remote

import com.tencent.kuikly.core.base.PagerScope
import com.tencent.kuikly.core.module.NetworkResponse
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import org.example.wan.kuikly.data.ApiResult
import org.example.wan.kuikly.utils.json
import org.example.wan.kuikly.utils.networkModule
import org.example.wan.kuikly.utils.sharedPreferencesModule
import org.example.wan.kuikly.utils.toJson

internal inline fun <reified T> PagerScope.requestGet(
    path: String,
    baseUrl: String = WanAPI.BASE_URL,
    params: Map<String, String>? = null,
    headers: Map<String, String>? = null,
    noinline onFailure: (exception: RemoteException) -> Unit = { it.biz(this) },
    noinline onSuccessHeaders: ((header: Map<String, List<String>>?) -> Unit)? = null,
    noinline onSuccess: (data: T?) -> Unit,
) {
    request(
        url = baseUrl + path,
        isPost = false,
        params = params,
        headers = headers,
    ) {
        it.onFailure(onFailure)
            .onSuccess<T>(onSuccessHeaders) {
                println(it.toJson(false))
                onSuccess.invoke(it)
            }
    }
}

internal inline fun <reified T> PagerScope.requestPost(
    path: String,
    params: Map<String, String>? = null,
    headers: Map<String, String>? = null,
    baseUrl: String = WanAPI.BASE_URL,
    noinline onFailure: (exception: RemoteException) -> Unit = { it.biz(this) },
    noinline onSuccessHeaders: ((header: Map<String, List<String>>?) -> Unit)? = null,
    noinline onSuccess: (data: T?) -> Unit,
) {
    request(
        url = baseUrl + path,
        isPost = true,
        params = params,
        headers = headers,
    ) {
        it.onFailure(onFailure)
            .onSuccess<T>(onSuccessHeaders) {
                println(it.toJson(false))
                onSuccess.invoke(it)
            }
    }
}

internal inline fun PagerScope.request(
    url: String,
    isPost: Boolean = false,
    params: Map<String, String>? = null,
    headers: Map<String, String>? = null,
    noinline onResult: ((result: NetworkResult) -> Unit),
) {
    val networkModule = this.networkModule
    val sharedPreferencesModule = sharedPreferencesModule

    println("${if (isPost) "POST" else "GET"}: $url")
    params?.let {
        println(params.toJson(false))
    }
    headers?.let {
        println(headers.toJson(false))
    }

    // cookies get
    val requestCookies = sharedPreferencesModule.getString("Cookie").takeIf { it.isNotBlank() }
    requestCookies?.let {
        println("requestCookies: $requestCookies")
    }

    networkModule.httpRequest(
        url = url,
        isPost = isPost,
        param = params?.let { JSONObject(params.toJson()) } ?: JSONObject(),
        // cookies 可放在 cookie 参数，也可放在 headers 的 Cookie
        cookie = requestCookies,
        headers = headers?.let { JSONObject(it.toJson()) },
        responseCallback = { data, success, errorMsg, response ->
            if (success) {
                try {
                    val responseCookies = response.headerFields.optString("Set-Cookie").takeIf { it.isNotBlank() }
                    responseCookies?.let {
                        println("responseCookies = $responseCookies")
                        val formatCookies = CookiesUtils.formatCookies(responseCookies)
                        println("formatCookies = $formatCookies")
                        sharedPreferencesModule.setString("Cookie", formatCookies)
                    }
                } catch (e: Exception) {
                    // 解析失败
                    e.printStackTrace()
                }
            }
            runResponseData {
                response to data
            }.let {
                onResult.invoke(it)
            }
        })
}

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

    data class Failure(val throwable: Throwable) : NetworkResult()

}

// networkModule 不需要 catch，跟 Ktor 不一样
inline fun runResponseData(block: () -> Pair<NetworkResponse, JSONObject>): NetworkResult {
    return try {
        NetworkResult.Success(block())
    } catch (e: Throwable) {
        NetworkResult.Failure(e)
    }
}

fun NetworkResponse.isSuccess(): Boolean = statusCode in (200 until 300)

/**
 * @param headerAction response header 数据，解析失败时，回调参数为 null
 * @param bodyAction response body 数据，解析失败时，回调参数为 null
 */
inline fun <reified T> NetworkResult.onSuccess(
    noinline headerAction: ((header: Map<String, List<String>>?) -> Unit)? = null,
    bodyAction: (data: T?) -> Unit,
): NetworkResult {
    if (this is NetworkResult.Success) {
        if (this.result.first.isSuccess()) {
            // header
            headerAction?.let {
                runCatching {
                    val headersJson = this.result.first.headerFields.toString()
                    json.decodeFromString<Map<String, List<String>>>(headersJson)
                }.onFailure {
                    println(it) // 解析失败
                    headerAction(null)
                }.onSuccess {
                    // body
                    headerAction(it)
                }
            }
            // body
            if (parseWrapped) {
                // 直接 optInt 减少解析失败的情况
                val errorCode = this.result.second.optInt("errorCode")
                if (errorCode == 0) {
                    // 需要处理 errorCode
                    runCatching {
                        val dataJson = this.result.second.toString()
                        json.decodeFromString<ApiResult<T>>(dataJson)
                    }.onFailure {
                        println(it) // 解析失败
                        bodyAction(null)
                    }.onSuccess {
                        bodyAction(it.data)
                    }
                }
                // errorCode != 0 onFailure
            } else {
                runCatching {
                    json.decodeFromString<T>(this.result.toString())
                }.onFailure {
                    println(it) // 解析失败
                    bodyAction(null)
                }.onSuccess {
                    bodyAction(it)
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
        val exception = RemoteException(-999, this.throwable)
        action(exception)
    } else {
        if (this is NetworkResult.Success) {
            // tag = KRNetworkModule, log =
            // Network module error: javax.net.ssl.SSLHandshakeException: Handshake failed
            // Kuikly 处理成成功返回 statusCode = -1000
            if (this.result.first.isSuccess().not()) {
                val statusCode = this.result.first.statusCode ?: -998
                val message = "HTTP error with status code: $statusCode"
                val ktorException = RemoteException(statusCode, message)
                action(ktorException)
            } else {
                if (parseWrapped) {
                    try {
                        // 需要处理 errorCode
                        val errorCode = this.result.second.optInt("errorCode")
                        // 解析数据，若 errorCode != 0 代表业务失败，则返回 BizException
                        if (errorCode != 0) {
                            // 服务器返回: errorCode = $errorCode, errorMsg = $errorMsg
                            val errorMsg = this.result.second.optString("errorMsg")
                            val bizException = RemoteException(errorCode, errorMsg)
                            action(bizException)
                        }
                    } catch (e: Exception) {
                        // 返回非 ApiResult 格式 解析异常
                        val bizException = RemoteException(-997, "数据异常")
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
