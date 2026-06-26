package net.vetcafe.jtetris.logging;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import net.vetcafe.jtetris.platform.ApplicationDataPaths;

record LoggingSettings(
        boolean debug,
        LogLevel globalLevel,
        LogLevel inputLevel,
        Path logDirectory,
        String maxFileSize,
        int maxHistory,
        String totalSizeCap,
        boolean edtWatchdogEnabled,
        long edtWatchdogThresholdMs,
        boolean externalConfiguration,
        List<String> warnings) {
    static final String DEFAULT_MAX_FILE_SIZE = "10MB";
    static final int DEFAULT_MAX_HISTORY = 7;
    static final String DEFAULT_TOTAL_SIZE_CAP = "100MB";
    static final long DEFAULT_WATCHDOG_THRESHOLD_MS = 500;
    private static final Pattern FILE_SIZE = Pattern.compile("[1-9][0-9]*(KB|MB|GB)");

    LoggingSettings {
        warnings = List.copyOf(warnings);
    }

    static LoggingSettings parse(Properties properties, Path applicationRoot) {
        List<String> warnings = new ArrayList<>();
        boolean debug = parseBoolean(properties, "jtetris.debug", false, warnings);
        LogLevel defaultGlobal = debug ? LogLevel.DEBUG : LogLevel.ERROR;
        LogLevel global = parseLevel(properties, "jtetris.log.level", defaultGlobal, warnings);
        LogLevel input = parseLevel(properties, "jtetris.log.input.level", global, warnings);
        Path defaultLogDirectory = ApplicationDataPaths.logDirectory(applicationRoot);
        Path logDirectory = parseDirectory(properties, defaultLogDirectory, warnings);
        String maxFileSize = parseFileSize(properties, "jtetris.log.maxFileSize", DEFAULT_MAX_FILE_SIZE, warnings);
        int maxHistory = parsePositiveInt(properties, "jtetris.log.maxHistory", DEFAULT_MAX_HISTORY, warnings);
        String totalSizeCap = parseFileSize(properties, "jtetris.log.totalSizeCap", DEFAULT_TOTAL_SIZE_CAP, warnings);
        boolean watchdogEnabled = parseBoolean(properties, "jtetris.log.edtWatchdog.enabled", debug, warnings);
        long watchdogThreshold = parsePositiveLong(
                properties, "jtetris.log.edtWatchdog.thresholdMs", DEFAULT_WATCHDOG_THRESHOLD_MS, warnings);
        boolean externalConfiguration = hasText(properties.getProperty("logback.configurationFile"));

        return new LoggingSettings(
                debug,
                global,
                input,
                logDirectory,
                maxFileSize,
                maxHistory,
                totalSizeCap,
                watchdogEnabled,
                watchdogThreshold,
                externalConfiguration,
                warnings);
    }

    private static LogLevel parseLevel(Properties properties, String key, LogLevel fallback, List<String> warnings) {
        String value = properties.getProperty(key);
        if (!hasText(value)) {
            return fallback;
        }
        return LogLevel.parse(value).orElseGet(() -> {
            warnings.add(key + " has invalid level: " + value);
            return fallback;
        });
    }

    private static Path parseDirectory(Properties properties, Path fallback, List<String> warnings) {
        String value = properties.getProperty("jtetris.log.dir");
        if (!hasText(value)) {
            return fallback;
        }
        try {
            Path path = Path.of(value.trim());
            if (path.isAbsolute()) {
                return path;
            }
        } catch (RuntimeException ignored) {
            // Report the invalid value below.
        }
        warnings.add("jtetris.log.dir must be an absolute path: " + value);
        return fallback;
    }

    private static String parseFileSize(Properties properties, String key, String fallback, List<String> warnings) {
        String value = properties.getProperty(key);
        if (!hasText(value)) {
            return fallback;
        }
        String normalized = value.trim().toUpperCase();
        if (FILE_SIZE.matcher(normalized).matches()) {
            return normalized;
        }
        warnings.add(key + " has invalid size: " + value);
        return fallback;
    }

    private static int parsePositiveInt(Properties properties, String key, int fallback, List<String> warnings) {
        String value = properties.getProperty(key);
        if (!hasText(value)) {
            return fallback;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed > 0) {
                return parsed;
            }
        } catch (NumberFormatException ignored) {
            // Report the invalid value below.
        }
        warnings.add(key + " must be a positive integer: " + value);
        return fallback;
    }

    private static long parsePositiveLong(Properties properties, String key, long fallback, List<String> warnings) {
        String value = properties.getProperty(key);
        if (!hasText(value)) {
            return fallback;
        }
        try {
            long parsed = Long.parseLong(value.trim());
            if (parsed > 0) {
                return parsed;
            }
        } catch (NumberFormatException ignored) {
            // Report the invalid value below.
        }
        warnings.add(key + " must be a positive integer: " + value);
        return fallback;
    }

    private static boolean parseBoolean(Properties properties, String key, boolean fallback, List<String> warnings) {
        String value = properties.getProperty(key);
        if (!hasText(value)) {
            return fallback;
        }
        if ("true".equalsIgnoreCase(value.trim())) {
            return true;
        }
        if ("false".equalsIgnoreCase(value.trim())) {
            return false;
        }
        warnings.add(key + " must be true or false: " + value);
        return fallback;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
