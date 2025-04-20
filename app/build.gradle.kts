import java.util.Locale

plugins {
    id("com.android.library")
}

var versionName = "1.0.2"
var versionCode = 3

android {
    namespace = "com.qtlws.android.jupdater"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        consumerProguardFiles("proguard-rules.pro")

        // Pass version info to BuildConfig
        buildConfigField("String", "SDK_NAME", "\"JUpdater\"")
        buildConfigField("String", "VERSION", "\"${versionName}\"")
        buildConfigField("String", "BUILDNUMBER", "\"${versionCode}\"")
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
            output.outputFileName = "jupdater-V${versionName}-${variantName.toLowerCase()}.aar"
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
