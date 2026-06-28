# Selenium Boot — Ecosystem

Developer tooling around the [Selenium Boot](https://seleniumboot.github.io)
automation framework. The core library (the Maven Central JAR
`io.github.seleniumboot:selenium-boot`) lives in its own repository; this
monorepo holds the **IDE and AI tooling** that sits on top of it.

> Goal: don't build a generic Selenium toolbox — build an experience
> specifically for the Selenium Boot ecosystem.

## Projects

| Directory | What it is | Status |
|---|---|---|
| [`intellij-plugin/`](intellij-plugin/) | IntelliJ IDEA plugin — `selenium-boot.yml` schema, project wizard, run config | 🟢 active (MVP) |
| `vscode-extension/` | VS Code extension (Gherkin nav, step generation, run) | ⚪ planned / to migrate |
| `mcp-server/` | `seleniumboot-mcp` server (84 tools) — AI test generation & control | ⚪ planned / to migrate |

Each project is independently buildable and publishable (JetBrains Marketplace,
VS Code Marketplace, npm) and keeps its own README with build instructions.

## Why a monorepo

- One place for the whole tooling story; shared docs, issues, and CI.
- The tools share concepts (config schema, generators, MCP) and evolve together
  with the framework's releases.
- The core library stays a clean, dependency-light JAR — IDE/Gradle/Node
  tooling never leaks into it.

## Relationship to the core library

These tools target a specific Selenium Boot release line. When the framework
publishes a new version, the tooling here is updated to match (e.g. the
IntelliJ project wizard pins the dependency version, the YAML schema tracks new
config keys).

## Getting started

See each project's own `README.md`. For the IntelliJ plugin:

```bash
cd intellij-plugin
gradle wrapper --gradle-version 8.10   # one-time
./gradlew runIde
```
