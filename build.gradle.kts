buildscript {
    dependencies {
        // KGP
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.KOTLIN_VERSION}")
        // Kuikly 的 KGP
        classpath(BuildPlugin.kuikly)
//        classpath("com.tencent.kuikly-open:core-gradle-plugin:2.4.0-2.0.21")
//        classpath("com.tencent.kuikly-open:core-gradle-plugin:${Version.getKuiklyVersion()}")
    }
}

plugins {

    val kotlinVersion = Version.KOTLIN_VERSION

    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version(Version.AGP_VERSION).apply(false)
    id("com.android.library").version(Version.AGP_VERSION).apply(false)
    kotlin("android").version(kotlinVersion).apply(false)
    kotlin("multiplatform").version(kotlinVersion).apply(false)
    id("com.google.devtools.ksp").version(Version.KSP_VERSION).apply(false)

    kotlin("plugin.serialization").version(kotlinVersion).apply(false)

}
