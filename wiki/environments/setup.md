---
name: wiki-environments-setup
description: Toolchain requirements, where versions live, and the commands to build and run the mod locally.
---

# Development Setup

## Requirements

| Tool | Version | Note |
|---|---|---|
| JDK | 25 | Gradle runs on it: Fabric Loom 1.18, which builds for Minecraft 26.3, refuses an older one. |
| Gradle | 9.7.1 | Comes from the committed wrapper. Use `./gradlew`, never a system `gradle`. |
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
| Fabric Loader | 0.19.5 |
| Fabric Loom | 1.18.4 |
| ModDevGradle | 2.0.148 |
| ForgeGradle | 7.0.40 |
| MCAgents core | 0.13.0 |

The entries that move with Minecraft sit in one block, for the one version the mod supports:

| Entry | Value |
|---|---|
| Minecraft | 26.3 |
| Fabric API | 0.162.0+26.3 |
| NeoForge | 26.3.0.76 |
| Forge | 26.3-66.0.11 |

Keep the Minecraft and loader versions the same as core's own catalog. A mod built for a
different Minecraft than core's cannot load beside it.

## The Minecraft version

The mod supports **only the latest Minecraft release, today 26.3**, and never an older one
beside it. `platforms/shared` and the three loader modules compile against Minecraft. Each is
one folder, `platforms/{module}`, with one `build.gradle` and one `src/`, and is the Gradle
project `template-{module}`; that name is also its artifact id. Each build file reads the
version from the catalog's `minecraft` entry and names the jar `MCAgentsTemplate{Module}-{version}+{minecraft}.jar`, so the jar name keeps the
game's version. The loader metadata is expanded from the same entry, so each jar declares that
Minecraft version and no other. `api` and `common` name no Minecraft type.
Moving to a new Minecraft release replaces the catalog block and the pack formats in Forge's
`pack.mcmeta`; no folder or project is renamed.

## MCAgents core

The mod compiles against MCAgents core and does not carry it. Clone `MCAgents-Mods/core`
beside this repository — `../core` — and `settings.gradle` builds the two together. Pass
`-Pcore-build=/path/to/core` for another location. Without a checkout, Gradle resolves the
published `io.github.mcagentsmods:core-api` and `core-common` at the
version in the catalog from GitHub Packages, `https://maven.pkg.github.com/MCAgents-Mods/core`.
GitHub Packages asks for a token even to read a public package, so set `GITHUB_ACTOR` to
your GitHub user name and `GITHUB_TOKEN` to a token with the `read:packages` scope.

## Build

```bash
./gradlew build
```

Builds every module, runs the `common/` tests, and writes one mod jar per loader to
`platforms/{fabric,neoforge,forge}/build/libs/`, such as
`platforms/fabric/build/libs/MCAgentsTemplateFabric-{version}+26.3.jar`.

## Run

```bash
./gradlew :template-fabric:runClient
./gradlew :template-neoforge:runClient
./gradlew :template-forge:runClient
```

`runClient` does not put core in the development game. Build core and copy its jar for the
same loader and Minecraft version into that loader module's `run/mods/` first.

Minecraft 26.3 draws through Vulkan or OpenGL with an sRGB surface. On a virtual display
(Xvfb), which offers no sRGB OpenGL surface, a 26.3 client needs a Vulkan driver such as
Mesa's software one (lavapipe), and NeoForge's OpenGL-only early loading window must be off:
`earlyWindowControl = false` in the run's `config/fml.toml`.

## Publish

Nobody runs `./gradlew publish` by hand for a release. The **Publish** workflow,
`.github/workflows/publish.yml`, runs on every push to `master`:

1. It reads `project-version` from `gradle.properties`. At `0.0.0`, the version a mod
   carries before its first release, it stops and publishes nothing. It also stops when the
   tag `v{version}` already exists, so a merge that leaves the version alone publishes
   nothing.
2. Otherwise it builds and tests against the core version in the catalog, read from GitHub
   Packages, publishes the library modules (`api`, `common` and `shared`) to GitHub Packages
   under this repository, and only then tags the commit `v{version}`. The mod jars are not
   published there; the Release workflow below attaches them to the version's GitHub release.

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
   `platforms/neoforge`, from each module's `build/libs/`.
3. It creates the `v{version}` release with those jars attached and that version's
   `wiki/logs/{Major}/{Minor}/{Patch}/CHANGELOG.md` as its notes, then deletes the copy of
   the jars the run kept in Actions.

A release that failed is made by the next merge to `master`. **Run workflow** on the
Release workflow's page does the same by hand; its `tag` input releases an earlier tag that
has none.
