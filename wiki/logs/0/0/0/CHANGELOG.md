---
name: changelog-0-0-0
description: Change log for MCAgentsTemplate 0.0.0 — the first and only version of the MCAgents mod starter.
---

# 0.0.0 — 2026-10-12

The MCAgents mod starter: an empty client-side mod for Fabric, NeoForge and Forge that runs
on MCAgents core, and the `PROMPT.md` that turns a copy of it into a named mod.

## Added

- `.agents/rules/template.md` — while this repository is the starter, the version stays
  `0.0.0` and no `.agents/memory/` is created. `PROMPT.md` deletes the rule in every copy.
- The Gradle build: the 9.7.0 wrapper, a version catalog matching core's Minecraft 26.2,
  Fabric, NeoForge and Forge versions, a composite build with a `core` checkout beside the
  repository, and `project-version=0.0.0`.
- Six modules: `api/` (`MCAgentsTemplate`, the mod's identity), `common/` (`TemplatePaths`
  and its test), `platforms/shared/` (`TemplateRuntime`, the start-up every loader calls),
  and one entry point and manifest each in `platforms/fabric/`, `platforms/neoforge/` and
  `platforms/forge/`, every manifest requiring `mcagents_core`.
- The agent instruction set: `AGENTS.md`, which sends agents to `PROMPT.md` first,
  `.claude/CLAUDE.md`, the repository rules and map, and the `wiki/` architecture and setup
  pages.
- `PROMPT.md` — every placeholder name, what replaces it, the commands, the check that
  nothing was missed, and the clean-up.
- `.github/workflows/publish.yml` — on a push to `master` whose `project-version` has no
  `v{version}` tag, builds and tests, publishes the six modules to GitHub Packages, then
  tags the commit. At `0.0.0` it stops at the first step, so this repository, and a copy
  before its first release, publish nothing.
- `.github/workflows/release.yml` — after each Publish run on `master`, when the
  `v{version}` tag has no GitHub release yet, builds the Forge, Fabric and NeoForge jars from
  the tag and attaches them to a new release whose notes are that version's changelog, then
  deletes the run's copy of the jars. At `0.0.0` it releases nothing.
- Core from GitHub Packages: with no core checkout beside the mod, the build reads
  `mcagents-core-api` and `mcagents-core-common` from
  `https://maven.pkg.github.com/MCAgents-Mods/core`, using `GITHUB_ACTOR` and
  `GITHUB_TOKEN`. That is how the Publish workflow builds a mod.

## Changed

- The architecture page, the repository rule and the `settings.gradle` and `api/build.gradle`
  comments describe core's holograms instead of its web console, which core 0.6.0 removed. A
  mod shows its interface in game, on a `Hologram` opened with `Holograms` from
  `mcagents-core-client`. The template still compiles against core 0.5.0; the architecture
  page says what to raise before using a hologram.

What a reader has to do: in a copy, register no web pages, and raise `mcagents-core` to 0.6.0
before the first hologram.

- Every Minecraft-facing module, `platforms/shared/` and the three loaders, is built once
  for each Minecraft version, 26.2 and 26.3, as a subproject `platforms/{module}/{minecraft}`
  named `mcagents-template-{module}-{minecraft}`. Each version has its own catalog entries,
  suffixed with the version, and its own jar, `MCAgentsTemplate{Loader}-{version}+{minecraft}.jar`,
  whose manifest declares that Minecraft version and no other. The Release workflow
  attaches all six jars. The catalog pins core 0.7.0, the first core built for both, and the
  build needs Java 25 and the Gradle 9.7.1 wrapper, which Fabric Loom 1.18 requires for 26.3.

What a reader has to do: run Gradle on Java 25. In a copy, name a loader's project with its
Minecraft version, such as `:mcagents-template-fabric-26.2:runClient`, and put code that
differs between versions in a class of the same name in each version folder.

- The template supports **only the latest Minecraft release, 26.3**. The 26.2 subprojects,
  catalog block and Forge `pack.mcmeta` are gone, and no older version will be kept beside
  the latest again. The version subprojects and their artifact ids lose the Minecraft
  version, `mcagents-template-{module}`, while the jars keep it,
  `MCAgentsTemplate{Loader}-{version}+26.3.jar`. The Release workflow attaches three jars.
  The catalog pins core 0.8.0, whose ids drop the version the same way.

What a reader has to do: in a copy, run a loader as `:mcagents-template-fabric:runClient`,
with no version, and move to the next Minecraft release by replacing the version in
`settings.gradle`, the catalog block and each module's version folder.

- The Gradle projects and artifact ids drop the repeated `mcagents-`: `template-api`,
  `template-common`, `template-shared` and the loaders' `template-{loader}`, which `PROMPT.md`
  renames to `{repo}-…` in a copy. Only the library modules are published, `api`, `common` and
  `shared`; the Fabric, NeoForge and Forge jars are attached to the GitHub release only. The
  catalog pins core 0.9.0, the first whose artifact ids are `core-api` and `core-common`.

What a reader has to do: in a copy, name a project without `mcagents-`, such as
`:{repo}-fabric:runClient`, and ask core for `core-api` and `core-common`.

- The catalog pins core 0.10.0, which carries the tool registry, the agent and the agents
  menu that were the separate MCAgents agents mod. The architecture page says where a mod
  registers a tool, asks the agent and adds a button to the menu G shows.

What a reader has to do: in a copy, take tools, the agent and menu buttons from core's
`io.github.mcagentsmods.core.tool`, `core.api.agent`, `core.api.button` and
`core.common.agent` packages, and depend on `mcagents_core` alone.

- The catalog pins core 0.11.0, where several holograms stand at once, one per id, and the
  `PickList` panel lists rows to pick from under a nav bar. The architecture page says so.

What a reader has to do: in a copy that opens holograms, give each hologram that may stand
beside another of its kind its own id, and read `Holograms.openIds()` where it read
`openId()`.

- The catalog pins core 0.12.0, whose `Form` panel makes a page of fields a hologram: text,
  typed boxes (one drawn as stars for a key, one with suggestions behind an arrow), a
  multi-line area, switches, steppers and buttons. `Hologram.onClose` runs when a hologram
  closes. The architecture page says so.

What a reader has to do: in a copy with a settings page, build it as a `Form` hologram rather
than a screen, and add a `Form` case to any `switch` over `HologramPanel`.

- The catalog pins core 0.13.0, where a model is a `ModelChoice` (the provider it is called
  through and the model) and core keeps the player's global model, which the Models page edits.
  A task with no model of its own resolves it with `Models.chat(own)` or `Models.embedding(own)`.
  The architecture page says so.

What a reader has to do: nothing, unless a copy picks models; then store a `ModelChoice`, null
for "use the global model", and resolve it with `Models.chat(own)`.

- No Gradle file declares `implementation`. The loader modules compile against this mod's own
  modules and their loader (Fabric Loader and Fabric API, or Forge) with `compileOnly`, each
  with a `runtimeOnly` twin so the development client still finds them. What a jar carries
  comes only from its `jar` task's merge, never from a configuration. The jars are unchanged.

What a reader has to do: in a copy, add a third-party runtime library as `compileOnly` where it
is compiled against, nest it with `include` (Fabric) or `jarJar` (NeoForge, Forge), and give
the loader module a `runtimeOnly` line for it.

## Fixed

- The Forge jar carries `pack.mcmeta`, described as `MCAgentsTemplate resources`, which
  `PROMPT.md` renames with the rest. Without it Forge 26.2 refused the jar's resources,
  warned at start-up and crashed when a world was created. NeoForge and Fabric build that
  metadata themselves, so their jars still have none.

What a reader has to do: nothing in this repository. In a copy, follow `PROMPT.md` once. A
mod copied before this fix adds `platforms/forge/src/main/resources/pack.mcmeta` with its
own name in the description.
