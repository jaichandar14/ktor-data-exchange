plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" // for Ktor
}

android {
    namespace = "com.jay.ktor_data_exchange"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jay.ktor_data_exchange"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // Ktor
    implementation(libs.ktor.server.core.v234)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.content.negotiation.v234)
    implementation(libs.ktor.serialization.kotlinx.json.v234)
    implementation(libs.kotlinx.serialization.json)

    // Koin for DI
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    // QR Code (Zxing)
    implementation(libs.zxing.android.embedded)

    // JWT
    implementation(libs.java.jwt)
    implementation (libs.nanohttpd)

// Ktor client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json.v233)

// Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json.v151)


}