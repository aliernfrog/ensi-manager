plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val composeMaterial3Version = "1.2.0-alpha11"
val composeMaterialVersion = "1.6.0-beta01"
val composeCompilerVersion = "1.5.4"

android {
    namespace = "com.aliernfrog.ensimanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aliernfrog.ensimanager"
        minSdk = 24
        targetSdk = 34
        versionCode = 19000
        versionName = "1.9.0"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.compose.ui:ui:$composeMaterialVersion")
    implementation("androidx.compose.material:material:$composeMaterialVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeMaterialVersion")
    implementation("androidx.compose.material3:material3:$composeMaterial3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$composeMaterial3Version")
    implementation("io.insert-koin:koin-androidx-compose:3.5.0")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("com.github.aliernfrog:top-toast-compose:1.3.5-alpha01")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.jeziellago:compose-markdown:0.3.6")
}