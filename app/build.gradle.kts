plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    jacoco
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

apply(from = rootProject.file("gradle/coverage.gradle.kts"))

android {
    namespace = "com.prosincerity.ghostwriter"
    ndkVersion = "30.0.16248370"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.prosincerity.ghostwriter"
        minSdk = 24
        targetSdk = 37
        versionCode = 6
        versionName = "1.3.0-beta.2"

        testInstrumentationRunner = if (providers.gradleProperty("ghostwriter.releaseCheck").orNull == "true")
            "com.prosincerity.ghostwriter.dictionary.data.EspeakReleaseCheckInstrumentation"
        else "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["testProviderAuthority"] = "com.prosincerity.ghostwriter.test"
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
        externalNativeBuild {
            cmake {
                targets += "ghostwriter_ipa"
            }
        }
    }

    signingConfigs {
        create("releaseVerification") {
            if (providers.gradleProperty("ghostwriter.releaseCheck").orNull == "true") {
                val releaseStore = providers.environmentVariable("GHOSTWRITER_RELEASE_KEYSTORE").orNull
                    ?: throw GradleException("Run python3 scripts/build_release.py --device DEVICE_SERIAL for signed release verification")
                storeFile = file(releaseStore)
                storeType = "PKCS12"
                keyAlias = providers.environmentVariable("GHOSTWRITER_RELEASE_KEY_ALIAS").get()
                storePassword = providers.environmentVariable("GHOSTWRITER_STORE_PASSWORD").get()
                keyPassword = storePassword
            }
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            optimization {
                enable = true
            }
        }
        create("releaseCheck") {
            initWith(getByName("release"))
            applicationIdSuffix = ".releasecheck"
            signingConfig = signingConfigs.getByName("releaseVerification")
            manifestPlaceholders["testProviderAuthority"] = "com.prosincerity.ghostwriter.releasecheck.test"
            matchingFallbacks += listOf("release")
        }
    }
    if (providers.gradleProperty("ghostwriter.releaseCheck").orNull == "true") {
        testBuildType = "releaseCheck"
    }
    testCoverage {
        // Aggregated reports require the same JaCoCo version for JVM and Android tests.
        jacocoVersion = libs.versions.jacoco.get()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "4.1.2"
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.json)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
