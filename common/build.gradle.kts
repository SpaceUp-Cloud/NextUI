plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.8.22"
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
}

kotlin {
    android()
    jvm("desktop") {
        jvmToolchain(11)
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
                api("moe.tlaster:precompose:1.3.15")

                // Google
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
                implementation("androidx.compose.material3:material3:1.1.1")
                implementation("androidx.compose.material:material-icons-extended:1.4.3")

                val dataStoreVersion = "1.1.0-alpha04"
                implementation("androidx.datastore:datastore-preferences:$dataStoreVersion")
                implementation("androidx.datastore:datastore-core-okio:$dataStoreVersion")

                // Jetbrains
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

                val ktorVersion = "2.3.1"
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
                implementation("io.github.oshai:kotlin-logging:4.0.2")

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
                api("androidx.core:core-ktx:1.10.1")

                val ktorVersion = "2.3.1"
                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.9.3")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                api(compose.ui)
                api(compose.material3)
                // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
                implementation("ch.qos.logback:logback-classic:1.4.8")
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    namespace = "technology.iatlas.spaceup.common"
}
dependencies {
    implementation("io.ktor:ktor-client-auth:2.3.1")
}
