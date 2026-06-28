# Selenium Boot — Ecosystem

Home for **new, greenfield IDE tooling** around the
[Selenium Boot](https://seleniumboot.github.io) automation framework.

The "ecosystem" is the [`seleniumboot`](https://github.com/seleniumboot) GitHub
org as a whole — several repositories, cross-linked — not a single folder.
Mature, already-published projects keep their own repos and release pipelines;
this monorepo only houses tooling that doesn't yet have one.

> Goal: don't build a generic Selenium toolbox — build an experience
> specifically for the Selenium Boot ecosystem.

## Ecosystem map

### In this monorepo (greenfield IDE tooling)

| Directory | What it is | Status |
|---|---|---|
| [`intellij-plugin/`](intellij-plugin/) | IntelliJ IDEA plugin — `selenium-boot.yml` schema, project wizard, run config | 🟢 active (MVP) |
| `vscode-extension/` | VS Code extension (Gherkin nav, step generation, run) | ⚪ planned (only if greenfield; otherwise its own repo) |

### Sibling repos (own release pipelines — not housed here)

| Repo | What it is | Publishes to |
|---|---|---|
| [`seleniumboot/selenium-boot`](https://github.com/seleniumboot/selenium-boot) | The core framework JAR `io.github.seleniumboot:selenium-boot` | Maven Central |
| [`seleniumboot/selenium-mcp`](https://github.com/seleniumboot/selenium-mcp) | `seleniumboot-mcp` server (84 tools) — AI test generation & control | PyPI |

Each repo is independently buildable, versioned, and published, and keeps its
own README.

## What belongs where

Rule of thumb:

- **Already published with its own release pipeline → its own repo.**
  (the core JAR on Maven Central, the MCP server on PyPI).
- **Brand-new with no repo or pipeline yet → this monorepo.**
  (the IntelliJ plugin today).

This keeps the core library a clean, dependency-light JAR — IDE/Gradle/Node
tooling never leaks into it — and avoids rewriting the history or release
machinery of projects that already have a home.

## Relationship to the core library

These tools target a specific Selenium Boot release line. When the framework
publishes a new version, the tooling here is updated to match (e.g. the
IntelliJ project wizard pins the dependency version, the YAML schema tracks new
config keys). The IntelliJ plugin reuses the MCP server's generators rather than
re-implementing them.

## Getting started

See each project's own `README.md`. For the IntelliJ plugin:

```bash
cd intellij-plugin
# Gradle 8.10 needs JDK 17–21 (not Java 22+):
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew runIde
```
