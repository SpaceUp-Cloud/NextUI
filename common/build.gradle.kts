plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.9.23"
}

group = "technology.iatlas.spaceup"
version = "1.0-SNAPSHOT"

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
        url = uri("https://repo1.maven.org/maven2/")
    }
    maven { url = uri("https://jitpack.io") }
}

kotlin {
    androidTarget {
        jvmToolchain(17)
    }
    jvm("desktop") {
        jvmToolchain(17)
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.material3)
                api(compose.materialIconsExtended)

                // ViewModel & Navigation
                val precomposeVersion = "1.5.7"
                api("moe.tlaster:precompose:$precomposeVersion")
                api("moe.tlaster:precompose-viewmodel:$precomposeVersion")
                api("moe.tlaster:precompose-koin:$precomposeVersion")
                api("io.insert-koin:koin-core:3.5.0")
                api("io.insert-koin:koin-compose:1.1.0")

                // JWT
                implementation("io.github.nefilim.kjwt:kjwt-core:0.9.0")

                // Google
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
                implementation("androidx.compose.material3:material3:1.1.2")
                implementation("androidx.compose.material:material-icons-extended:1.4.3")

                val dataStoreVersion = "1.1.0-alpha07"
                implementation("androidx.datastore:datastore-preferences:$dataStoreVersion")
                implementation("androidx.datastore:datastore-core-okio:$dataStoreVersion")

                // Jetbrains
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

                val ktorVersion = "2.3.4"
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-auth:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
                implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")

                // Misc.
                implementation("io.github.oshai:kotlin-logging:5.1.0")

                val jetthemeVersion = "1.0.0"
                // Use this if you want material design support (recommended)
                implementation("dev.lcdsmao.jettheme:jettheme-material:$jetthemeVersion")
                // Use this if you want to build custom design system
                implementation("dev.lcdsmao.jettheme:jettheme:$jetthemeVersion")

                // Settings
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.0.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.12.0")

                val ktorVersion = "2.3.4"
                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.10.0")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                api(compose.ui)
                api(compose.material3)
                // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
                implementation("ch.qos.logback:logback-classic:1.4.11")
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 28
    }
    namespace = "technology.iatlas.spaceup.common"
}
dependencies {
    implementation("io.ktor:ktor-client-logging-jvm:2.3.4")
    implementation("androidx.compose.ui:ui-geometry-android:1.5.4")
}
