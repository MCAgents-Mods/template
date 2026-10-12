# MCAgentsTemplate

The starter for a new **MCAgents** mod: a client-side Minecraft mod for the Fabric, NeoForge
and Forge loaders that runs on [MCAgents core](https://github.com/MCAgents-Mods/core).

It is an empty mod that already builds. It has the module layout, the Gradle build, the three
loader entry points and manifests, and the agent instruction set every MCAgents mod shares,
so a new mod starts from a green build instead of from nothing.

## Start a new mod

1. Create a repository from this template on GitHub (**Use this template**), in the
   `MCAgents-Mods` organization.
2. Clone it beside a checkout of `MCAgents-Mods/core`.
3. Open it with an agent. `AGENTS.md` sends the agent to `PROMPT.md`, which renames every
   placeholder — the repository name, the namespace, the mod id, the jar names — and then
   deletes itself.

To do it by hand, follow `PROMPT.md` yourself; every name to change is listed there.

## Build

```bash
./gradlew build
```

The jars land in `platforms/{fabric,neoforge,forge}/build/libs/`. Requirements and the
version catalog are in [wiki/environments/setup.md](wiki/environments/setup.md); the module
split is in [wiki/information/architecture.md](wiki/information/architecture.md).

## License

MIT — see [LICENSE](LICENSE).
