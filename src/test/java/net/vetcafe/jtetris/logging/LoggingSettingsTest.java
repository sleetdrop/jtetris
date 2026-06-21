package net.vetcafe.jtetris.logging;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingSettingsTest {

    @Test
    void defaultsToErrorAndPlatformLogDirectory() {
        LoggingSettings settings = LoggingSettings.parse(new Properties(), Path.of("/app/data"));

        assertEquals(LogLevel.ERROR, settings.globalLevel());
        assertEquals(LogLevel.ERROR, settings.inputLevel());
        assertEquals(Path.of("/app/data/logs"), settings.logDirectory());
        assertEquals("10MB", settings.maxFileSize());
        assertEquals(7, settings.maxHistory());
        assertEquals("100MB", settings.totalSizeCap());
        assertFalse(settings.edtWatchdogEnabled());
        assertEquals(500, settings.edtWatchdogThresholdMs());
        assertFalse(settings.externalConfiguration());
        assertTrue(settings.warnings().isEmpty());
    }

    @Test
    void debugModeDefaultsGlobalLevelToDebugAndEnablesWatchdog() {
        Properties properties = properties("jtetris.debug", "true");

        LoggingSettings settings = LoggingSettings.parse(properties, Path.of("/app/data"));

        assertEquals(LogLevel.DEBUG, settings.globalLevel());
        assertEquals(LogLevel.DEBUG, settings.inputLevel());
        assertTrue(settings.edtWatchdogEnabled());
    }

    @Test
    void explicitLevelsOverrideDebugAndAllowInputTrace() {
        Properties properties = properties(
                "jtetris.debug", "true",
                "jtetris.log.level", "WARN",
                "jtetris.log.input.level", "TRACE"
        );

        LoggingSettings settings = LoggingSettings.parse(properties, Path.of("/app/data"));

        assertEquals(LogLevel.WARN, settings.globalLevel());
        assertEquals(LogLevel.TRACE, settings.inputLevel());
    }

    @Test
    void acceptsAbsoluteDirectoryAndRollingOverrides() {
        Properties properties = properties(
                "jtetris.log.dir", "/tmp/jtetris-logs",
                "jtetris.log.maxFileSize", "25MB",
                "jtetris.log.maxHistory", "14",
                "jtetris.log.totalSizeCap", "250MB",
                "jtetris.log.edtWatchdog.enabled", "true",
                "jtetris.log.edtWatchdog.thresholdMs", "750"
        );

        LoggingSettings settings = LoggingSettings.parse(properties, Path.of("/app/data"));

        assertEquals(Path.of("/tmp/jtetris-logs"), settings.logDirectory());
        assertEquals("25MB", settings.maxFileSize());
        assertEquals(14, settings.maxHistory());
        assertEquals("250MB", settings.totalSizeCap());
        assertTrue(settings.edtWatchdogEnabled());
        assertEquals(750, settings.edtWatchdogThresholdMs());
    }

    @Test
    void invalidValuesFallBackAndProduceWarnings() {
        Properties properties = properties(
                "jtetris.log.level", "LOUD",
                "jtetris.log.input.level", "VERBOSE",
                "jtetris.log.dir", "relative/logs",
                "jtetris.log.maxFileSize", "huge",
                "jtetris.log.maxHistory", "0",
                "jtetris.log.totalSizeCap", "-1MB",
                "jtetris.log.edtWatchdog.enabled", "maybe",
                "jtetris.log.edtWatchdog.thresholdMs", "-5"
        );

        LoggingSettings settings = LoggingSettings.parse(properties, Path.of("/app/data"));

        assertEquals(LogLevel.ERROR, settings.globalLevel());
        assertEquals(LogLevel.ERROR, settings.inputLevel());
        assertEquals(Path.of("/app/data/logs"), settings.logDirectory());
        assertEquals("10MB", settings.maxFileSize());
        assertEquals(7, settings.maxHistory());
        assertEquals("100MB", settings.totalSizeCap());
        assertFalse(settings.edtWatchdogEnabled());
        assertEquals(500, settings.edtWatchdogThresholdMs());
        assertEquals(8, settings.warnings().size());
    }

    @Test
    void externalConfigurationIsDetected() {
        Properties properties = properties(
                "logback.configurationFile", "/tmp/custom-logback.xml",
                "jtetris.debug", "true"
        );

        LoggingSettings settings = LoggingSettings.parse(properties, Path.of("/app/data"));

        assertTrue(settings.externalConfiguration());
    }

    private static Properties properties(String... pairs) {
        Properties properties = new Properties();
        for (int i = 0; i < pairs.length; i += 2) {
            properties.setProperty(pairs[i], pairs[i + 1]);
        }
        return properties;
    }
}
