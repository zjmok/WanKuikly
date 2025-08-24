object Version {

    const val KUIKLY_VERSION = "2.4.0"
    const val KOTLIN_VERSION = "2.0.21"

//    const val KOTLIN_OHOS_VERSION = "2.0.21-ohos"
    const val KOTLIN_OHOS_VERSION = "2.0.21-KBA-004"
    const val AGP_VERSION = "7.4.2"
    const val KSP_VERSION = "2.0.21-1.0.28"
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