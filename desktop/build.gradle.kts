import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "technology.iatlas.spaceup"
version = "1.0-SNAPSHOT"

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs) {
                    exclude("org.jetbrains.compose.material")
                }
                implementation("com.bybutter.compose:compose-jetbrains-expui-theme:2.2.0")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            modules("java.instrument", "java.prefs", "java.sql",
                "jdk.unsupported", "jdk.crypto.ec", "jdk.localedata", "java.naming")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.AppImage, TargetFormat.Exe)
            packageName = "NextUI"
            packageVersion = "1.0.0"
            vendor = "Gino Atlas"
            copyright = "iatlas.technology"

            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            macOS {
                iconFile.set(project.file("resources/spaceup_icon.png"))
            }
            windows {
                iconFile.set(project.file("resources/spaceup_icon.png"))
                shortcut = true
            }
            linux {
                iconFile.set(project.file("resources/spaceup_icon.png"))
            }
        }
    }
}
