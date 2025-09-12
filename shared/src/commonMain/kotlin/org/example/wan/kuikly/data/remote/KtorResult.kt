package org.example.wan.kuikly.data.remote

import io.ktor.client.statement.HttpResponse

sealed class KtorResult {
    data class Success(val response: HttpResponse) : KtorResult()
    data class Failure(val exception: Throwable) : KtorResult()
}
