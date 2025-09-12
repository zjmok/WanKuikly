package org.example.wan.kuikly.data.remote

import com.tencent.kuikly.core.coroutines.CoroutineScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
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
                json()
            }
            // logger
            install(KtorLoggerInterceptor)
        }
        return client
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
 * @param T 需要解析的类型 可改为传入 BaseData<T> 返回 T 去掉一层包装
 * @param action 解析失败时 返回 null
 */
suspend inline fun <reified T> KtorResult.onSuccess(action: (value: T?) -> Unit): KtorResult {
    if (this is KtorResult.Success) {
        if (this.response.status.isSuccess()) {
            runCatching {
                this.response.body<T>()
            }.onFailure {
                action(null)
            }.onSuccess {
                action(it)
            }
        }
    }
    return this
}

/**
 * 异常 + code 不在 [200, 300) 范围内
 */
inline fun KtorResult.onFailure(action: (exception: RemoteException) -> Unit): KtorResult {
    if (this is KtorResult.Failure) {
        val exception = RemoteException(-999, this.exception)
        action(exception)
    } else {
        if (this is KtorResult.Success) {
            if (this.response.status.isSuccess().not()) {
                val statusCode = this.response.status.value
                val exception = RuntimeException("HTTP error with status code: $statusCode")
                val ktorException = RemoteException(statusCode, exception)
                action(ktorException)
            } else {

                // todo 还可以处理 成功返回 200 后 业务的 errorCode
                val errorCode = 0
                val message = ""
                // 上面是假数据

                // 解析数据，若 errorCode != 0 代表业务失败，则返回 BizException
                if (errorCode != 0) {
                    val exception = RuntimeException("服务器返回： errorCode = $errorCode, message = $message")
                    val bizException = RemoteException(errorCode, exception)
                    action(bizException)
                }
            }
        }
    }

    return this
}
