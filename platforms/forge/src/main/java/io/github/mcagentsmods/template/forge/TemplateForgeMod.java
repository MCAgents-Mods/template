package io.github.mcagentsmods.template.forge;

import io.github.mcagentsmods.template.api.MCAgentsTemplate;
import io.github.mcagentsmods.template.shared.TemplateRuntime;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * Forge's entry point: the game directory, and nothing else. Everything the mod does lives in
 * {@code platforms/shared}.
 *
 * <p>Forge constructs a mod on both sides, so the mod starts only when Forge reports a client.
 * A dedicated server with the jar in its mods folder loads the class and does nothing.
 */
@Mod(MCAgentsTemplate.MOD_ID)
public final class TemplateForgeMod {

    public TemplateForgeMod() {
        if (FMLEnvironment.dist.isClient()) {
            TemplateRuntime.start(FMLPaths.GAMEDIR.get());
        }
    }
}
