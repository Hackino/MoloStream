plugins {
    alias(libs.plugins.molostream.android.library)
}

android {
    namespace = "com.molostream.core.data"
}

dependencies {
    api(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.android)
}
