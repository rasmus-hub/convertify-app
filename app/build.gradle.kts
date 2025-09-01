import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

// Leer propiedades del archivo local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.instamedia.convertify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.instamedia.convertify"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"${localProperties.getProperty("API_BASE_URL", "https://default-api-url.com")}\"")
            buildConfigField("String", "API_KEY", "\"${localProperties.getProperty("API_KEY", "default-key")}\"")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "API_BASE_URL", "\"${localProperties.getProperty("API_BASE_URL", "https://default-api-url.com")}\"")
            buildConfigField("String", "API_KEY", "\"${localProperties.getProperty("API_KEY", "default-key")}\"")
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
    implementation("androidx.core:core-ktx:1.9.0")  // Compatible con AGP 8.1.4
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("com.google.android.gms:play-services-ads:22.6.0")  // Compatible
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))  // Compatible
    implementation("com.squareup.okhttp3:okhttp:4.11.0")  // Versión compatible
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}