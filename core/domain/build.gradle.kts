plugins {
    alias(libs.plugins.molostream.jvm.library)
}

dependencies {
    api(projects.core.model)
    implementation(libs.kotlinx.coroutines.core)
}
