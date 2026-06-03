plugins {
    alias(libs.plugins.molostream.android.library)
    alias(libs.plugins.molostream.android.compose)
}

android {
    namespace = "com.molostream.feature.splash"
}

dependencies {
    implementation(projects.core.designsystem)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.compose)
}
