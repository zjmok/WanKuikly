buildscript {
    dependencies {
        classpath(BuildPlugin.kuikly)
    }
}

plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version(Version.AGP_VERSION).apply(false)
    id("com.android.library").version(Version.AGP_VERSION).apply(false)
    kotlin("android").version(Version.KOTLIN_VERSION).apply(false)
    kotlin("multiplatform").version(Version.KOTLIN_VERSION).apply(false)
    id("com.google.devtools.ksp").version(Version.KSP_VERSION).apply(false)
    kotlin("plugin.serialization") version Version.KOTLIN_VERSION apply false

}
