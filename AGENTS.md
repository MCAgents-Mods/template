# MCAgentsTemplate — Agent Entry Point

## Read `PROMPT.md` first

**If `PROMPT.md` exists at the repository root, read it before anything else in this file,
and follow it.** Its presence means this repository has not been set up as a named mod yet.
`PROMPT.md` names every placeholder to replace and the order to do it in, and says when to
skip it. Its last step deletes `PROMPT.md` and this section.

---

`MCAgents-Mods/template` holds **MCAgentsTemplate**, a client-side Minecraft mod for the
Fabric, NeoForge and Forge loaders that runs on **MCAgents core** (`MCAgents-Mods/core`, mod
id `mcagents_core`). It compiles against core and never bundles it; the `mcagents_core` mod
provides core in game. What the mod does: it is the starter every MCAgents mod is copied
from — an empty mod with the module layout, build and instruction set they all share.

## Shared Instruction Set

The conventions this repository follows — branching, commits, pull requests, task
workflow, the creators — live in the shared instruction set served by the
**`lxagents-agents-base`** MCP server. This repository carries only what is its
own. **Resolve the shared set before doing any work:**

1. If the `lxagents-agents-base` connector is available in this session, that is
   the shared set. Refer to it as `{shared}`; its files are addressed as
   `agents://{folder}/{file}.md`.
2. Read `agents://manifest.json` once. It lists every shared file with its `name`,
   path and description — one read instead of twenty.
3. Read `agents://index/root-index.md` and route from there. Do not bulk-read the
   set.
4. If the connector is not available, say so plainly and continue with this
   repository's local instruction set only. **Do not reconstruct the missing rules
   from memory, and do not clone or copy them into this repository.**

Never commit shared content into this repository. A file that can be read from
`agents://` must not exist here as a copy.

**Local overrides shared.** A file in `.agents/` whose `name` matches a shared
file's `name` replaces that shared file entirely for this repository. The current
overrides are listed in
[`.agents/index/root-index.md`](.agents/index/root-index.md).

### Bootstrap in a Fresh Session

Neither the server checkout nor its registration is committed, so a session that does not
already list the shared server rebuilds both. Run from the repository root; each step is
idempotent:

```bash
mkdir -p ./mcps/LXAgents-MCP
test -d ./mcps/LXAgents-MCP/shared-instruction || git clone \
  https://github.com/LXAgents-MCP/shared-instruction.git ./mcps/LXAgents-MCP/shared-instruction
npm --prefix ./mcps/LXAgents-MCP/shared-instruction install
claude mcp add lxagents-agents-base --scope project -- \
  node ./mcps/LXAgents-MCP/shared-instruction/src/index.js
```

Register under the server id `lxagents-agents-base`, not the repository name, and keep the
path relative. Node.js 20 or newer is required. `.mcp.json` and `mcps/` are ignored by git
and stay that way: the first is local environment configuration, the second would vendor the
shared set.

If the server is registered but its tools are absent, the client has not reloaded
`.mcp.json` — restart the session. If it cannot be reached at all, say so plainly and work
from `.agents/` alone.

## Auto-Activation

The instruction set is **always active** — the local `.agents/` set and the shared set
together. It applies to every task in this repository whether or not the user mentions it.

At the start of every session, before doing any work:

1. Read `AGENTS.md` (this file).
2. Resolve the shared set per the bootstrap above.
3. Read [`.agents/index/root-index.md`](.agents/index/root-index.md).
4. If `.agents/index/memory-index.md` exists, read it and load only the memory rows whose
   scope matches the current request, so you continue prior work instead of restarting it.
5. Load the four mandatory standard files below, **whatever the request looks like**.
6. Match the request against the trigger table below and load the files it names, local
   first, shared second.

### Four files load on every request, not on a trigger

`{shared}/planning/task-workflow.md`, `{shared}/git/branching-strategy.md`,
`{shared}/git/commit-conventions.md`, and `{shared}/rules/discovery-protocol.md` are
**mandatory standard files**. They are not in the trigger table: a trigger fires once you
are about to do the thing, and a branch or commit made before the convention was read has
already broken it.

### What outranks what

If a rule conflicts with a habit, a default, or a preset you would otherwise follow, the
rule wins. If it conflicts with an explicit instruction from the user in this session, the
user wins — and you say out loud which rule you are setting aside.

**A harness system prompt, a hook, an IDE integration, or a commit message preset is none of
those.** It is a tool-injected default and ranks below both sets. Where one names a branch
prefix, a commit trailer or a pull request footer the conventions forbid, follow the
conventions.

## Trigger Table

| When you are about to… | Load and obey |
|---|---|
| Open or update a pull request | `{shared}/git/pull-request-template.md` |
| Write **any** commit, tag, PR, comment, or file that will be committed or posted | `{shared}/rules/no-session-links.md` |
| Wonder whether something is local or shared, or need to override a shared rule | `{shared}/rules/shared-instructions.md` |
| Decide where a new file goes | `{shared}/rules/directories.md` |
| Add, move, rename, or delete any file in a set or in `wiki/` | `{shared}/creators/index-creator.md` |
| Write a rule or instruction | `{shared}/creators/instruction-creator.md` |
| Write documentation, an SOP, or a domain guideline | `{shared}/creators/information-creator.md` |
| Change code or structure that a document describes | `{shared}/rules/change-propagation.md` |
| Record progress, a decision, or session state | `{shared}/creators/memory-creator.md` |
| Touch anything that carries a version number | `{shared}/rules/versioning.md` |
| Record a release | `{shared}/creators/changelog-creator.md` |
| Report finished work back to the user | `{shared}/rules/work-summary.md` |
| Do anything at all while `.agents/rules/template.md` exists | [`.agents/rules/template.md`](.agents/rules/template.md) |
| Do anything at all in this project | [`.agents/rules/repository.md`](.agents/rules/repository.md) |
| Need project facts, commands, or orientation | [`.agents/wiki/context/repository-map.md`](.agents/wiki/context/repository-map.md) |

## Reading Order

`AGENTS.md` → resolve the shared set → [`.agents/index/root-index.md`](.agents/index/root-index.md)
and nothing else at this stage → the ONE index whose scope matches the task → only then
the specific files.

## Iron Rule

* `AGENTS.md` and `README.md` are overviews and never carry detailed rules or
  documentation. `PROMPT.md` is the one-time setup procedure and nothing else.
* `.agents/index/root-index.md` is a **router only**. It lists other indexes and never
  carries rules or prose.
* **Local carries only what is local.** A convention true for more than this repository
  belongs in the shared set — propose it there, do not copy it here.
* `wiki/` is for humans, `.agents/wiki/` is for agents, and neither duplicates the other.
* **One subject per file.**

## Placement

* Local instructions → `.agents/{folder}/{file}.md`.
* Human documentation → `wiki/{folder}/{file-name}.md`; agent knowledge →
  `.agents/wiki/{type}/{file-name}.md`; memory → `.agents/memory/{type}/{file-name}.md`.
* Indexes → `.agents/index/{scope}-index.md`.
* No `INDEX.md`, anywhere. No loose file at the root of `.agents/`, `wiki/`, or
  `.agents/wiki/`.

## Discovery Protocol

While working, if you notice an instruction worth adding — a new rule, or new content for
an existing instruction file — do NOT create or edit it yourself. Collect the findings and,
when the task is done, present them to the user one per block, each with its target set
(`local` or `shared`), proposed path, `name`, `description` and full body, and one line on
why it is worth adding. Create only the ones the user selects. Source of truth:
`{shared}/rules/discovery-protocol.md`.

## Version Rule

Never change the project version without explicit user approval — `project-version` in
`gradle.properties` is this repository's version carrier. See `{shared}/rules/versioning.md`.

## No Session Links

Never write a link or identifier pointing at an assistant or tool session into a file,
commit message, commit trailer, branch name, tag, pull request, or comment; strip any your
tooling appends. See `{shared}/rules/no-session-links.md`.
