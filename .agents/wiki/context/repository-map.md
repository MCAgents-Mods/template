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
.github/workflows/   publish.yml — publishes the library modules, tags each new version on master
                     release.yml — attaches a jar per loader to each tag's release
settings.gradle      module map, plugin repositories, the core composite build
build.gradle         group and version for every module, the three loader plugins
gradle.properties    project identity only — organization, repository, version
gradle/              the wrapper and libs.versions.toml
api/                 template-api
common/              template-common
platforms/shared/    template-shared: build.gradle and src/
platforms/fabric/    template-fabric: build.gradle and src/
platforms/neoforge/  template-neoforge: build.gradle and src/
platforms/forge/     template-forge: build.gradle and src/
```

## The six modules

| Path | Gradle module | Holds today |
|---|---|---|
| `api/` | `template-api` | `MCAgentsTemplate` — the mod id, name and on-disk directory names. |
| `common/` | `template-common` | `TemplatePaths` — the mod's directory under a game directory — and its test. |
| `platforms/shared/` | `template-shared` | `TemplateRuntime.start(Path)`, the start-up every loader calls. |
| `platforms/fabric/` | `template-fabric` | `TemplateFabricClient` (`ClientModInitializer`) and `fabric.mod.json`. |
| `platforms/neoforge/` | `template-neoforge` | `TemplateNeoForgeClient` (`@Mod`, client only) and `META-INF/neoforge.mods.toml`. |
| `platforms/forge/` | `template-forge` | `TemplateForgeMod` (`@Mod`, starts on the client only) and `META-INF/mods.toml` and `pack.mcmeta`. |

`settings.gradle` maps the Gradle names to those directories; the directory names and the
module names deliberately differ, so this mod's `:api` never collides with core's in the
composite build.

The dependency chain is one-way: `api/` ← `common/` ← `platforms/shared/` ← each loader
module. Core sits under `api/` and `common/` as `compileOnlyApi`.

Only the loader modules declare `implementation`, and only for this mod's own modules: each of
`platforms/{fabric,forge,neoforge}` implements `template-api`, `template-common` and the shared
module. Everything provided at runtime by someone else is `compileOnly`: core, Fabric Loader,
Fabric API and Forge, the last three with a `runtimeOnly` line for `runClient`. Loader modules
are never published, so neither scope reaches anything else. What a jar carries comes only from
its `jar` task's merge (and, for a third-party library, `include` or `jarJar`).

## Commands

| Task | Command |
|---|---|
| Build everything and run the tests | `./gradlew build` |
| Run the `common/` tests only | `./gradlew :template-common:test` |
| Fabric dev client | `./gradlew :template-fabric:runClient` |
| NeoForge dev client | `./gradlew :template-neoforge:runClient` |
| Forge dev client | `./gradlew :template-forge:runClient` |

The mod jars land in `platforms/{fabric,neoforge,forge}/build/libs/`, named
`MCAgentsTemplate{Loader}-{version}+{minecraft}.jar`.

## Gotchas

* **Core comes from a checkout beside the repository, or from GitHub Packages.**
  `settings.gradle` builds `../core` (or `-Pcore-build=…`) together with this mod. Without a
  checkout Gradle reads the published core artifacts from GitHub Packages, which needs
  `GITHUB_ACTOR` and `GITHUB_TOKEN` even though core is public, and fails without them.
* **Merging a version above `0.0.0` publishes it.** The Publish workflow ships and tags any
  new `project-version` the moment it reaches `master`, and does nothing at `0.0.0`; the
  Release workflow then attaches the Forge, Fabric and NeoForge jars to that tag's GitHub
  release. A published version can never be overwritten, so the version changes in the
  release task alone, and the core version the catalog pins must already be published.
* **Gradle runs on Java 25.** Fabric Loom 1.18, the first for 26.3, refuses an older JVM,
  and needs Gradle 9.7 or newer.
* **Minecraft 26.3 draws through Vulkan or OpenGL with an sRGB surface.** A virtual
  display (Xvfb) offers no sRGB OpenGL surface, so a 26.3 dev client there needs a Vulkan
  driver, such as Mesa's software one (lavapipe). NeoForge's early loading window is
  OpenGL only: turn it off with `earlyWindowControl = false` in the run's
  `config/fml.toml`.
* **The first build is slow.** Loom, ModDevGradle and ForgeGradle each fetch and prepare
  their own copy of Minecraft.
* **The Forge jar needs `pack.mcmeta`.** Forge refuses the resources of a mod jar
  without one, shows a warning at start-up, and crashes when a world is created; NeoForge
  and Fabric build the metadata themselves. Its `min_format` and `max_format` copy Forge's
  own `pack.mcmeta` and change with the Minecraft version: copy them from the new Forge
  build when the catalog moves.
* **One Minecraft version, named once.** Each platform module is one folder with one
  `build.gradle`; the catalog's `minecraft` entry is the only place the version is named.
  Moving to a new release replaces the catalog block and Forge's pack formats, and renames
  no folder or project.
* **`runClient` does not put core in the development game.** Copy core's jar for the same
  loader and Minecraft version into that loader module's `run/mods/`.
