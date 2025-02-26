plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id("com.google.gms.google-services")
    id("com.android.application")
}

android {
    namespace 'com.example.myapplication'
    compileSdk 35

    defaultConfig {
        applicationId "com.example.myapplication"
        minSdk 24
        targetSdk 35
        versionCode 2
        versionName "1.0"

        // Use the API key from project properties
        buildConfigField("String", "DEEPSICK_API_KEY", "\"${project.properties['DEEPSICK_API_KEY']}\"")

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary true
        }
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
                targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = '17'
    }

    buildFeatures {
        compose true
        buildConfig true
    }

    composeOptions {
        kotlinCompilerExtensionVersion '1.5.8'
    }

    packaging {
        resources {
            excludes += '/META-INF/{AL2.0,LGPL2.1}'
        }
    }
}

dependencies {
    // Benchmarking library
    implementation libs.benchmark.common

            // Generative AI library
            implementation libs.generativeai

            // Compose BOM for version management
            def composeBom = platform('androidx.compose:compose-bom:2023.10.01')
    implementation composeBom
            androidTestImplementation composeBom

            // Core libraries
            implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
    implementation 'androidx.activity:activity-compose:1.8.2'
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-analytics")
    // Compose UI libraries
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    implementation "androidx.navigation:navigation-compose:2.7.7"
    implementation "androidx.compose.animation:animation-graphics:1.5.4"
    implementation "androidx.compose.material:material-icons-extended:1.5.4"

    // Debugging libraries
    debugImplementation 'androidx.compose.ui:ui-tooling'
    debugImplementation 'androidx.compose.ui:ui-test-manifest'

    // Retrofit and OkHttp for network calls
    implementation "com.squareup.retrofit2:retrofit:2.9.0"
    implementation "com.squareup.retrofit2:converter-gson:2.9.0"
    implementation "com.squareup.okhttp3:okhttp:4.9.3"
    implementation "com.squareup.okhttp3:logging-interceptor:4.9.3"
}