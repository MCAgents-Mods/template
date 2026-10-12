---
name: template-repository
description: Rules that hold only while this repository is the MCAgents mod template — the version stays 0.0.0 and no .agents/memory/ is ever created. Deleted by PROMPT.md when a copy becomes a mod.
---

# Template Repository

This repository is the starter every new MCAgents mod is copied from. What it holds is
copied into every one of them, so it carries no version history and no memory of its own.
The owner set both rules on 2026-10-12.

## The version stays `0.0.0`

* `project-version` in `gradle.properties` is `0.0.0`, and it is never changed in this
  repository — not for a release, not for a breaking change, not on request.
* The only version directory under `wiki/logs/` is `0/0/0/`. A change worth recording is
  appended to that changelog; no other version directory is created.
* If a task asks for a version bump here, decline it, name this rule, and say that the
  version moves only in a mod made from the template.

## No `.agents/memory/`

* Never create `.agents/memory/`, any file under it, or `.agents/index/memory-index.md` in
  this repository.
* The shared workflow's task record, per-task entries and closing entry are skipped here.
  Keep the plan and its progress outside the repository — in the pull request bodies, or in
  the workspace the request came from — and say in the plan that the record slot is skipped
  because of this rule.
* A decision a later session needs belongs in a pull request body or a wiki page, never in
  memory.

## Lifetime

This file exists only in the template. `PROMPT.md` deletes it, its row in
[`../index/agents-index.md`](../index/agents-index.md) and its trigger row in `AGENTS.md`
while turning a copy into a mod. From then on the mod versions itself and keeps memory like
any other repository.
