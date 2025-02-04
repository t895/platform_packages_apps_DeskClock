import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt()))
    }
}

android {
    namespace = "com.android.deskclock"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.android.deskclock"
        minSdk = libs.versions.minSdk.get().toInt()
        //noinspection ExpiredTargetSdkVersion
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 15
        versionName = versionCode.toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
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

    sourceSets.getByName("main") {
        assets.srcDir("../assets")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(getDefaultProguardFile("proguard-android.txt"))
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
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.preference)
    implementation(libs.google.material)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.percentlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)
}
