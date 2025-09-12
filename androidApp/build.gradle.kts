plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "org.example.wan.kuikly"
    compileSdk = 34
    defaultConfig {
        applicationId = "org.example.wan.kuikly.android"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            isMinifyEnabled = true // false 打包会报错
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
//        create("newType") {}
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    configurations.all {
        // 剔除指定库
//        exclude(group = "org.slf4j", module = "slf4j-api")
        // 强制指定版本
        resolutionStrategy {
            // 2.0.17 新版，报错
            // 1.7.36 旧版，兼容
            force("org.slf4j:slf4j-api:1.7.36")
        }
    }

    implementation(project(":shared"))

    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.appcompat:appcompat:1.3.1")

    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")

    // https://github.com/bumptech/glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.KOTLINX_COROUTINES_VERSION}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.KOTLINX_COROUTINES_VERSION}")

    // https://github.com/getActivity/Toaster
//    implementation("com.github.getActivity:Toaster:13.5")
    // https://github.com/zjmok/Toaster
    implementation("com.github.zjmok:Toaster:13.5.2") // 基于 13.5 的修改版

}