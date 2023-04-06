plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val composeVersion = "1.4.0"
val composeCompilerVersion = "1.4.4"

android {
    namespace = "com.aliernfrog.ensimanager"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.aliernfrog.ensimanager"
        minSdk = 24
        targetSdk = 33
        versionCode = 175
        versionName = "1.7.5"
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

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.material3:material3:1.1.0-beta01")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.29.0-alpha")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.27.0")
    implementation("com.github.aliernfrog:top-toast-compose:1.2.0")
    implementation("com.google.code.gson:gson:2.10.1")
}