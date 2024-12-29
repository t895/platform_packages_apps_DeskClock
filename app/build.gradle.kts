import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val packageName = "com.android.deskclock"
android {
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    namespace = packageName

    defaultConfig {
        versionCode = 15
        versionName = "1.0.001"
        minSdk = 35
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 29
    }

    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val useKeystoreProperties = keystorePropertiesFile.canRead()
    val keystoreProperties = Properties()
    if (useKeystoreProperties) {
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    }

    if (useKeystoreProperties) {
        signingConfigs {
            create("release") {
                storeFile = rootProject.file(keystoreProperties["storeFile"]!!)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                enableV4Signing = true
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            signingConfig = if (useKeystoreProperties) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
            applicationIdSuffix = ".preview"
            resValue("string", "app_label", "Preview Clock")
        }

        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_label", "Clock d")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    sourceSets.getByName("main") {
        assets.srcDir("../assets")
        manifest.srcFile("../AndroidManifest.xml")
        java.srcDirs("../src")
        res.srcDir("../res")
    }
}

dependencies {
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.core:core:1.15.0")
    implementation("androidx.legacy:legacy-support-v13:1.0.0")
    implementation("androidx.legacy:legacy-preference-v14:1.0.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
}
