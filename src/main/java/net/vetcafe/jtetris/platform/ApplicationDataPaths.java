package net.vetcafe.jtetris.platform;

import java.nio.file.Path;
import java.util.Locale;

public final class ApplicationDataPaths {
    public static final String APP_DIRECTORY = "net.vetcafe.jtetris";
    public static final String SCORE_FILE = "scores.properties";
    public static final String PREFERENCES_FILE = "preferences.properties";
    public static final String LOG_DIRECTORY = "logs";

    private ApplicationDataPaths() {}

    public static Path currentRoot() {
        Path home = Path.of(System.getProperty("user.home", "."));
        return resolveRoot(
                System.getProperty("os.name", ""), home, System.getenv("XDG_DATA_HOME"), System.getenv("LOCALAPPDATA"));
    }

    public static Path resolveRoot(String osName, Path home, String xdgDataHome, String localAppData) {
        String normalizedOs = osName == null ? "" : osName.toLowerCase(Locale.ROOT);
        Path dataRoot;
        if (normalizedOs.contains("mac")) {
            dataRoot = home.resolve("Library").resolve("Application Support");
        } else if (normalizedOs.contains("win")) {
            dataRoot = pathWhenPresent(localAppData);
            if (dataRoot == null) {
                dataRoot = home.resolve("AppData").resolve("Local");
            }
        } else {
            dataRoot = absolutePathWhenPresent(xdgDataHome);
            if (dataRoot == null) {
                dataRoot = home.resolve(".local").resolve("share");
            }
        }
        return dataRoot.resolve(APP_DIRECTORY);
    }

    public static Path scoreFile(Path root) {
        return root.resolve(SCORE_FILE);
    }

    public static Path preferencesFile(Path root) {
        return root.resolve(PREFERENCES_FILE);
    }

    public static Path logDirectory(Path root) {
        return root.resolve(LOG_DIRECTORY);
    }

    private static Path pathWhenPresent(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Path.of(value);
    }

    private static Path absolutePathWhenPresent(String value) {
        Path path = pathWhenPresent(value);
        return path != null && path.isAbsolute() ? path : null;
    }
}
