---
name: wiki-environments-setup
description: Toolchain requirements, where versions live, and the commands to build and run the mod locally.
---

# Development Setup

## Requirements

| Tool | Version | Note |
|---|---|---|
| JDK | 21 or newer to launch Gradle; 25 to compile | The `foojay-resolver-convention` plugin downloads JDK 25 if you do not have it. |
| Gradle | 9.7.0 | Comes from the committed wrapper. Use `./gradlew`, never a system `gradle`. |
| Git | any recent | |

## Where versions live

`gradle.properties` holds the project's identity only:

```properties
git-org=MCAgents-Mods
git-repo=template
project-version=0.0.0
```

Every dependency coordinate lives in the Gradle version catalog at
`gradle/libs.versions.toml`:

| Entry | Value |
|---|---|
| Java | 25 |
| Fabric Loader | 0.19.3 |
| Fabric Loom | 1.17.19 |
| ModDevGradle | 2.0.148 |
| ForgeGradle | 7.0.40 |
| MCAgents core | 0.7.0 |

The entries that move with Minecraft come in one block per Minecraft version, each suffixed
with it (`fabric-api-26-2` for 26.2):

| Entry | 26.2 |
|---|---|
| Minecraft | 26.2 |
| Fabric API | 0.157.0+26.2 |
| NeoForge | 26.2.0.90 |
| Forge | 26.2-65.1.3 |

Keep the Minecraft and loader versions the same as core's own catalog. A mod built for a
different Minecraft than core's cannot load beside it.

## One build per Minecraft version

`platforms/shared` and the three loader modules compile against Minecraft, so each is built
once for every Minecraft version, listed once in `settings.gradle`. Each is a folder of shared
sources with one Gradle subproject per version, `platforms/{module}/{minecraft}`, named
`mcagents-template-{module}-{minecraft}`; that name is also its artifact id. The subproject
compiles the module's shared `src/` together with its own `src/`, which holds only what
differs for that version, such as Forge's `pack.mcmeta`. Code that differs between versions
is a class of the same name in each version folder; shared code never checks the version.
`platforms/minecraft.gradle`, which every version subproject applies, takes the version from
the folder's name, finds its coordinates in the catalog, and names the jar
`MCAgentsTemplate{Module}-{version}+{minecraft}.jar`. The loader metadata is expanded per
version, so each jar declares its own Minecraft version and no other. `api` and `common` name
no Minecraft type and are built once. Adding a version is a catalog block, an entry in
`settings.gradle`'s list, and a folder per module.

## MCAgents core

The mod compiles against MCAgents core and does not carry it. Clone `MCAgents-Mods/core`
beside this repository — `../core` — and `settings.gradle` builds the two together. Pass
`-Pcore-build=/path/to/core` for another location. Without a checkout, Gradle resolves the
published `io.github.mcagentsmods:mcagents-core-api` and `mcagents-core-common` at the
version in the catalog from GitHub Packages, `https://maven.pkg.github.com/MCAgents-Mods/core`.
GitHub Packages asks for a token even to read a public package, so set `GITHUB_ACTOR` to
your GitHub user name and `GITHUB_TOKEN` to a token with the `read:packages` scope.

## Build

```bash
./gradlew build
```

Builds every module, runs the `common/` tests, and writes one mod jar per loader and Minecraft
version to `platforms/{fabric,neoforge,forge}/{minecraft}/build/libs/`, such as
`platforms/fabric/26.2/build/libs/MCAgentsTemplateFabric-{version}+26.2.jar`.

## Run

```bash
./gradlew :mcagents-template-fabric-26.2:runClient
./gradlew :mcagents-template-neoforge-26.2:runClient
./gradlew :mcagents-template-forge-26.2:runClient
```

`runClient` does not put core in the development game. Build core and copy its jar for the
same loader and Minecraft version into that version subproject's `run/mods/` first.

## Publish

Nobody runs `./gradlew publish` by hand for a release. The **Publish** workflow,
`.github/workflows/publish.yml`, runs on every push to `master`:

1. It reads `project-version` from `gradle.properties`. At `0.0.0`, the version a mod
   carries before its first release, it stops and publishes nothing. It also stops when the
   tag `v{version}` already exists, so a merge that leaves the version alone publishes
   nothing.
2. Otherwise it builds and tests against the core version in the catalog, read from GitHub
   Packages, publishes all six modules to GitHub Packages under this repository, and only
   then tags the commit `v{version}`.

So a release is the merge that raises `project-version`, and the core version it pins must
already be published. A failed run leaves no tag, and the next push to `master`, or **Run
workflow** on the Publish workflow's page in the Actions tab, tries again. GitHub Packages
refuses to overwrite a version, so if a run failed after some modules were uploaded, delete
those package versions before retrying.

## Release

The mod jars a player installs are attached to the repository's **Releases** page, one
GitHub release per version. The **Release** workflow, `.github/workflows/release.yml`, runs
each time a Publish run on `master` finishes:

1. It reads `project-version`. At `0.0.0` it stops and releases nothing. It also stops when
   the tag `v{version}` does not exist, or already has a GitHub release.
2. Otherwise it checks out the tag, runs `./gradlew assemble` with the same token Publish
   uses, and takes one jar from `platforms/forge`, `platforms/fabric` and
   `platforms/neoforge` for every Minecraft version, from each `{minecraft}/build/libs/`.
3. It creates the `v{version}` release with those jars attached and that version's
   `wiki/logs/{Major}/{Minor}/{Patch}/CHANGELOG.md` as its notes, then deletes the copy of
   the jars the run kept in Actions.

A release that failed is made by the next merge to `master`. **Run workflow** on the
Release workflow's page does the same by hand; its `tag` input releases an earlier tag that
has none.
