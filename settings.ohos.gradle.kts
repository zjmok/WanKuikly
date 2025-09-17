pluginManagement {
    repositories {
        maven("https://mirrors.tencent.com/nexus/repository/maven-tencent") // 用于发布由腾讯开源且发布的产物
        maven("https://mirrors.tencent.com/nexus/repository/maven-public") // 代理了十多个常用的国内外 Maven 仓库，包括 Maven 中央仓库、Google Maven 仓库、JitPack Maven 仓库、华为 Maven 仓库等等
        maven("https://jitpack.io")
        gradlePluginPortal() // https://plugins.gradle.org/m2
        mavenCentral() // https://repo.maven.apache.org/maven2, https://mvnrepository.com/
        google() // https://maven.google.com
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://mirrors.tencent.com/nexus/repository/maven-tencent") // 用于发布由腾讯开源且发布的产物
        maven("https://mirrors.tencent.com/nexus/repository/maven-public") // 代理了十多个常用的国内外 Maven 仓库，包括 Maven 中央仓库、Google Maven 仓库、JitPack Maven 仓库、华为 Maven 仓库等等
        maven("https://jitpack.io")
        mavenCentral() // https://repo.maven.apache.org/maven2, https://mvnrepository.com/
        google() // https://maven.google.com
        mavenLocal()
    }
}

rootProject.name = "WanKuikly"

val buildFileName = "build.ohos.gradle.kts"
rootProject.buildFileName = buildFileName

include(":androidApp")
//include(":kmp") // kmp
include(":shared") // kuikly
project(":shared").buildFileName = buildFileName
