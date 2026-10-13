---
name: repository-rules
description: The rules that apply to MCAgentsTemplate — client side only, core compiled against and never bundled, the one-way module boundaries, and what must not be introduced.
---

# MCAgentsTemplate Repository Rules

## Mode and shared set

This repository is a **Mode B consumer**. The shared instruction set is resolved through
the `lxagents-agents-base` MCP connector and is never copied here. Nothing under
`.agents/` may duplicate a file the connector serves; a local file that shadows a shared
`name` is an override and must be declared in
[`../index/root-index.md`](../index/root-index.md).

## Client side only

* The mod runs on the client. It must never require a server plugin, a server-side
  companion mod, or any server cooperation to work.
* Each loader manifest declares it: `"environment": "client"` in `fabric.mod.json`,
  `side = "CLIENT"` on every dependency in `neoforge.mods.toml` and `mods.toml`. The NeoForge
  entry point is `@Mod(dist = Dist.CLIENT)`; the Forge entry point starts the mod only when
  `FMLEnvironment.dist.isClient()`.
* Starting the mod never throws out of the loader's entry point. A failure is logged and the
  mod carries on without what it could not set up.

## MCAgents core

* **Core is compiled against, never bundled.** `mcagents-core-api` and
  `mcagents-core-common` are `compileOnlyApi`, and the `mcagents_core` mod provides them in
  game. A mod jar that carries a core class would load a second copy beside the one every
  other mod uses.
* Every loader manifest declares a required dependency on `mcagents_core` at the core
  version the build compiled against or newer.
* **What core already does is not written again here.** The model client, its transport,
  the request types, model naming and the holograms are core's; a change to them is made
  in core.
* **The interface is in game.** A mod shows what it needs on core's holograms, never in a
  browser: core has had no web console since 0.6.0.

## Module boundaries

Six Gradle modules, and the dependency direction is one-way. See
[`../../wiki/information/architecture.md`](../../wiki/information/architecture.md) for the
reasoning.

| Module | Directory | Gradle name | May depend on |
|---|---|---|---|
| API | `api/` | `mcagents-template-api` | The JDK and MCAgents core's API. |
| Common | `common/` | `mcagents-template-common` | `api/`, MCAgents core, and plain JVM libraries. |
| Shared platform | `platforms/shared/` | `mcagents-template-shared` | all of the above and vanilla Minecraft — no loader. |
| Fabric platform | `platforms/fabric/` | `mcagents-template-fabric` | all of the above, Fabric. |
| NeoForge platform | `platforms/neoforge/` | `mcagents-template-neoforge` | all of the above, NeoForge. |
| Forge platform | `platforms/forge/` | `mcagents-template-forge` | all of the above, Forge. |

* `api/` holds **contracts only** — interfaces, records, enums, sealed types, abstract
  classes and constants. No implementation logic, no I/O, no third-party dependency.
* `common/` holds the implementations. It must compile and be testable **without Minecraft
  on the classpath**. If a class in `common/` needs a Minecraft type, pass the value in
  through an `api/` contract instead.
* `platforms/shared/` holds everything Minecraft-specific that every loader shares —
  start-up, screens, keys, commands — compiled against vanilla Minecraft. No loader type may
  appear in it.
* A loader module holds only what that loader needs: the entry point, registrations the
  loader insists on making itself, its events, and the game directory it hands to
  `TemplateRuntime.start`.
* Nothing in `api/` or `common/` imports `net.minecraft.*` or a loader's packages. Nothing in
  `platforms/` re-implements logic that belongs below it.
* Every loader jar merges `api/`, `common/` and `platforms/shared/` into itself. A
  third-party runtime library is nested with the loader's own mechanism — Loom's `include`,
  NeoForge's `jarJar` — never merged.

## Names

* Every Gradle project is `mcagents-template-{module}`, which is also its artifact id under
  the group `io.github.mcagentsmods`. Every jar is `MCAgentsTemplate{Module}`.
* Packages live under `io.github.mcagentsmods.template.{api|common|shared|fabric|neoforge|forge}`.
* The mod id is `mcagents_template` in every manifest, in `MCAgentsTemplate.MOD_ID`, and in
  the Loom and ModDevGradle `mods {}` blocks.

## Build and test

```bash
./gradlew build                                   # every module, the tests, all three mod jars
./gradlew :mcagents-template-common:test          # the logic, without Minecraft
```

A `core` checkout beside this repository is built together with it; see
[`../../wiki/environments/setup.md`](../../wiki/environments/setup.md).

## Must not be introduced

* A dependency from `api/` or `common/` on Minecraft or a loader.
* A core class inside any jar this repository builds.
* A Minecraft, loader or core version that differs from core's own catalog.
* Player data in the repository. Everything the mod writes goes under
  `{game_dir}/MCAgents/template/` on the player's machine.
