---
name: logs-index
description: Version list for MCAgentsTemplate, newest first — each row points at that version's change log.
---

# Logs Index

**Scope:** `wiki/logs/`
**Parent:** [`project-wiki-index.md`](project-wiki-index.md)

## Versions

| Version | File | Purpose |
|---|---|---|
| 0.0.0 | [`../../wiki/logs/0/0/0/CHANGELOG.md`](../../wiki/logs/0/0/0/CHANGELOG.md) | The starter: the build, an empty mod for Fabric, NeoForge and Forge on core, the agent instructions, `PROMPT.md`, the Publish and Release workflows, `pack.mcmeta` in the Forge jar, a jar per loader for Minecraft 26.3, the latest release and the only one supported, artifact ids `template-{module}` with only the library modules published, core 0.10.0, which carries tool calling, core 0.11.0, where several holograms stand at once, loader modules that `implementation` the mod's own modules and keep everything else `compileOnly`, core 0.12.0, whose `Form` panel makes a page of fields a hologram, and core 0.13.0, with each model's provider and the global model a task falls back to. |

## Maintenance

A new version directory is added to the top of this table **in the same commit** that
creates it. The project version is never bumped without explicit user approval.
