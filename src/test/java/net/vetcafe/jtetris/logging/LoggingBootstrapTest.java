package net.vetcafe.jtetris.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingBootstrapTest {
    @TempDir
    Path tempDir;

    private Thread.UncaughtExceptionHandler previousHandler;

    @AfterEach
    void restoreHandler() {
        if (previousHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(previousHandler);
        }
    }

    @Test
    void preparesResolvedPropertiesAndCreatesDirectory() {
        Properties source = new Properties();
        source.setProperty("jtetris.debug", "true");
        source.setProperty("jtetris.log.input.level", "TRACE");
        source.setProperty("jtetris.log.maxFileSize", "25MB");
        Path root = tempDir.resolve("app");
        AtomicBoolean created = new AtomicBoolean();

        LoggingSettings settings = LoggingBootstrap.prepareBundled(
                source,
                root,
                path -> {
                    Files.createDirectories(path);
                    created.set(true);
                }
        );

        assertTrue(created.get());
        assertEquals("DEBUG", source.getProperty("jtetris.log.level.resolved"));
        assertEquals("TRACE", source.getProperty("jtetris.log.input.level.resolved"));
        assertEquals("25MB", source.getProperty("jtetris.log.maxFileSize.resolved"));
        assertEquals(
                settings.logDirectory().toString(),
                source.getProperty("jtetris.log.dir.resolved")
        );
    }

    @Test
    void externalConfigurationBypassesBundledPropertiesAndDirectoryCreation() throws IOException {
        Properties source = new Properties();
        source.setProperty("logback.configurationFile", "/tmp/custom.xml");
        AtomicBoolean created = new AtomicBoolean();

        LoggingSettings settings = LoggingBootstrap.prepareBundled(
                source,
                tempDir,
                path -> created.set(true)
        );

        assertTrue(settings.externalConfiguration());
        assertFalse(created.get());
        assertFalse(source.containsKey("jtetris.log.level.resolved"));
    }

    @Test
    void directoryFailureSelectsStderrFallbackConfiguration() {
        Properties source = new Properties();

        LoggingBootstrap.prepareBundled(
                source,
                tempDir,
                path -> {
                    throw new IOException("denied");
                }
        );

        String configuration = source.getProperty("logback.configurationFile");
        assertNotNull(configuration);
        assertTrue(configuration.contains("logback-stderr.xml"));
    }

    @Test
    void installsDefaultUncaughtExceptionHandler() {
        previousHandler = Thread.getDefaultUncaughtExceptionHandler();

        LoggingBootstrap.installUncaughtExceptionHandler();

        assertNotNull(Thread.getDefaultUncaughtExceptionHandler());
    }
}
