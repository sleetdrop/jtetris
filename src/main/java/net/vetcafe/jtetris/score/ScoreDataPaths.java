package net.vetcafe.jtetris.score;

import java.nio.file.Path;
import java.util.Locale;

final class ScoreDataPaths {
    static final String APP_DIRECTORY = "net.vetcafe.jtetris";
    static final String SCORE_FILE = "scores.properties";

    private ScoreDataPaths() {
    }

    static Path current() {
        Path home = Path.of(System.getProperty("user.home", "."));
        return resolve(
                System.getProperty("os.name", ""),
                home,
                System.getenv("XDG_DATA_HOME"),
                System.getenv("LOCALAPPDATA")
        );
    }

    static Path legacy(Path home) {
        return home.resolve(".tetris_scores.properties");
    }

    static Path resolve(String osName, Path home, String xdgDataHome, String localAppData) {
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
        return dataRoot.resolve(APP_DIRECTORY).resolve(SCORE_FILE);
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
