package org.example.kmp

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.JsClient

actual fun getEngine(): HttpClientEngine {
    return JsClient().create()
}