package io.github.mcagentsmods.template.neoforge;

import io.github.mcagentsmods.template.api.MCAgentsTemplate;
import io.github.mcagentsmods.template.shared.TemplateRuntime;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

/**
 * NeoForge's client entry point: the game directory, and nothing else. Everything the mod
 * does lives in {@code platforms/shared}.
 *
 * <p>Declared for the client distribution only, so a dedicated server that has the jar in its
 * mods folder never constructs it.
 */
@Mod(value = MCAgentsTemplate.MOD_ID, dist = Dist.CLIENT)
public final class TemplateNeoForgeClient {

    public TemplateNeoForgeClient() {
        TemplateRuntime.start(FMLPaths.GAMEDIR.get());
    }
}
