plugins {
    id("com.android.application")

    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.cosmorun"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cosmorun"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation ("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation ("androidx.compose.ui:ui:1.7.6")
    implementation ("androidx.compose.runtime:runtime:1.7.6")
    implementation ("androidx.compose.foundation:foundation:1.5.2")
    implementation ("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation ("androidx.compose.ui:ui-text:1.7.6")
    implementation ("io.coil-kt:coil-compose:2.2.2")
    implementation ("io.coil-kt:coil-gif:2.3.0")

    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))


    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    implementation ("androidx.compose.ui:ui:1.5.2")
    implementation ("androidx.compose.runtime:runtime:1.5.2")
    implementation ("androidx.compose.foundation:foundation:1.5.2")
    implementation ("androidx.compose.material3:material3:1.2.0")


    implementation ("androidx.activity:activity-compose:1.7.2")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)


    debugImplementation ("androidx.compose.ui:ui-tooling:1.5.2")
    debugImplementation ("androidx.compose.ui:ui-tooling-preview:1.5.2")


    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.5.2")
    debugImplementation ("androidx.compose.ui:ui-test-manifest:1.5.2")
}