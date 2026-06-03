import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.molostream.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Enables Jetpack Compose for an Android module (application or library) and
 * wires the BOM-managed Compose dependencies. Apply on top of the
 * application/library convention plugin.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        val commonExtension = extensions.findByType(ApplicationExtension::class.java)
            ?: extensions.findByType(LibraryExtension::class.java)
            ?: error("Apply an Android application/library plugin before the Compose convention plugin")

        configureCompose(commonExtension)
    }

    private fun Project.configureCompose(commonExtension: CommonExtension) {
        commonExtension.buildFeatures.compose = true

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))

            add("implementation", lib("androidx-compose-ui"))
            add("implementation", lib("androidx-compose-ui-graphics"))
            add("implementation", lib("androidx-compose-ui-tooling-preview"))
            add("implementation", lib("androidx-compose-foundation"))
            add("implementation", lib("androidx-compose-material3"))
            add("implementation", lib("androidx-compose-material-icons-extended"))

            add("debugImplementation", lib("androidx-compose-ui-tooling"))
        }
    }

    private fun Project.lib(alias: String) = libs.findLibrary(alias).get()
}
