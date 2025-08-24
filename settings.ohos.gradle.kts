pluginManagement {
    repositories {
        maven("https://mirrors.tencent.com/nexus/repository/maven-public")
        gradlePluginPortal()
        mavenCentral()
        google()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://mirrors.tencent.com/nexus/repository/maven-public")
        mavenCentral()
        google()
        mavenLocal()
    }
}

rootProject.name = "WanKuikly"

val buildFileName = "build.ohos.gradle.kts"
rootProject.buildFileName = buildFileName

include(":androidApp")
include(":shared")
project(":shared").buildFileName = buildFileName