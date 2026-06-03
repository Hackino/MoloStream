import com.android.build.api.dsl.LibraryExtension
import com.molostream.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** Applies the Android library plugin with the project's shared config. */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        // AGP 9+ provides built-in Kotlin support; the kotlin-android plugin
        // must NOT be applied separately.
        pluginManager.apply("com.android.library")
        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
        }
    }
}
