plugins {
    // Auto-provisions a matching JDK for the Java toolchain (see build.gradle.kts),
    // so the build compiles with JDK 17 regardless of what's installed on the host.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "selenium-boot-idea"
