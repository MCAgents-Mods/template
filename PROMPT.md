# PROMPT.md — Turn This Template Into a New Mod

You are in a copy of `MCAgents-Mods/template`, the starter for a new MCAgents mod. This file
is the one-time setup: it names everything that still says `template` and how to replace it.
Its last step deletes this file. After that, `AGENTS.md` is the only entry point.

## 0. Is this the template itself?

Run `git remote get-url origin`. If it ends in `MCAgents-Mods/template` or
`MCAgents-Mods/template.git`, **you are in the template repository itself: stop reading this
file, do not rename anything, and continue with `AGENTS.md`.** The template keeps its
placeholder names; `.agents/rules/template.md` applies to it.

Otherwise, carry on.

## 1. Settle the names

Everything below is derived from the new repository's name. Read it from the origin remote
(`MCAgents-Mods/{repo}`). Ask the owner for two things the name cannot give you: the mod's
display name if it should not be `MCAgents{Pascal}`, and one sentence saying what the mod
does.

| Form | Rule | In the template | Example: repository `foo-bar` |
|---|---|---|---|
| `{repo}` | The repository name, exactly | `template` | `foo-bar` |
| `{snake}` | `{repo}` with `-` turned into `_` | `template` | `foo_bar` |
| `{package}` | `{repo}` with every `-` removed | `template` | `foobar` |
| `{Pascal}` | Each word of `{repo}` capitalized and joined | `Template` | `FooBar` |
| Description | One sentence from the owner | `A starter MCAgents mod. Replace this sentence with what the mod does.` | the owner's sentence |

`{repo}` must be lowercase letters, digits and `-`, and start with a letter. `{snake}` must
fit a mod id: `mcagents_{snake}` is at most 64 characters. If either fails, stop and ask.

## 2. What changes

Replace in this order — the longer, more specific forms first, so a short replacement never
breaks a longer one.

| # | Find | Replace with | Where it appears |
|---|---|---|---|
| 1 | `MCAgents-Mods/template` | `MCAgents-Mods/{repo}` | Build files, manifests, docs — every GitHub URL |
| 2 | `mcagents-template-` | `mcagents-{repo}-` | `settings.gradle`, every module's `build.gradle`, docs — the Gradle project names and artifact ids |
| 3 | `mcagents_template` | `mcagents_{snake}` | The mod id: `MCAgentsTemplate.java`, all three manifests, the `mods {}` blocks in `platforms/fabric` and `platforms/neoforge`, docs |
| 4 | `io.github.mcagentsmods.template` | `io.github.mcagentsmods.{package}` | Every `package` and `import` line, the Fabric entry point in `fabric.mod.json`, docs |
| 5 | `io/github/mcagentsmods/template` | `io/github/mcagentsmods/{package}` | The source directories (move them, step 3) and any path in docs |
| 6 | `MCAgentsTemplate` | `MCAgents{Pascal}` | Jar names (`archivesName`), POM names, the constants class, display names in the manifests, the description in the Forge `pack.mcmeta`, docs |
| 7 | `Template` at the start of a class name | `{Pascal}` | `TemplatePaths`, `TemplatePathsTest`, `TemplateRuntime`, `TemplateFabricClient`, `TemplateNeoForgeClient`, `TemplateForgeMod` — names and file names |
| 8 | `template` as the repository name | `{repo}` | `git-repo=template` in `gradle.properties`, `rootProject.name = 'template'` in `settings.gradle`, `MOD_DIRECTORY = "template"` in `MCAgentsTemplate.java`, `resolve("template")` in `TemplatePathsTest.java`, `MCAgents/template` in docs |
| 9 | The placeholder description | The owner's sentence | `fabric.mod.json`, `neoforge.mods.toml`, `mods.toml` |

**What does not change:**

* The Maven group `io.github.mcagentsmods` and the organization `MCAgents-Mods` (`git-org`).
* The version: `project-version=0.0.0` stays `0.0.0`. The new mod starts there.
* `gradle/libs.versions.toml` — Minecraft, the loaders, the plugins and the core version.
* The `mcagents_core` dependency in every manifest.
* `LICENSE`, the wrapper, `.gitignore`, `.gitattributes`, `.claude/CLAUDE.md`.
* `.github/workflows/publish.yml`. It names no placeholder, and it publishes nothing while the
  version is `0.0.0`, so the setup below ships nothing. The mod's first release, the first
  merge that raises `project-version`, is the first version it publishes.
* `.github/workflows/release.yml`. It names no placeholder either, and it releases nothing
  while the version is `0.0.0`. After each Publish run it attaches the mod's three jars to
  the new tag's GitHub release.
* `{shared}/git/pull-request-template.md` in `AGENTS.md`: it names a shared file, not this mod.

## 3. Do it

This is more than one step, so the shared task workflow applies: present the plan below to
the owner and wait for a yes before writing anything. A split that works:

| # | Branch | Does |
|---|---|---|
| 1 | `chore/setup-plan` | Deletes `.agents/rules/template.md`, its row in `.agents/index/agents-index.md` and its trigger row in `AGENTS.md` — the rule that forbids memory goes first — then writes the task record `.agents/memory/tasks/setup.md` and `.agents/index/memory-index.md`, and lists the memory index in `.agents/index/root-index.md`. |
| 2 | `refactor/mod-names` | Steps 2 and 3 below: every replacement, the moved directories, a green build. |
| 3 | `docs/mod-description` | Rewrites `README.md` and the opening paragraph of `AGENTS.md` (the sentence starting "What the mod does") for this mod. |
| 4 | `release/0.0.0` | Deletes the template's `wiki/logs/0/0/0/CHANGELOG.md` and writes the mod's own; deletes this file and the "Read `PROMPT.md` first" section of `AGENTS.md`; closes the record. |

The replacements, from the repository root, with `perl` because it behaves the same on
Linux and macOS. Set the four names first:

```bash
REPO=foo-bar; SNAKE=foo_bar; PACKAGE=foobar; PASCAL=FooBar

files=$(git ls-files | grep -v -e '^gradle/wrapper/' -e '^gradlew' -e '^PROMPT.md$')
perl -pi -e "
  s{MCAgents-Mods/template}{MCAgents-Mods/$REPO}g;
  s{mcagents-template-}{mcagents-$REPO-}g;
  s{mcagents_template}{mcagents_$SNAKE}g;
  s{io\.github\.mcagentsmods\.template}{io.github.mcagentsmods.$PACKAGE}g;
  s{io/github/mcagentsmods/template}{io/github/mcagentsmods/$PACKAGE}g;
  s{MCAgentsTemplate}{MCAgents$PASCAL}g;
  s{\bTemplate(?=[A-Z])}{$PASCAL}g;
  s{git-repo=template}{git-repo=$REPO}g;
  s{rootProject\.name = 'template'}{rootProject.name = '$REPO'}g;
  s{MOD_DIRECTORY = \"template\"}{MOD_DIRECTORY = \"$REPO\"}g;
  s{resolve\(\"template\"\)}{resolve(\"$REPO\")}g;
  s{MCAgents/template}{MCAgents/$REPO}g;
" $files

# Move every package directory, then rename the files that carry the class prefix.
for dir in $(git ls-files | grep '/io/github/mcagentsmods/template/' | sed 's#/io/github/mcagentsmods/template/.*#/io/github/mcagentsmods#' | sort -u); do
  git mv "$dir/template" "$dir/$PACKAGE"
done
for f in $(git ls-files | grep -E '/(MCAgentsTemplate|Template[A-Z][A-Za-z]*)\.java$'); do
  git mv "$f" "$(dirname "$f")/$(basename "$f" | sed -e "s/^MCAgentsTemplate/MCAgents$PASCAL/" -e "s/^Template/$PASCAL/")"
done
```

Then replace the description (row 9) by hand in the three manifests. If the owner chose a
display name other than `MCAgents{Pascal}`, set it by hand too: `MOD_NAME` in
`MCAgents{Pascal}.java`, `name` in `fabric.mod.json` and `displayName` in both `.toml`
manifests. The jar, class and project names keep the `MCAgents{Pascal}` form.

## 4. Prove nothing was missed

At the end of the rename branch, both must hold:

```bash
# 1. No placeholder is left outside the four files the later tasks rewrite or delete.
#    gradlew is Gradle's own script, which says it was generated from a template.
git grep -n -i template -- . ':!gradlew' ':!PROMPT.md' ':!README.md' ':!wiki/logs/' \
  ':!.agents/index/logs-index.md' | grep -v 'pull-request-template'

# 2. The mod builds under its new names, with core checked out beside it.
./gradlew build
```

The first command must print nothing. The second must pass, and
`platforms/{fabric,neoforge,forge}/build/libs/` must hold `MCAgents{Pascal}Fabric-0.0.0.jar`,
`MCAgents{Pascal}Neoforge-0.0.0.jar` and `MCAgents{Pascal}Forge-0.0.0.jar`.

## 5. Clean up

In the release task, delete:

* this file, `PROMPT.md`;
* the "Read `PROMPT.md` first" section at the top of `AGENTS.md`, down to and including the
  `---` line under it;
* the template's changelog, `wiki/logs/0/0/0/CHANGELOG.md`, replaced by the mod's own for
  `0.0.0`, with its row in `.agents/index/logs-index.md` rewritten to match;
* the `PROMPT.md` line in `.agents/wiki/context/repository-map.md`.

After that, `git grep -n -i template -- . ':!gradlew' | grep -v pull-request-template` prints
nothing, and the new mod follows `AGENTS.md` like any other MCAgents repository.
