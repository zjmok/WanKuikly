object Version {

    const val KUIKLY_VERSION = "2.4.0"
    const val KOTLIN_VERSION = "2.0.21"

//    const val KOTLIN_OHOS_VERSION = "2.0.21-ohos"
    const val KOTLIN_OHOS_VERSION = "2.0.21-KBA-004"

    const val AGP_VERSION = "7.4.2"

    // https://github.com/google/ksp/releases
//    const val KSP_VERSION = "2.1.21-2.0.1" // Template 的版本，但与 Kotlin 不对应
    const val KSP_VERSION = "2.0.21-1.0.28" // 与 Kotlin 对应的版本
//    const val KUIKLY_KSP_VERSION = "2.4.0-2.0.21"

    // 不能盲目使用最新版本，要使用 Kotlin 版本对应能用的版本
    const val KOTLINX_COROUTINES_VERSION = "1.10.2"
    const val KOTLINX_SERIALIZATION_VERSION = "1.8.1" // 1.9.0 // 1.8.1

    // https://repo1.maven.org/maven2/com/tencent/kuiklyx-open/coroutines/
//    const val KUIKLYX_COROUTINES_VERSION = "1.1.0-2.0.21"
//    const val KUIKLYX_COROUTINES_VERSION = "1.2.0-2.0.21"
    const val KUIKLYX_COROUTINES_VERSION = "1.3.0-2.0.21"
    const val KUIKLYX_COROUTINES_OHOS_VERSION = "1.1.0-2.0.21-ohos"

    /**
     * 获取 Kuikly 版本号，版本号规则：${shortVersion}-${kotlinVersion}
     * 适用于 core、core-ksp、core-annotation、core-render-android
     */
    fun getKuiklyVersion(): String {
        return "$KUIKLY_VERSION-$KOTLIN_VERSION"
    }

    /**
     * 获取 Kuikly Ohos 版本号
     */
    fun getKuiklyOhosVersion(): String {
        return "$KUIKLY_VERSION-$KOTLIN_OHOS_VERSION"
    }

}

object BuildPlugin {
    val kuikly by lazy {
        "com.tencent.kuikly-open:core-gradle-plugin:${Version.getKuiklyVersion()}"
    }
    val kuiklyOhos by lazy {
        "com.tencent.kuikly-open:core-gradle-plugin:${Version.getKuiklyOhosVersion()}"
    }
}