plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

name "SpaceUp-NextUI"
group "technology.iatlas.spaceup"
version "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/releases")
    }
    maven {
        url = uri("https://artifactory.iatlas.dev/releases")
    }
    maven {
        url = uri("https://repo1.maven.org/maven2")
    }
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.8.2")
    //implementation("androidx.activity:activity-compose:1.8.0") // for SDK Version 34
}

android {
    compileSdk = 34
    defaultConfig {
        applicationId = "technology.iatlas.spaceup.android"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    namespace = "technology.iatlas.spaceup.android"
}