plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.openclassrooms.eventorias"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.openclassrooms.eventorias"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "MAPS_API_KEY",
            "\"AIzaSyBkNXnVe1nBN3IH4FvWjG_3K--vTFAtrhI\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true          // obfuscation ProGuard (cahier des charges)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }



    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    // Compose (via BOM — les versions sont gérées automatiquement)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Firebase (via BOM)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Coil (images)
    implementation(libs.coil.compose)

    // Icons
    implementation("androidx.compose.material:material-icons-extended")
}