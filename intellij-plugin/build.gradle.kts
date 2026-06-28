import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
    java
    // IntelliJ Platform Gradle Plugin 2.x — the current, supported toolchain.
    // https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

java {
    // Match the framework: Java 17.
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(
            IntelliJPlatformType.IntellijIdeaCommunity,
            providers.gradleProperty("platformVersion").get()
        )

        bundledPlugins(
            providers.gradleProperty("platformBundledPlugins").get()
                .split(',').map(String::trim).filter(String::isNotEmpty)
        )

        pluginVerifier()
        zipSigner()
    }
}

intellijPlatform {
    pluginConfiguration {
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            // untilBuild left open so the plugin survives minor IDE upgrades.
            untilBuild = provider { null }
        }
    }

    // `./gradlew verifyPlugin` checks API compatibility against recent IDEs.
    pluginVerification {
        ides {
            recommended()
        }
    }
}

tasks {
    // `./gradlew runIde` launches a sandbox IDE with the plugin installed.
    runIde {
        // Uncomment to develop against IntelliJ IDEA Ultimate instead of Community.
        // type = IntelliJPlatformType.IntellijIdeaUltimate
    }
}
