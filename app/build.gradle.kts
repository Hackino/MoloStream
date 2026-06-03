import java.util.Properties

plugins {
    alias(libs.plugins.molostream.android.application)
    alias(libs.plugins.molostream.android.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.molostream.app"

    defaultConfig {
        applicationId = "com.molostream.app"
        // Auto-bumped in CI: the workflow exports VERSION_CODE/VERSION_NAME from the
        // run number. Locally (no env) these fall back to the base 1 / "1.0".
        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = System.getenv("VERSION_NAME") ?: "1.0"
    }

    // ── Signing ────────────────────────────────────────────────────────────────
    // Priority: keystore.properties (local dev, from setup-wizard) →
    //           env vars (CI / GitHub Secrets) →
    //           debug signing fallback (so assembleRelease still works without any keystore).
    val keystorePropsFile = file("keystore.properties")
    val keystoreProps =
        Properties().also { props ->
            if (keystorePropsFile.exists()) props.load(keystorePropsFile.inputStream())
        }

    val hasLocalKeystore = keystoreProps.isNotEmpty()
    val hasCiKeystore = !System.getenv("KEYSTORE_FILE").isNullOrBlank()
    val hasReleaseKeystore = hasLocalKeystore || hasCiKeystore

    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                if (hasLocalKeystore) {
                    storeFile = file(keystoreProps["storeFile"] as String)
                    storePassword = keystoreProps["storePassword"] as String
                    keyAlias = keystoreProps["keyAlias"] as String
                    keyPassword = keystoreProps["keyPassword"] as String
                } else {
                    storeFile = file(System.getenv("KEYSTORE_FILE")!!)
                    storePassword = System.getenv("KEYSTORE_PASSWORD")
                    keyAlias = System.getenv("KEY_ALIAS")
                    keyPassword = System.getenv("KEY_PASSWORD")
                }
            }
        }
    }

    buildTypes {
        release {
            // R8 full mode: shrinks, obfuscates, and optimises.
            // Resource shrinking strips unreferenced res entries on top of code shrinking.
            // Keep rules: this module's proguard-rules.pro + each library's consumer-rules.pro.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (hasReleaseKeystore) signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    // Features
    implementation(projects.feature.splash)
    implementation(projects.feature.home)
    implementation(projects.feature.player)

    // Core (modules wired into Koin at startup)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.datastore)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation 3
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
