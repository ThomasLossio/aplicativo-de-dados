pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AplicativoDeDados"
include(":app")

gradle.beforeProject {
    val safeBuildRoot = File(
        providers.environmentVariable("LOCALAPPDATA").orElse(layout.settingsDirectory.dir(".gradle-local").asFile.absolutePath).get(),
        "GradleBuilds/AplicativoDeDados/${path.removePrefix(":").ifBlank { "root" }}",
    )
    layout.buildDirectory.set(safeBuildRoot)
}
