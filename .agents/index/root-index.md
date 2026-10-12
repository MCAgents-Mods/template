---
name: root-index
description: Router for MCAgentsTemplate — lists every local index plus the shared router, and declares this repository's overrides.
---

# Root Index

## Indexes

| Index | Scope | Load when |
|---|---|---|
| `{shared}/index/root-index.md` | The shared instruction set | You need a branching, commit, pull request, planning, or creator convention. |
| [`agents-index.md`](agents-index.md) | This repository's instruction set | You need a rule specific to this repository. |
| [`agent-wiki-index.md`](agent-wiki-index.md) | `.agents/wiki/` agent knowledge | You need orientation, commands, or operating context written for agents. |
| [`project-wiki-index.md`](project-wiki-index.md) | `wiki/` human documentation | You need to read or write documentation a person will read. |

## Shared Overrides

| `name` | Local file | Replaces | Why |
|---|---|---|---|

No overrides — this repository uses the shared set unchanged.

## Maintenance

This file lists indexes only, never rules, never leaf content. Adding, removing, or
renaming any index updates the table above **in the same commit**; adding or dropping an
override updates the override table in the same commit. Read exactly one branch per task.
