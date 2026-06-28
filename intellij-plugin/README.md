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
| New Project wizard (Spring-Initializr style) | 🚧 roadmap | Scaffolds `pom.xml` (correct dep version), `selenium-boot.yml`, a `BaseTest` subclass + sample page object. |
| Selenium Boot run/debug configuration | 🚧 roadmap | Reuses native test gutters; injects profile / headless / env. |

Everything AI-related (test generation, failure analysis, locator suggestions)
ships as **MCP server registration docs** for JetBrains AI Assistant — not
bespoke plugin code — until the basics have users.

## Build & run

```bash
# one-time: generate the Gradle wrapper
gradle wrapper --gradle-version 8.10

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

## Layout

```
intellij-plugin/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── src/main/
    ├── java/io/github/seleniumboot/idea/
    │   └── config/                       # JSON Schema wiring  ✅
    │       ├── SeleniumBootSchemaProvider.java
    │       └── SeleniumBootSchemaProviderFactory.java
    └── resources/
        ├── META-INF/plugin.xml
        └── schemas/selenium-boot.schema.json
```

## Roadmap implementation notes

**New Project wizard** — implement a `ModuleBuilder` (or, on newer platforms, a
`GeneratorNewProjectWizard`) under `…/wizard/`. The generator should emit a
`pom.xml` pinned to the current `io.github.seleniumboot:selenium-boot` release,
a starter `selenium-boot.yml`, and a `BaseTest` subclass. Reuse the framework's
own MCP generators (`generate_java_testng`, `generate_java_page_object`) for the
sample files rather than re-templating them here. Register via the
`com.intellij.moduleBuilder` extension point (see the commented block in
`plugin.xml`).

**Run configuration** — add a `ConfigurationType` + factory under `…/run/` that
wraps the Maven/TestNG run with Selenium Boot knobs (`-Denv=…`, headless,
profile). Do **not** add gutter icons for plain test methods — IntelliJ already
provides those; only contribute a `RunLineMarkerContributor` if you need a
Selenium-Boot-specific action distinct from the native one.
