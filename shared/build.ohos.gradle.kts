plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("maven-publish")
//    id("com.tencent.kuikly-open.kuikly")
    kotlin("plugin.serialization")

}

val KEY_PAGE_NAME = "pageName"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishLibraryVariants("release")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            freeCompilerArgs = freeCompilerArgs + getCommonCompilerArgs()
            isStatic = true
            license = "MIT"
        }
    }

    ohosArm64 {
        binaries.sharedLib {
        }
    }

    sourceSets {
        val ktorVersion = "3.1.3" // 3.2.3 // 3.1.3 // 3.0.3
        val okioVersion = "3.10.2"
        val commonMain by getting {
            dependencies {
                implementation("com.tencent.kuikly-open:core:${Version.getKuiklyOhosVersion()}")
                implementation("com.tencent.kuikly-open:core-annotations:${Version.getKuiklyOhosVersion()}")

                // 需要注意，使用的库是否支持多平台，或纯 kotlin 实现

                // kotlin 协程
                // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
                // ohos 不兼容
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.KOTLINX_COROUTINES_VERSION}")

                // kuiklyx 内建协程
                // https://kuikly.tds.qq.com/DevGuide/thread-and-coroutines.html#kuikly协程api和依赖库
//                implementation("com.tencent.kuiklyx-open:coroutines:${Version.KUIKLYX_COROUTINES_VERSION}")
                // ohos 兼容版本
                implementation("com.tencent.kuiklyx-open:coroutines:${Version.KUIKLYX_COROUTINES_OHOS_VERSION}")

                // kotlinx-serialization
                // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.KOTLINX_SERIALIZATION_VERSION}")
                // ohos 兼容版本
                implementation("com.github.trhyl:kotlinx.serialization:${Version.KOTLINX_SERIALIZATION_VERSION}")

                // ktor
                // https://mvnrepository.com/artifact/io.ktor/ktor-client-core
                implementation("io.ktor:ktor-client-core:$ktorVersion") // 核心库
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion") // 内容协商（用于JSON序列化）
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion") // Kotlinx.serialization JSON支持

                // okio, js 使用 node.js 实现
                // https://square.ac.cn/okio/multiplatform/
//                implementation("com.squareup.okio:okio:$okioVersion")

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
//                api("com.squareup.okio:okio-fakefilesystem:$okioVersion")
            }
        }

        val androidMain by getting {
            dependencies {
                api("com.tencent.kuikly-open:core-render-android:${Version.getKuiklyOhosVersion()}")

                api("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        iosMain.dependencies {
            api("io.ktor:ktor-client-darwin:$ktorVersion")
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }

        // 鸿蒙是 Kotlin Native 的目标平台
        val ohosArm64Main by getting {
            dependencies {
//                implementation("...")
//                api("io.ktor:ktor-client-js:$ktorVersion")
            }
        }

    }
}

group = "org.example.wan.kuikly"
version = System.getenv("kuiklyBizVersion") ?: "1.0.0"

publishing {
    repositories {
        maven {
            credentials {
                username = System.getenv("mavenUserName") ?: ""
                password = System.getenv("mavenPassword") ?: ""
            }
            rootProject.properties["mavenUr?"]?.toString()?.let { url = uri(it) }
        }
    }
}

ksp {
    arg(KEY_PAGE_NAME, getPageName())
}

dependencies {
    compileOnly("com.tencent.kuikly-open:core-ksp:${Version.getKuiklyOhosVersion()}") {
        add("kspAndroid", this)
        add("kspIosArm64", this)
        add("kspIosX64", this)
        add("kspIosSimulatorArm64", this)
        add("kspOhosArm64", this)
    }
}

android {
    namespace = "org.example.wan.kuikly.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }
    sourceSets {
        named("main") {
            assets.srcDirs("src/commonMain/assets")
        }
    }
}

fun getPageName(): String {
    return (project.properties[KEY_PAGE_NAME] as? String) ?: ""
}

fun getCommonCompilerArgs(): List<String> {
    return listOf(
        "-Xallocator=std"
    )
}

fun getLinkerArgs(): List<String> {
    return listOf()
}
