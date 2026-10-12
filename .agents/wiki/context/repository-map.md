---
name: agent-wiki-context-repository-map
description: Orientation for agents working on MCAgentsTemplate — what exists today, where things go, the build commands, and the gotchas that bite first.
---

# Repository Map

Read this before touching anything. It says what is in the repository right now. The design
lives in [`../../../wiki/information/architecture.md`](../../../wiki/information/architecture.md).

## What exists right now

```
AGENTS.md            entry point for agents
PROMPT.md            one-time setup: the placeholders to replace for a new mod (deleted after)
.claude/CLAUDE.md    one import of AGENTS.md, so Claude Code reads the same file
README.md            human overview
LICENSE              MIT license, MCAgents-Mods, 2026
.agents/             this repository's local instruction set, indexes and agent wiki
wiki/                human documentation and versioned change logs
.github/workflows/   publish.yml — publishes and tags each new version that reaches master
settings.gradle      module map, plugin repositories, the core composite build
build.gradle         group and version for every module, the three loader plugins
gradle.properties    project identity only — organization, repository, version
gradle/              the wrapper and libs.versions.toml
api/                 mcagents-template-api
common/              mcagents-template-common
platforms/shared/    mcagents-template-shared
platforms/fabric/    mcagents-template-fabric
platforms/neoforge/  mcagents-template-neoforge
platforms/forge/     mcagents-template-forge
```

## The six modules

| Path | Gradle module | Holds today |
|---|---|---|
| `api/` | `mcagents-template-api` | `MCAgentsTemplate` — the mod id, name and on-disk directory names. |
| `common/` | `mcagents-template-common` | `TemplatePaths` — the mod's directory under a game directory — and its test. |
| `platforms/shared/` | `mcagents-template-shared` | `TemplateRuntime.start(Path)`, the start-up every loader calls. |
| `platforms/fabric/` | `mcagents-template-fabric` | `TemplateFabricClient` (`ClientModInitializer`) and `fabric.mod.json`. |
| `platforms/neoforge/` | `mcagents-template-neoforge` | `TemplateNeoForgeClient` (`@Mod`, client only) and `META-INF/neoforge.mods.toml`. |
| `platforms/forge/` | `mcagents-template-forge` | `TemplateForgeMod` (`@Mod`, starts on the client only) and `META-INF/mods.toml`. |

`settings.gradle` maps the Gradle names to those directories; the directory names and the
module names deliberately differ, so this mod's `:api` never collides with core's in the
composite build.

The dependency chain is one-way: `api/` ← `common/` ← `platforms/shared/` ← each loader
module. Core sits under `api/` and `common/` as `compileOnlyApi`.

## Commands

| Task | Command |
|---|---|
| Build everything and run the tests | `./gradlew build` |
| Run the `common/` tests only | `./gradlew :mcagents-template-common:test` |
| Fabric dev client | `./gradlew :mcagents-template-fabric:runClient` |
| NeoForge dev client | `./gradlew :mcagents-template-neoforge:runClient` |
| Forge dev client | `./gradlew :mcagents-template-forge:runClient` |

The mod jars land in `platforms/{fabric,neoforge,forge}/build/libs/`.

## Gotchas

* **Core comes from a checkout beside the repository, or from GitHub Packages.**
  `settings.gradle` builds `../core` (or `-Pcore-build=…`) together with this mod. Without a
  checkout Gradle reads the published core artifacts from GitHub Packages, which needs
  `GITHUB_ACTOR` and `GITHUB_TOKEN` even though core is public, and fails without them.
* **Merging a version above `0.0.0` publishes it.** The Publish workflow ships and tags any
  new `project-version` the moment it reaches `master`, and does nothing at `0.0.0`. A
  published version can never be overwritten, so the version changes in the release task
  alone, and the core version the catalog pins must already be published.
* **JDK 25 compiles, JDK 21 or newer launches Gradle.** The foojay resolver downloads JDK 25
  if it is missing.
* **The first build is slow.** Loom, ModDevGradle and ForgeGradle each fetch and prepare
  their own copy of Minecraft.
* **`runClient` does not put core in the development game.** Copy core's jar for the same
  loader into that loader's `run/mods/`.
* **Nothing has been run in a real game yet.** The jars build; loading them is unproven.
