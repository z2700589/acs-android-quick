import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/*
 * Copyright 2026 zhaijie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.acs.quick.common"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        multiDexEnabled = true
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    api(project(":quick-res"))
    api(project(":quick-ui-widgets"))
    api(project(":quick-search"))

    // AndroidX
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.androidx.activity.ktx)
    api(libs.androidx.fragment.ktx)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.recyclerview)
    api(libs.androidx.multidex)
    api(libs.androidx.startup)
    api(libs.androidx.datastore)
    api(libs.androidx.paging)
    api(libs.material)

    // Lifecycle
    api(libs.lifecycle.livedata.ktx)
    api(libs.lifecycle.runtime.ktx)
    api(libs.lifecycle.viewmodel.ktx)

    // Room
    api(libs.room.runtime)
    api(libs.room.ktx)
    ksp(libs.room.compiler)

    // Work
    api(libs.work.runtime)
    api(libs.work.runtime.ktx)

    // Hilt
    api(libs.hilt.android)
    api(libs.hilt.ext.work)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.ext.compiler)

    // Network
    api(libs.retrofit)
    api(libs.retrofit.converter.moshi)
    api(libs.okhttp)
    api(libs.okhttp.logging)
    api(libs.moshi)
    api(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)

    // Kotlin
    api(libs.kotlin.stdlib)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)

    // WindowManager（折叠屏适配）
    api(libs.window)

    // UI Utils
    api(libs.glide)
    ksp(libs.glide.compiler)
    api(libs.timber)
    api(libs.brv)
    api(libs.utilcodex)
    api(libs.smartrefresh)
    api(libs.liveeventbus)

    api(libs.therouter)
    api(libs.statelayout)
    api(libs.fragmentVisibility)
    kapt(libs.therouter.compiler)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.extJunit)
    androidTestImplementation(libs.espresso)
}
