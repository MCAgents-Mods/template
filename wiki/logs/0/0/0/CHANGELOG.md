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
- Core from GitHub Packages: with no core checkout beside the mod, the build reads
  `mcagents-core-api` and `mcagents-core-common` from
  `https://maven.pkg.github.com/MCAgents-Mods/core`, using `GITHUB_ACTOR` and
  `GITHUB_TOKEN`. That is how the Publish workflow builds a mod.

What a reader has to do: nothing in this repository. In a copy, follow `PROMPT.md` once.
