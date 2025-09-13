package org.example.kmp

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.java.JavaHttpConfig
import io.ktor.client.engine.java.JavaHttpEngine

actual fun getEngine(): HttpClientEngine {
    return JavaHttpEngine(JavaHttpConfig())
}