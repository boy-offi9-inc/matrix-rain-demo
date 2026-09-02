plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.boyoffi9.matrixrainview.demo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.boyoffi9.matrixrainview.demo"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    // Published via JitPack — see matrix-rain-view repo for the library source.
    implementation("com.github.boy-offi9-inc:matrix-rain-view:1.0.0")
    implementation("androidx.core:core-ktx:1.14.0")
    implementation("androidx.appcompat:appcompat:1.8.1")
}
