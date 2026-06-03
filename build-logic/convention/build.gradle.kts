plugins {
    `kotlin-dsl`
}

group = "com.molostream.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // The convention plugins apply AGP / Kotlin / KSP / Compose; expose them on
    // the build-logic classpath as compileOnly artifacts.
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

// Register every convention plugin by id so modules can apply them by alias.
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "molostream.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "molostream.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "molostream.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidRoom") {
            id = "molostream.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("jvmLibrary") {
            id = "molostream.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}
