plugins {
    alias(libs.plugins.molostream.android.library)
    alias(libs.plugins.molostream.android.compose)
}

android {
    namespace = "com.molostream.core.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(libs.coil.compose)
}
