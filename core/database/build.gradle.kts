plugins {
    alias(libs.plugins.molostream.android.library)
    alias(libs.plugins.molostream.android.room)
}

android {
    namespace = "com.molostream.core.database"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.android)
}
