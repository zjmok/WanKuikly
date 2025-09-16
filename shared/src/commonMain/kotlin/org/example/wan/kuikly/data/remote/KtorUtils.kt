package org.example.wan.kuikly.data.remote

import com.tencent.kuikly.core.coroutines.CoroutineScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.toMap
import org.example.wan.kuikly.data.ApiResult
import org.example.wan.kuikly.kmp.getEngine

val ktorClient
    get(): HttpClient {
        // 通过 KMP 的 expect/actual 获取适配各平台的 HttpClient 引擎
        val engine = getEngine()
        val client = HttpClient(engine) {
            engine {
                // 引擎配置...
            }
            // 序列化
            install(ContentNegotiation) {
                json(org.example.wan.kuikly.utils.json)
            }
            // logger
            install(KtorLoggerInterceptor)
        }
        return client
    }

sealed class KtorResult {

    /**
     * 在 onSuccess 和 onFailure 的行为，默认 true
     * - `true` onSuccess 解析 BaseData<T> 然后提取 data (errorCode != 0 走 onFailure)
     * - `false` onSuccess 直接解析 T，不处理 data (errorCode != 0 走 onSuccess)
     */
    var parseWrapped = true

    data class Success(val response: HttpResponse) : KtorResult()

    data class Failure(val throwable: Throwable) : KtorResult()

}

inline fun <T : CoroutineScope> T.runCatchingKtor(block: T.() -> HttpResponse): KtorResult {
    return try {
        KtorResult.Success(block())
    } catch (e: Throwable) {
        KtorResult.Failure(e)
    }
}

/**
 * code 在 [200, 300) 范围内
 */
inline fun KtorResult.onSuccess(action: (value: HttpResponse) -> Unit): KtorResult {
    if (this is KtorResult.Success) {
        if (this.response.status.isSuccess()) {
            action(this.response)
        }
    }
    return this
}

/**
 * @param headerAction response header 数据
 * @param bodyAction response body 数据，解析失败时，回调参数为 null
 */
suspend inline fun <reified T> KtorResult.onSuccess(
    noinline headerAction: ((header: Map<String, List<String>>) -> Unit)? = null,
    bodyAction: (value: T?) -> Unit,
): KtorResult {
    if (this is KtorResult.Success) {
        if (this.response.status.isSuccess()) {
            // header
            headerAction?.let {
                val headers = this.response.headers.toMap()
                headerAction(headers)
            }
            // body
            if (parseWrapped) {
                // 需要处理 errorCode
                runCatching {
                    this.response.body<ApiResult<T>>()
                }.onFailure {
                    println(it) // 解析失败
                    bodyAction(null)
                }.onSuccess {
                    if (it.errorCode == 0) {
                        bodyAction(it.data)
                    }
                    // errorCode != 0 onFailure
                }
            } else {
                runCatching {
                    this.response.body<T>()
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
suspend inline fun KtorResult.onFailure(action: (exception: RemoteException) -> Unit): KtorResult {
    if (this is KtorResult.Failure) {
        val exception = RemoteException(-999, this.throwable)
        action(exception)
    } else {
        if (this is KtorResult.Success) {
            if (this.response.status.isSuccess().not()) {
                val statusCode = this.response.status.value
                val errorMsg = "HTTP error with status code: $statusCode"
                val ktorException = RemoteException(statusCode, errorMsg)
                action(ktorException)
            } else {
                if (parseWrapped) {
                    try {
                        // 需要处理 errorCode
                        val result = this.response.body<ApiResult<String>>()
                        // 解析数据，若 errorCode != 0 代表业务失败，则返回 BizException
                        val errorCode = result.errorCode
                        if (errorCode != 0) {
                            // 服务器返回: errorCode = $errorCode, errorMsg = $errorMsg
                            val errorMsg = result.errorMsg
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
