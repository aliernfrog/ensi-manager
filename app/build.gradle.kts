plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.aboutlibraries)
}

android {
    namespace = "com.aliernfrog.ensimanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aliernfrog.ensimanager"
        minSdk = 24
        targetSdk = 35
        versionCode = 200000
        versionName = "2.0.0"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.lifecycle.ktx)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.splashscreen)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.window)

    implementation(libs.aboutlibraries)
    implementation(libs.coil)
    implementation(libs.coil.okhttp)
    implementation(libs.coil.svg)
    implementation(libs.gson)
    implementation(libs.koin)
    implementation(libs.markdown)
    implementation(libs.toptoast)
    implementation(libs.zoomable)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.tooling.preview)
}