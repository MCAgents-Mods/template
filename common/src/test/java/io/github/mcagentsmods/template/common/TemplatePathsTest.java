package io.github.mcagentsmods.template.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplatePathsTest {

    @TempDir
    Path gameDirectory;

    @Test
    void theModOwnsItsOwnDirectoryUnderMCAgents() {
        TemplatePaths paths = TemplatePaths.under(gameDirectory);

        assertEquals(gameDirectory.resolve("MCAgents").resolve("template"), paths.dataDirectory());
    }

    @Test
    void describingTheLayoutCreatesNothing() {
        TemplatePaths.under(gameDirectory);

        assertFalse(Files.exists(gameDirectory.resolve("MCAgents")));
    }

    @Test
    void createDataDirectoryCreatesItAndIsSafeToRepeat() throws Exception {
        TemplatePaths paths = TemplatePaths.under(gameDirectory);

        paths.createDataDirectory();
        paths.createDataDirectory();

        assertTrue(Files.isDirectory(paths.dataDirectory()));
    }

    @Test
    void aMissingGameDirectoryIsRejected() {
        assertThrows(NullPointerException.class, () -> TemplatePaths.under(null));
    }
}
