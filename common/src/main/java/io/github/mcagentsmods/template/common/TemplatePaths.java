package io.github.mcagentsmods.template.common;

import io.github.mcagentsmods.template.api.MCAgentsTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Everything the mod owns on disk, resolved from a game directory it is handed.
 *
 * <p>This class knows nothing about Minecraft. A loader module resolves the game directory
 * and calls {@link #under(Path)}; from there the layout is the same whether it came from a
 * real installation or from a temporary directory in a test.
 */
public final class TemplatePaths {

    private final Path dataDirectory;

    private TemplatePaths(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    /**
     * Describes the mod's layout under {@code gameDirectory}. Creates nothing — call
     * {@link #createDataDirectory()} for that.
     *
     * @param gameDirectory the Minecraft game directory
     */
    public static TemplatePaths under(Path gameDirectory) {
        Objects.requireNonNull(gameDirectory, "gameDirectory");
        return new TemplatePaths(gameDirectory
                .resolve(MCAgentsTemplate.DATA_DIRECTORY)
                .resolve(MCAgentsTemplate.MOD_DIRECTORY)
                .normalize());
    }

    /** {@code {game_dir}/MCAgents/template} — the directory holding every file the mod owns. */
    public Path dataDirectory() {
        return dataDirectory;
    }

    /** Creates {@link #dataDirectory()} and its parents if they do not exist yet. */
    public Path createDataDirectory() throws IOException {
        return Files.createDirectories(dataDirectory);
    }
}
