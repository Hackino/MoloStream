plugins {
    alias(libs.plugins.molostream.android.library)
}

android {
    namespace = "com.molostream.core.datastore"
}

dependencies {
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.android)
}
