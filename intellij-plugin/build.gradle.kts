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
    // IntelliJ IDEA 2024.2 runs on JDK 21, so plugin bytecode targets 21.
    // A toolchain (auto-provisioned via the foojay resolver in
    // settings.gradle.kts) compiles with a real JDK 21 regardless of which
    // JDK launched Gradle.
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
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

        // Required by the code-instrumentation step (instrumentCode / buildPlugin).
        instrumentationTools()

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

    // `./gradlew verifyPlugin` checks API compatibility. Verify against the
    // locally-installed IDE when present (no download, and it's the build users
    // here actually run); otherwise pin to the build target so CI is deterministic.
    pluginVerification {
        ides {
            val localIde = providers.environmentVariable("SB_VERIFY_IDE_HOME")
                .orElse("/snap/intellij-idea-ultimate/current")
            val ideDir = file(localIde.get())
            if (ideDir.resolve("build.txt").isFile) {
                local(ideDir)
            } else {
                ide(IntelliJPlatformType.IntellijIdeaCommunity,
                    providers.gradleProperty("platformVersion").get())
            }
        }
    }

    // `./gradlew publishPlugin` uploads to the JetBrains Marketplace.
    // Token comes from a permanent token created at https://hub.jetbrains.com.
    publishing {
        token = providers.environmentVariable("INTELLIJ_PLATFORM_PUBLISH_TOKEN")
        // First release goes to the default (stable) channel.
        channels = listOf("default")
    }

    // Optional plugin signing (recommended by JetBrains). Configured only when
    // the signing env vars are present, so unsigned local builds still work.
    // See: https://plugins.jetbrains.com/docs/intellij/plugin-signing.html
    signing {
        certificateChainFile = providers.environmentVariable("INTELLIJ_SIGNING_CERT_CHAIN")
            .map { file(it) }.orNull
        privateKeyFile = providers.environmentVariable("INTELLIJ_SIGNING_PRIVATE_KEY")
            .map { file(it) }.orNull
        password = providers.environmentVariable("INTELLIJ_SIGNING_PASSWORD").orNull
    }
}

tasks {
    // `./gradlew runIde` launches a sandbox IDE with the plugin installed.
    runIde {
        // Uncomment to develop against IntelliJ IDEA Ultimate instead of Community.
        // type = IntelliJPlatformType.IntellijIdeaUltimate
    }
}
