plugins {
    // Auto-provisions a matching JDK for the Java toolchain (see build.gradle.kts),
    // so the build compiles with JDK 17 regardless of what's installed on the host.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

val currentJavaVersion = JavaVersion.current()
if (currentJavaVersion < JavaVersion.VERSION_17 || currentJavaVersion > JavaVersion.VERSION_21) {
    throw GradleException(
        "This project requires Gradle to run on JDK 17-21, but it was launched with $currentJavaVersion. " +
        "Please set JAVA_HOME to a supported JDK as described in README.md."
    )
}

rootProject.name = "selenium-boot-idea"
