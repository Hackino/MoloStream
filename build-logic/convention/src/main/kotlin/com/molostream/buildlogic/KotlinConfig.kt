package com.molostream.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private val JAVA = JavaVersion.VERSION_17
private val JVM = JvmTarget.JVM_17

/** Shared Android + Kotlin configuration for application and library modules. */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = ProjectConfig.COMPILE_SDK
        defaultConfig.minSdk = ProjectConfig.MIN_SDK
        compileOptions.sourceCompatibility = JAVA
        compileOptions.targetCompatibility = JAVA
        // Required by Media3 IMA (and good hygiene at minSdk 24).
        compileOptions.isCoreLibraryDesugaringEnabled = true
    }
    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("android-desugar-jdk-libs").get())
    }
    configureKotlin()
}

/** Shared configuration for pure-JVM (non-Android) Kotlin modules. */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JAVA
        targetCompatibility = JAVA
    }
    configureKotlin()
}

private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JVM)
        }
    }
}
