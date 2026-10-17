---
name: wiki-information-architecture
description: How MCAgentsTemplate is split into modules, why each boundary exists, and how the mod reaches MCAgents core.
---

# Architecture

An MCAgents mod is one codebase that ships a Fabric, a NeoForge and a Forge jar for each
Minecraft version it supports, and runs beside the MCAgents core mod, which supplies the language model client and the
holograms every MCAgents mod shows its interface on, in game.

## Modules

```
            MCAgents core (mcagents_core mod, provided in game)
                 ▲                 ▲
                 │ compileOnlyApi  │
              api/  ◄────────  common/
                                  ▲
                          platforms/shared/
                     ▲            ▲            ▲
          platforms/fabric  platforms/neoforge  platforms/forge
```

| Module | Holds | Knows about |
|---|---|---|
| `api/` | Contracts and constants | The JDK and core's API |
| `common/` | The mod's logic | `api/`, core, plain JVM libraries |
| `platforms/shared/` | Start-up, screens, keys, commands | The above and vanilla Minecraft |
| `platforms/{fabric,neoforge,forge}/` | One entry point and that loader's events | The above and one loader |

**Why `common/` has no Minecraft.** Logic that needs no game can be tested in milliseconds
with plain JUnit. The moment a class in `common/` needs a Minecraft type, the value it needs
is passed down through an `api/` contract instead.

**Why `platforms/shared/` has no loader.** Minecraft 26.x ships unobfuscated, so code
compiled against the vanilla game links against every loader's game. Screens and start-up
are written once; each loader module adds only what that loader insists on doing itself.

**Why core is never bundled.** The `mcagents_core` mod provides core's classes in game. A
mod that also carried them would load a second copy, and two copies of core's open hologram
or token store do not see each other.

## Start-up

Each loader's entry point finds the game directory and calls
`TemplateRuntime.start(gameDirectory)` in `platforms/shared/`. Start-up never throws: a
failure is logged and the mod carries on without what it could not set up. The mod keeps its
files under `{game_dir}/MCAgents/template/`.

## Reaching core

| To | Use |
|---|---|
| Call a language model | Core's model client, from `io.github.mcagentsmods.core.api.model` |
| Give the agent a tool | Register a `ToolSpec` and its body in `ToolRegistry.shared()`, from `io.github.mcagentsmods.core.tool`; core's agent offers it when a prompt needs its category. See core's [`wiki/api/tool-registry.md`](https://github.com/MCAgents-Mods/core/blob/master/wiki/api/tool-registry.md) |
| Answer a prompt with tools | `Agents.agent(client)`, from `io.github.mcagentsmods.core.common.agent`. See core's [`wiki/api/agent.md`](https://github.com/MCAgents-Mods/core/blob/master/wiki/api/agent.md) |
| Put a button on the menu G shows | Register an `AgentButton` with `Agents.buttons()`. See core's [`wiki/api/buttons.md`](https://github.com/MCAgents-Mods/core/blob/master/wiki/api/buttons.md) |
| Show an interface in game | Build a `Hologram` from core's panels and open it with core's `Holograms`; core places it, draws it, takes the clicks and the typing, and closes it when the player walks away. From core 0.11.0 several stand at once, one per id, and a `PickList` panel lists rows to pick from, with buttons on its nav bar and rows that can be faded. See core's [`wiki/api/hologram.md`](https://github.com/MCAgents-Mods/core/blob/master/wiki/api/hologram.md) |

There is no browser interface. Core 0.6.0 removed the web console every MCAgents mod used to
register pages with, so an MCAgents mod is used in game only. Up to core 0.9.0 the tool
registry, the agent and the menu were a separate mod, MCAgents agents (`mcagents_agents`);
from core 0.10.0 they are part of core, and a mod needs core alone.

`Holograms` is in `core-client`, which core publishes for the latest Minecraft release, and
the template compiles against core 0.11.0's `core-api` and `core-common` only, which bring
`core-tool`. Core's artifact
ids are `core-{module}` from 0.9.0; before that they started with `mcagents-`. Before a copy
opens its first hologram it substitutes `io.github.mcagentsmods:core-client` with core's
`:platforms:client` in `settings.gradle`, and adds
`compileOnly "io.github.mcagentsmods:core-client:{coreVersion}"`
to `platforms/shared/shared.gradle`. Like the rest of
core, it is never bundled.

## The jars

| Loader | Toolchain | Manifest | Jar |
|---|---|---|---|
| Fabric | Fabric Loom | `fabric.mod.json` | `MCAgentsTemplateFabric-{version}+{minecraft}.jar` |
| NeoForge | ModDevGradle | `META-INF/neoforge.mods.toml` | `MCAgentsTemplateNeoforge-{version}+{minecraft}.jar` |
| Forge | ForgeGradle | `META-INF/mods.toml` | `MCAgentsTemplateForge-{version}+{minecraft}.jar` |

Each jar merges `api/`, `common/` and that Minecraft version's `platforms/shared/` into
itself, declares that Minecraft version and no other, and requires `mcagents_core` at the
core version it was built against or newer.

The Forge jar also carries `pack.mcmeta`. Forge refuses the resources of a mod jar without
one and crashes when a world is created; NeoForge and Fabric build that metadata themselves,
so their jars have none.
