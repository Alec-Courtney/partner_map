plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.androidcourse.partner_map"
    compileSdk = 36
    buildToolsVersion = "36.1.0"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.androidcourse.partner_map"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"https://app.shouzhitu.cn/api/v1/\"")
            buildConfigField("String", "WS_BASE_URL", "\"wss://app.shouzhitu.cn/ws/chat\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "API_BASE_URL", "\"https://app.shouzhitu.cn/api/v1/\"")
            buildConfigField("String", "WS_BASE_URL", "\"wss://app.shouzhitu.cn/ws/chat\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.gson)

    implementation(libs.glide)
    implementation(libs.recyclerview)

    implementation(libs.amap.map)
    implementation(libs.amap.location)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
