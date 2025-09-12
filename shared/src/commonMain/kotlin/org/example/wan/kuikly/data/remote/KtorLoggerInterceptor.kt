package org.example.wan.kuikly.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.client.utils.EmptyContent
import io.ktor.http.HttpMethod
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import org.example.wan.kuikly.utils.takeNotNull
import org.example.wan.kuikly.utils.toJson

class KtorLoggerInterceptor {
    companion object Feature : HttpClientPlugin<Unit, KtorLoggerInterceptor> {
        override val key: io.ktor.util.AttributeKey<KtorLoggerInterceptor>
            get() = io.ktor.util.AttributeKey("KtorLogInterceptor")

        override fun prepare(block: Unit.() -> Unit): KtorLoggerInterceptor = KtorLoggerInterceptor()

        override fun install(plugin: KtorLoggerInterceptor, scope: HttpClient) {
            scope.sendPipeline.intercept(HttpSendPipeline.State) {

                println("--> request ${context.method} to ${context.url}")
//                println("context.headers: ${context.headers::class.simpleName}") // HeadersBuilder
                if (context.headers.isEmpty().not()) {
                    println("--> request Headers: ${context.headers.entries()}}")
                }

                if (context.method != HttpMethod.Get) {
                    val body = context.body
                    (body as? OutgoingContent)?.let {
                        body.contentLength.takeNotNull {
                            if (it > 0) {
                                when (body) {
                                    is EmptyContent -> {}

                                    is TextContent -> {
                                        println("--> request Body (TextContent): ${body.text}")
                                    }

                                    is FormDataContent -> {
                                        println("--> request Body (FormDataContent): ${body.formData.entries()}")
                                    }

                                    is MultiPartFormDataContent -> {
                                        println("--> request Body (MultiPartFormDataContent) contentType = [${(body.contentType)}]")
                                    }

                                    is ByteArrayContent -> {
                                        println("--> request Body (ByteArrayContent) contentType = [${body.contentType}]")
                                    }

                                    else -> {
                                        println(
                                            "--> request Body (${
                                                body::class.simpleName?.split(".")?.last()
                                            }): ${body.toJson(false)}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                proceed()
            }
            scope.receivePipeline.intercept(HttpReceivePipeline.State) {
                val t1 = it.requestTime.timestamp
                val t2 = it.responseTime.timestamp
                println("<-- response ${it.status.value} from ${it.request.method} ${it.request.url} in ${(t2 - t1)} ms")
                // response.headers 是 EmptyHeaders，不包含任何值
//                println("it.request.headers: ${it.request.headers::class.simpleName}") // HeadersImpl
//                println("it.headers: ${it.headers::class.simpleName}") // null
                println("<-- response Headers: ${it.headers.entries()}}")
                println(it.bodyAsText())

                proceed()
            }
        }
    }
}