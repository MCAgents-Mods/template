package io.github.mcagentsmods.template.shared;

import io.github.mcagentsmods.template.api.MCAgentsTemplate;
import io.github.mcagentsmods.template.common.TemplatePaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The mod's start-up, shared by every loader.
 *
 * <p>Each loader's entry point finds the game directory and calls {@link #start(Path)}, and
 * nothing else. Everything the mod does from then on starts here, so a loader module never
 * holds logic of its own.
 */
public final class TemplateRuntime {

    private static final Logger LOGGER = LoggerFactory.getLogger(MCAgentsTemplate.MOD_NAME);

    private static final AtomicReference<TemplatePaths> PATHS = new AtomicReference<>();

    private TemplateRuntime() {
        throw new AssertionError("No instances.");
    }

    /**
     * Starts the mod. Safe to call more than once; only the first call does anything.
     *
     * <p>Never throws: a mod that fails to start must not take the game down with it, so a
     * failure is logged and the mod carries on without what it could not set up.
     *
     * @param gameDirectory the Minecraft game directory, as the loader reports it
     */
    public static void start(Path gameDirectory) {
        TemplatePaths paths = TemplatePaths.under(gameDirectory);
        if (!PATHS.compareAndSet(null, paths)) {
            return;
        }
        try {
            paths.createDataDirectory();
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("{} could not create {}", MCAgentsTemplate.MOD_NAME, paths.dataDirectory(), e);
        }
        LOGGER.info("{} started, data in {}", MCAgentsTemplate.MOD_NAME, paths.dataDirectory());
    }

    /** The mod's layout on disk, once {@link #start(Path)} has run. */
    public static Optional<TemplatePaths> paths() {
        return Optional.ofNullable(PATHS.get());
    }
}
