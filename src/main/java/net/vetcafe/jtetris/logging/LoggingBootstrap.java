package net.vetcafe.jtetris.logging;

import net.vetcafe.jtetris.platform.ApplicationDataPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class LoggingBootstrap {
    private static final String APP_LOGGER = "net.vetcafe.jtetris";
    private static volatile LoggingSettings currentSettings;

    private LoggingBootstrap() {
    }

    public static synchronized void initialize() {
        if (currentSettings != null) {
            return;
        }

        Properties systemProperties = System.getProperties();
        LoggingSettings settings = prepareBundled(
                systemProperties,
                ApplicationDataPaths.currentRoot(),
                Files::createDirectories
        );
        printWarnings(settings, System.err);

        Logger logger = LoggerFactory.getLogger(APP_LOGGER);
        currentSettings = settings;
        installUncaughtExceptionHandler();
        logger.info(
                "event=application_start debug={} globalLevel={} inputLevel={} externalConfig={}",
                settings.debug(),
                settings.globalLevel(),
                settings.inputLevel(),
                settings.externalConfiguration()
        );
    }

    public static boolean edtWatchdogEnabled() {
        LoggingSettings settings = currentSettings;
        return settings != null && settings.edtWatchdogEnabled();
    }

    public static long edtWatchdogThresholdMs() {
        LoggingSettings settings = currentSettings;
        return settings == null
                ? LoggingSettings.DEFAULT_WATCHDOG_THRESHOLD_MS
                : settings.edtWatchdogThresholdMs();
    }

    static LoggingSettings prepareBundled(
            Properties properties,
            Path applicationRoot,
            DirectoryCreator directoryCreator
    ) {
        LoggingSettings settings = LoggingSettings.parse(properties, applicationRoot);
        if (settings.externalConfiguration()) {
            return settings;
        }

        properties.setProperty("jtetris.log.level.resolved", settings.globalLevel().name());
        properties.setProperty("jtetris.log.input.level.resolved", settings.inputLevel().name());
        properties.setProperty("jtetris.log.dir.resolved", settings.logDirectory().toString());
        properties.setProperty("jtetris.log.maxFileSize.resolved", settings.maxFileSize());
        properties.setProperty(
                "jtetris.log.maxHistory.resolved",
                Integer.toString(settings.maxHistory())
        );
        properties.setProperty("jtetris.log.totalSizeCap.resolved", settings.totalSizeCap());

        try {
            directoryCreator.create(settings.logDirectory());
        } catch (IOException | RuntimeException e) {
            System.err.println(
                    "JTetris logging: cannot initialize log directory "
                            + settings.logDirectory()
                            + ": "
                            + e.getMessage()
            );
            URL fallback = LoggingBootstrap.class.getResource("/logback-stderr.xml");
            if (fallback != null) {
                properties.setProperty("logback.configurationFile", fallback.toExternalForm());
            }
        }
        return settings;
    }

    static void installUncaughtExceptionHandler() {
        Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            LoggerFactory.getLogger(APP_LOGGER).error(
                    "event=uncaught_exception thread={}",
                    thread.getName(),
                    error
            );
            if (previous != null) {
                previous.uncaughtException(thread, error);
            }
        });
    }

    private static void printWarnings(LoggingSettings settings, PrintStream err) {
        for (String warning : settings.warnings()) {
            err.println("JTetris logging: " + warning);
        }
    }

    @FunctionalInterface
    interface DirectoryCreator {
        void create(Path path) throws IOException;
    }
}
