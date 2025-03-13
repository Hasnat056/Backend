pluginManagement {
    repositories {
        // Keep Google repo for Android Studio
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Keep Google repo for Android Studio
        google()
        mavenCentral()
    }
}

rootProject.name = "Ktor-Backend"

// Conditionally include modules
if (!System.getenv().containsKey("DEPLOYING_ON_RAILWAY")) {
    // Android Studio will see both modules
    include(":app")
}
include(":Ktor-Backend")