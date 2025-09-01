buildscript {
    val kotlinVersion by extra("1.9.0")
    dependencies {
        classpath("com.google.gms:google-services:4.4.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
    repositories {
        mavenCentral()
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
}