pluginManagement {
    repositories {
        maven("https://mirrors.tencent.com/nexus/repository/maven-public")
        gradlePluginPortal() // https://plugins.gradle.org/m2
        mavenCentral() // https://repo.maven.apache.org/maven2, https://mvnrepository.com/
        google() // https://maven.google.com
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://mirrors.tencent.com/nexus/repository/maven-public")
        mavenCentral() // https://repo.maven.apache.org/maven2, https://mvnrepository.com/
        google() // https://maven.google.com
        mavenLocal()
    }
}

rootProject.name = "WanKuikly"

val buildFileName = "build.ohos.gradle.kts"
rootProject.buildFileName = buildFileName

include(":androidApp")
include(":shared")
project(":shared").buildFileName = buildFileName