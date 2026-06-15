/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

import java.util.Locale
import java.util.Properties

plugins {
    id("com.android.library")
}

// 1. Create an empty properties object
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

// 2. Load the file if it exists
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

var sdkName  = localProperties.getProperty("SDK_NAME") ?: "\"JUpdater\""
var version = localProperties.getProperty("VERSION") ?: "\"1.0.0\""
val buildNumber = localProperties.getProperty("BUILDNUMBER") ?: "1"

android {
    namespace = "com.qtlws.android.jupdater"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        consumerProguardFiles("proguard-rules.pro")

        // Pass version info to BuildConfig
        buildConfigField("String", "SDK_NAME", "\"${sdkName}\"")
        buildConfigField("String", "VERSION", "\"${version}\"")
        buildConfigField("String", "BUILDNUMBER", "\"${buildNumber}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        buildConfig = true
    }

    publishing {
        singleVariant("release")
    }

    // Optional: To generate consistent filenames
    libraryVariants.all {
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.LibraryVariantOutputImpl
            val variantName = output.baseName.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
//            val buildType = output.b.name.capitalize()
            output.outputFileName = "jupdater-V${version}-${variantName.toLowerCase()}.aar"
        }
    }


}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
}

tasks.register("buildDebugAar") {
    dependsOn(":app:assembleDebug") // Task to build the debug variant
    description = "Builds the debug AAR for the jupdater library."
    group = "build"
}

tasks.register("buildReleaseAar") {
    dependsOn(":app:assembleRelease")
    description = "Builds the release AAR for the jupdater library."
    group = "build" // This will group it under the 'build' category in the Gradle window
}
