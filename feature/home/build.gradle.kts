plugins {
    alias(libs.plugins.molostream.android.library)
    alias(libs.plugins.molostream.android.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.molostream.feature.home"

    defaultConfig {
        // Keep rules for this module's @Serializable catalog DTOs travel with
        // the AAR and are merged into the consuming app's R8 run.
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.domain)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
}
