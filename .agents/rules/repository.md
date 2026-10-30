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

* **Core is compiled against, never bundled.** `core-api` and `core-common` are
  `compileOnlyApi`, and the `mcagents_core` mod provides them in game. A mod jar that
  carries a core class would load a second copy beside the one every other mod uses.
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
| API | `api/` | `template-api` | The JDK and MCAgents core's API. |
| Common | `common/` | `template-common` | `api/`, MCAgents core, and plain JVM libraries. |
| Shared platform | `platforms/shared/` | `template-shared` | all of the above and vanilla Minecraft — no loader. |
| Fabric platform | `platforms/fabric/` | `template-fabric` | all of the above, Fabric. |
| NeoForge platform | `platforms/neoforge/` | `template-neoforge` | all of the above, NeoForge. |
| Forge platform | `platforms/forge/` | `template-forge` | all of the above, Forge. |

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

* Every Gradle project is `template-{module}`, which is also its artifact id, under the group
  `io.github.mcagentsmods`, when it is published. Every jar is `MCAgentsTemplate{Module}`.
* Packages live under `io.github.mcagentsmods.template.{api|common|shared|fabric|neoforge|forge}`.
* The mod id is `mcagents_template` in every manifest, in `MCAgentsTemplate.MOD_ID`, and in
  the Loom and ModDevGradle `mods {}` blocks.

## Build and test

```bash
./gradlew build                                   # every module, the tests, all three mod jars
./gradlew :template-common:test                   # the logic, without Minecraft
```

A `core` checkout beside this repository is built together with it; see
[`../../wiki/environments/setup.md`](../../wiki/environments/setup.md).

## Files on the player's disk

The owner's rule for every MCAgents mod (2026-10-22), set so that no mod has to be asked for
it again:

* **Every file the mod keeps — configuration and data alike — lives in
  `{game_dir}/MCAgents/template/`**, the directory named for this repository: core keeps its
  files in `MCAgents/core/`, chat in `MCAgents/chat/`. Nothing goes loose in the game directory
  or at the root of `MCAgents/`, which every MCAgents mod shares.
* `MCAgentsTemplate.MOD_DIRECTORY` names the directory, and `TemplatePaths` is the one place
  that resolves it: ask it for `dataDirectory()` or `file(name)`, never build the path by hand.
* A file that moves keeps working for players who update: move it from its old place on start,
  before anything opens it, with core's `ModFiles.moveIfAbsent(legacy, target)`, which never
  overwrites a file already in the new place.

## Commands

The owner's rule for every MCAgents mod (2026-10-30): a command a player should not be able to
use is never reachable by one.

* **A mod registers a command only behind the permission level its effect needs.** A command
  that only touches the player's own client — their settings, their keys, their files — needs
  none. A command that runs on a server and changes or reveals anything beyond the caller's own
  view — keys, money, files, other players — gets Brigadier's `requires` at the level that
  effect needs: `Commands.hasPermission(Commands.LEVEL_OWNERS)` for anything that spends the
  server owner's money or touches their keys.
* **The check is also made where the work is done**, not only on the command tree, so a second
  way in (a console bridge, a hologram button, another mod) is refused the same way. Core's
  `/agents` is the pattern: `requires` on the tree, and `ServerEntrypoint`'s
  `ServerCommandAuthority` on every call.
* **A reply never contains a secret** and goes to the caller alone (`sendSuccess(…, false)`),
  never broadcast to other operators.
* Prefer a hologram to a command for anything the player does in game: core's holograms need
  no permission because they only ever reach the player's own client.

## Must not be introduced

* A dependency from `api/` or `common/` on Minecraft or a loader.
* A core class inside any jar this repository builds.
* A Minecraft, loader or core version that differs from core's own catalog.
* A server command with no permission level, or one that checks only the command tree.
* Player data in the repository. Everything the mod writes goes under
  `{game_dir}/MCAgents/template/` on the player's machine.
