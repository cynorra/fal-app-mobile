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

rootProject.name = "LunaApp"

include(":app")
include(":core:ui")
include(":core:data")
include(":core:database")
include(":core:utils")
include(":feature:onboarding")
include(":feature:home")
include(":feature:fortune")
include(":feature:social")
include(":feature:profile")
