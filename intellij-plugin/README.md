# Selenium Boot — IntelliJ IDEA plugin

IDE support for the [Selenium Boot](https://seleniumboot.github.io) automation
framework. Built on the **IntelliJ Platform Gradle Plugin 2.x**, targeting
IntelliJ IDEA Community 2024.2+ (Java 17).

> Scope note: this plugin deliberately does **not** re-implement what IntelliJ
> already does for generic Java (TestNG/JUnit gutters, Cucumber/Gherkin). It
> only adds the Selenium-Boot-specific delta.

## MVP scope

| Feature | Status | Notes |
|---|---|---|
| `selenium-boot.yml` schema (completion + validation + docs) | ✅ shipped | Zero custom UI — a JSON Schema bound via the JSON Schema SPI. |
| New Project wizard (Spring-Initializr style) | ✅ shipped | Scaffolds `pom.xml` (pinned dep version), `selenium-boot.yml`, `testng.xml`, a sample `BaseTest` test + `BasePage` page object. Options: group/artifact, base URL, browser, headless, version. |
| Selenium Boot run/debug configuration | ✅ shipped | Runs the suite via Maven (`mvnw`/`mvn`) with real knobs: config profile (`-Dselenium.boot.profile`), config file (`-Dselenium.boot.config`), test filter (`-Dtest`), goals, extra args. Native test gutters left untouched. |

Everything AI-related (test generation, failure analysis, locator suggestions)
ships as **MCP server registration docs** for JetBrains AI Assistant — not
bespoke plugin code — until the basics have users.

## Build & run

The Gradle wrapper is committed, so no local Gradle install is needed.

> **JDK note:** Gradle 8.10 runs on JDK 17–21, **not** Java 22+. If your default
> `java` is newer, point the wrapper at a supported JDK first:
> ```bash
> export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
> ```

```bash
# launch a sandbox IDE with the plugin installed
./gradlew runIde

# build the distributable ZIP (build/distributions/)
./gradlew buildPlugin

# verify API compatibility against recent IDEs
./gradlew verifyPlugin
```

To try the schema: in the sandbox IDE, open any `selenium-boot.yml`. You should
get completion for keys like `browser.name` (enum: chrome/firefox/edge/safari),
`execution.parallel`, validation on out-of-range numbers, and hover docs.

## Publishing to the JetBrains Marketplace

CI (`.github/workflows/intellij-plugin.yml`) builds and verifies every push.
Publishing is a manual, token-gated step:

```bash
# Permanent token from https://hub.jetbrains.com (Marketplace publishing scope)
export INTELLIJ_PLATFORM_PUBLISH_TOKEN=...   # required

# Optional plugin signing (recommended) — https://plugins.jetbrains.com/docs/intellij/plugin-signing.html
export INTELLIJ_SIGNING_CERT_CHAIN=/path/chain.crt
export INTELLIJ_SIGNING_PRIVATE_KEY=/path/private.pem
export INTELLIJ_SIGNING_PASSWORD=...

./gradlew publishPlugin
```

The **first** submission for a new plugin ID goes through JetBrains manual
moderation (typically a couple of days) before it appears on the Marketplace.

## Layout

```
intellij-plugin/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── src/main/
    ├── java/io/github/seleniumboot/idea/
    │   ├── config/                       # JSON Schema wiring  ✅
    │   │   ├── SeleniumBootSchemaProvider.java
    │   │   └── SeleniumBootSchemaProviderFactory.java
    │   ├── wizard/                        # New Project wizard  ✅
    │   │   ├── SeleniumBootModuleBuilder.java   # wizard entry + scaffolding
    │   │   ├── SeleniumBootWizardStep.java      # options form
    │   │   └── ProjectScaffold.java             # file templates (no platform deps)
    │   └── run/                           # Run/debug configuration  ✅
    │       ├── SeleniumBootRunConfigurationType.java
    │       ├── SeleniumBootRunConfigurationFactory.java
    │       ├── SeleniumBootRunConfiguration.java   # state + persistence
    │       ├── SeleniumBootSettingsEditor.java     # settings UI
    │       └── SeleniumBootRunState.java           # builds the mvn command
    └── resources/
        ├── META-INF/plugin.xml
        └── schemas/selenium-boot.schema.json
```

## Implementation notes

**New Project wizard** (`…/wizard/`) — a `ModuleBuilder` registered via the
`com.intellij.moduleBuilder` extension point. `ProjectScaffold` holds the file
templates and has no IntelliJ Platform dependencies, so it's trivially testable.
Templates are pinned to a Selenium Boot version (default
`ProjectScaffold.DEFAULT_SELENIUM_BOOT_VERSION`) — bump it when the framework
releases. A future improvement is to source the sample files from the framework's
own MCP generators (`generate_java_testng`, `generate_java_page_object`) instead
of local templates.

## Roadmap implementation notes

**Run configuration** — add a `ConfigurationType` + factory under `…/run/` that
wraps the Maven/TestNG run with Selenium Boot knobs (`-Denv=…`, headless,
profile). Do **not** add gutter icons for plain test methods — IntelliJ already
provides those; only contribute a `RunLineMarkerContributor` if you need a
Selenium-Boot-specific action distinct from the native one.
