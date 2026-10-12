---
name: wiki-environments-setup
description: Toolchain requirements, where versions live, and the commands to build and run the mod locally.
---

# Development Setup

## Requirements

| Tool | Version | Note |
|---|---|---|
| JDK | 21 or newer to launch Gradle; 25 to compile | The `foojay-resolver-convention` plugin downloads JDK 25 if you do not have it. |
| Gradle | 9.7.0 | Comes from the committed wrapper. Use `./gradlew`, never a system `gradle`. |
| Git | any recent | |

## Where versions live

`gradle.properties` holds the project's identity only:

```properties
git-org=MCAgents-Mods
git-repo=template
project-version=0.0.0
```

Every dependency coordinate lives in the Gradle version catalog at
`gradle/libs.versions.toml`:

| Entry | Value |
|---|---|
| Java | 25 |
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3 |
| Fabric API | 0.157.0+26.2 |
| Fabric Loom | 1.17.19 |
| NeoForge | 26.2.0.90 |
| ModDevGradle | 2.0.148 |
| Forge | 26.2-65.1.3 |
| ForgeGradle | 7.0.40 |
| MCAgents core | 0.5.0 |

Keep the Minecraft and loader versions the same as core's own catalog. A mod built for a
different Minecraft than core's cannot load beside it.

## MCAgents core

The mod compiles against MCAgents core and does not carry it. Clone `MCAgents-Mods/core`
beside this repository — `../core` — and `settings.gradle` builds the two together. Pass
`-Pcore-build=/path/to/core` for another location. Without a checkout, Gradle resolves the
published `io.github.mcagentsmods:mcagents-core-api` and `mcagents-core-common` at the
version in the catalog.

## Build

```bash
./gradlew build
```

Builds every module, runs the `common/` tests, and writes the three mod jars to
`platforms/fabric/build/libs/`, `platforms/neoforge/build/libs/` and
`platforms/forge/build/libs/`.

## Run

```bash
./gradlew :mcagents-template-fabric:runClient
./gradlew :mcagents-template-neoforge:runClient
./gradlew :mcagents-template-forge:runClient
```

`runClient` does not put core in the development game. Build core and copy its jar for the
same loader into that loader's `run/mods/` first.
