---
name: wiki-information-architecture
description: How MCAgentsTemplate is split into modules, why each boundary exists, and how the mod reaches MCAgents core.
---

# Architecture

An MCAgents mod is one codebase that ships three jars — Fabric, NeoForge and Forge — and
runs beside the MCAgents core mod, which supplies the language model client and the local
web console every MCAgents mod shares.

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
mod that also carried them would load a second copy, and two copies of core's web registry
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
| Serve pages in the browser | Register a `WebModule` with core's web registry; core owns the server, the token and the page shell |

## The jars

| Loader | Toolchain | Manifest | Jar |
|---|---|---|---|
| Fabric | Fabric Loom | `fabric.mod.json` | `MCAgentsTemplateFabric-{version}.jar` |
| NeoForge | ModDevGradle | `META-INF/neoforge.mods.toml` | `MCAgentsTemplateNeoforge-{version}.jar` |
| Forge | ForgeGradle | `META-INF/mods.toml` | `MCAgentsTemplateForge-{version}.jar` |

Each jar merges `api/`, `common/` and `platforms/shared/` into itself and requires
`mcagents_core` at the core version it was built against or newer.
