plugins {
    alias(libs.plugins.molostream.android.library)
}

android {
    namespace = "com.molostream.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
