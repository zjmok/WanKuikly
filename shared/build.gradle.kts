import com.tencent.kuikly.gradle.config.KuiklyConfig

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("maven-publish")
    id("com.tencent.kuikly-open.kuikly")
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
        extraSpecAttributes["resources"] = "['src/commonMain/assets/**']"
    }

    js(IR) {
        browser {
            webpackTask {
                outputFileName = "nativevue2.js" // 最后输出的名字
            }

            commonWebpackConfig {
                output?.library = null // 不导出全局对象，只导出必要的入口函数
                devtool = "source-map" // 不使用默认的 eval 执行方式构建出 source-map，而是构建单独的 sourceMap 文件
//                devtool = null
//                devtool = WebpackDevtool.EVAL_SOURCE_MAP // h5 使用 eval 执行方式构建，调试 kotlin 源码
//                devtool = WebpackDevtool.INLINE_SOURCE_MAP // 小程序不支持 eval，建议使用 inline_source_map，生产环境建议不要配置sourcemap
            }
        }
        binaries.executable() //将kotlin.js与kotlin代码打包成一份可直接运行的js文件
    }

    sourceSets {
        val ktorVersion = "3.1.3" // 3.2.3 // 3.1.3 // 3.0.3
        val okioVersion = "3.10.2"
        val commonMain by getting {
            dependencies {
//                implementation(project(":kmp"))

                implementation("com.tencent.kuikly-open:core:${Version.getKuiklyVersion()}")
                implementation("com.tencent.kuikly-open:core-annotations:${Version.getKuiklyVersion()}")

                // 需要注意，使用的库是否支持多平台，或纯 kotlin 实现

                // kotlin 协程
                // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
                // ohos 不兼容
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.KOTLINX_COROUTINES_VERSION}")

                // kuiklyx 内建协程
                // https://kuikly.tds.qq.com/DevGuide/thread-and-coroutines.html#kuikly协程api和依赖库
                implementation("com.tencent.kuiklyx-open:coroutines:${Version.KUIKLYX_COROUTINES_VERSION}")
                // ohos 兼容版本
//                implementation("com.tencent.kuiklyx-open:coroutines:${Version.KUIKLYX_COROUTINES_OHOS_VERSION}")

                // kotlinx-serialization
                // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.KOTLINX_SERIALIZATION_VERSION}")
                // ohos 兼容版本
                implementation("com.github.trhyl:kotlinx.serialization:v1.8.1")

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
                api(kotlin("test"))
//                api("com.squareup.okio:okio-fakefilesystem:$okioVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                api("com.tencent.kuikly-open:core-render-android:${Version.getKuiklyVersion()}")

                api("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }
        val jsMain by getting
        jsMain.dependencies {
            api("io.ktor:ktor-client-js:$ktorVersion")

            // js 使用 node.js 实现
//            api("com.squareup.okio:okio-nodefilesystem:$okioVersion")
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
    compileOnly("com.tencent.kuikly-open:core-ksp:${Version.getKuiklyVersion()}") {
        add("kspAndroid", this)
        add("kspIosArm64", this)
        add("kspIosX64", this)
        add("kspIosSimulatorArm64", this)
        add("kspJs", this)
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

// Kuikly 插件配置
configure<KuiklyConfig> {
    // JS 产物配置
    js {
        // 构建产物名，与 KMM 插件 webpackTask#outputFileName 一致
        outputName("nativevue")
        // 可选：分包构建时的页面列表，如果为空则构建全部页面
        // addSplitPage("route","home")
    }
}
