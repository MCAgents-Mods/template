package io.github.mcagentsmods.template.fabric;

import io.github.mcagentsmods.template.shared.TemplateRuntime;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Fabric's client entry point: the game directory, and nothing else. Everything the mod does
 * lives in {@code platforms/shared}.
 */
public final class TemplateFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        TemplateRuntime.start(FabricLoader.getInstance().getGameDir());
    }
}
