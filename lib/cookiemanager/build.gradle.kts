plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "eu.kanade.tachiyomi.lib.cookiemanager"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
    }

    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
}
