import com.android.build.api.dsl.ApplicationExtension
import com.molostream.buildlogic.ProjectConfig
import com.molostream.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** Applies the Android application plugin with the project's shared config. */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        // AGP 9+ provides built-in Kotlin support; the kotlin-android plugin
        // must NOT be applied separately.
        pluginManager.apply("com.android.application")
        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = ProjectConfig.TARGET_SDK
        }
    }
}
