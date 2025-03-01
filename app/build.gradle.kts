plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "3.0"

        // Use the API key from project properties
        buildConfigField("String", "DEEPSICK_API_KEY", "\"${project.properties["DEEPSICK_API_KEY"]}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "androidx.compose.material3") {
            useVersion("1.2.0")
        }
    }
}

dependencies {
    // Benchmarking library
    implementation(libs.benchmark.common)

    // Generative AI library
    implementation(libs.generativeai)

    // Compose BOM (manages compatible versions of Compose libraries)
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Core libraries
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")

    // Compose UI libraries
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    // Explicitly add Material3 without a version number (BOM or resolution strategy will manage it)
    implementation("androidx.compose.material3:material3")

    // Debugging libraries
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Retrofit and OkHttp for network calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation "androidx.camera:camera-core:1.3.0"
    implementation "androidx.camera:camera-lifecycle:1.3.0"
    implementation "androidx.camera:camera-view:1.3.0"
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
}
