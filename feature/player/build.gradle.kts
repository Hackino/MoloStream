plugins {
    alias(libs.plugins.molostream.android.library)
    alias(libs.plugins.molostream.android.compose)
}

android {
    namespace = "com.molostream.feature.player"

    defaultConfig {
        // Keep rules for the Media3 / ExoPlayer / IMA stack travel with the AAR
        // and are merged into the consuming app's R8 run.
        consumerProguardFiles("consumer-rules.pro")
    }
}

kotlin {
    compilerOptions {
        // Media3's ExoPlayer/IMA APIs we rely on are marked @UnstableApi.
        optIn.add("androidx.media3.common.util.UnstableApi")
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.domain)

    // Media3 / ExoPlayer + HLS + Google IMA ads
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.exoplayer.ima)
    implementation(libs.media3.ui)
    implementation(libs.media3.common)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
}
