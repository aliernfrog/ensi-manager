plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val composeMaterialVersion = "1.6.0"
val composeMaterial3Version = "1.2.0-rc01"
val composeCompilerVersion = "1.5.8"

android {
    namespace = "com.aliernfrog.ensimanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aliernfrog.ensimanager"
        minSdk = 24
        targetSdk = 34
        versionCode = 19400
        versionName = "1.9.4"
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
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:$composeMaterialVersion")
    implementation("androidx.compose.material:material:$composeMaterialVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeMaterialVersion")
    implementation("androidx.compose.material3:material3:$composeMaterial3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$composeMaterial3Version")
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("aliernfrog:top-toast-compose:1.4.0-alpha05")
    implementation("com.google.code.gson:gson:2.10.1")
    //0.4.0 and above versions of compose-markdown library crashes with the latest compose alpha libraries
    //noinspection GradleDependency
    implementation("com.github.jeziellago:compose-markdown:0.3.7")
}