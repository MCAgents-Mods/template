package io.github.mcagentsmods.template.api;

/**
 * The mod's identity and the layout it owns on the player's disk.
 *
 * <p>Shared by every module. The paths are names rather than resolved {@link java.nio.file.Path}s
 * on purpose: only a loader module knows where the game directory is, so it resolves them and
 * hands the result down.
 */
public final class MCAgentsTemplate {

    /** Mod id, as declared in each loader's mod manifest. */
    public static final String MOD_ID = "mcagents_template";

    /** Human-readable mod name, used for logging and in the interface. */
    public static final String MOD_NAME = "MCAgentsTemplate";

    /** Directory every MCAgents mod keeps its files under, inside the game directory. */
    public static final String DATA_DIRECTORY = "MCAgents";

    /**
     * This mod's own directory inside {@link #DATA_DIRECTORY}, named for its repository, so no
     * two mods share files. Every file the mod keeps, configuration and data alike, goes in it:
     * core keeps its files in {@code MCAgents/core/}, chat in {@code MCAgents/chat/}.
     */
    public static final String MOD_DIRECTORY = "template";

    private MCAgentsTemplate() {
        throw new AssertionError("No instances.");
    }
}
