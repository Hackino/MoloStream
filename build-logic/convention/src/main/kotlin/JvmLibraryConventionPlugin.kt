import com.molostream.buildlogic.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

/** Pure-JVM Kotlin module (no Android), e.g. :core:model and :core:domain. */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.jvm")
        configureKotlinJvm()
    }
}
