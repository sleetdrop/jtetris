package net.vetcafe.jtetris.score;

import net.vetcafe.jtetris.platform.ApplicationDataPaths;

import java.nio.file.Path;

final class ScoreDataPaths {
    static final String APP_DIRECTORY = ApplicationDataPaths.APP_DIRECTORY;
    static final String SCORE_FILE = ApplicationDataPaths.SCORE_FILE;

    private ScoreDataPaths() {
    }

    static Path current() {
        return ApplicationDataPaths.scoreFile(ApplicationDataPaths.currentRoot());
    }

    static Path legacy(Path home) {
        return home.resolve(".tetris_scores.properties");
    }

    static Path resolve(String osName, Path home, String xdgDataHome, String localAppData) {
        return ApplicationDataPaths.scoreFile(
                ApplicationDataPaths.resolveRoot(osName, home, xdgDataHome, localAppData)
        );
    }
}
