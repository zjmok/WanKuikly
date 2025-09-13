package org.example.kmp

class Greeting {

    private val platform = getPlatform()

    fun greet(): String {
        return "platform: ${platform.name}!"
    }

}