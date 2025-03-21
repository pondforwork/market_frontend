plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.marketbooking"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.marketbooking"
        minSdk = 24
        targetSdk = 35
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

        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.14"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.activity:activity-compose")  // Jetpack Activity Compose integration
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))  // Jetpack Compose BOM (Bill of Materials)
    implementation("androidx.compose.ui:ui")  // Compose UI for basic UI components
    implementation("androidx.compose.ui:ui-graphics")  // Compose UI for graphics support
    implementation("androidx.compose.ui:ui-tooling-preview")  // Compose tooling support for previews
    implementation("androidx.compose.material:material")  // Material Design components for Compose
    implementation("androidx.compose.material3:material3")  // New Material3 components for Compose
    implementation ("androidx.compose.material3:material3:1.1.2")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

}