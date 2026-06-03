import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

// Root build file. Plugins are declared (but not applied) here so module
// build files can apply them by alias; real configuration lives in the
// :build-logic convention plugins.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

// Apply ktlint formatting + detekt static analysis to every module
// (gives :ktlintCheck / :ktlintFormat and :detekt). A single shared detekt
// config lives in config/detekt/detekt.yml and layers on top of the defaults.
subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    extensions.configure<DetektExtension> {
        buildUponDefaultConfig = true
        parallel = true
        config.setFrom(rootProject.file("config/detekt/detekt.yml"))
    }

    tasks.withType<Detekt>().configureEach {
        // Detekt 1.23.x parses with its bundled compiler; skip type-resolution
        // tasks here to keep the pre-commit run fast and dependency-free.
        reports {
            html.required.set(true)
            sarif.required.set(false)
            md.required.set(false)
            txt.required.set(false)
            xml.required.set(false)
        }
    }
}

// --- Git hooks ---------------------------------------------------------------
// Points git at the version-controlled `.githooks/` directory (which holds the
// pre-commit / pre-push lint hooks) and makes the scripts executable. Runs
// automatically on IDE sync / build, so the hooks install themselves with no
// manual setup. No-ops cleanly outside a git checkout.
val installGitHooks by tasks.registering {
    group = "git hooks"
    description = "Installs project git hooks via core.hooksPath = .githooks."
    val gitDir = rootProject.file(".git")
    val hooksDir = rootProject.file(".githooks")
    onlyIf { gitDir.exists() }
    doLast {
        providers.exec {
            workingDir = rootDir
            commandLine("git", "config", "core.hooksPath", ".githooks")
        }.result.get().assertNormalExitValue()
        hooksDir.listFiles()?.forEach { it.setExecutable(true) }
    }
}

tasks.matching { it.name == "prepareKotlinBuildScriptModel" }.configureEach {
    dependsOn(installGitHooks)
}
