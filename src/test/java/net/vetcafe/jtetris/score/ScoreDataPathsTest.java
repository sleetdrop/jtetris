package net.vetcafe.jtetris.score;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ScoreDataPathsTest {
    private final Path home = Path.of("/Users/test");

    @Test
    void resolvesMacApplicationSupportPath() {
        assertEquals(
                home.resolve("Library/Application Support/net.vetcafe.jtetris/scores.properties"),
                ScoreDataPaths.resolve("Mac OS X", home, null, null));
    }

    @Test
    void resolvesAbsoluteXdgDataHome() {
        assertEquals(
                Path.of("/data/net.vetcafe.jtetris/scores.properties"),
                ScoreDataPaths.resolve("Linux", home, "/data", null));
    }

    @Test
    void fallsBackWhenXdgDataHomeIsRelative() {
        assertEquals(
                home.resolve(".local/share/net.vetcafe.jtetris/scores.properties"),
                ScoreDataPaths.resolve("Linux", home, "relative/data", null));
    }

    @Test
    void resolvesWindowsLocalAppData() {
        assertEquals(
                Path.of("C:/Users/test/AppData/Local/net.vetcafe.jtetris/scores.properties"),
                ScoreDataPaths.resolve("Windows 11", Path.of("C:/Users/test"), null, "C:/Users/test/AppData/Local"));
    }

    @Test
    void fallsBackWhenWindowsLocalAppDataIsBlank() {
        Path windowsHome = Path.of("C:/Users/test");

        assertEquals(
                windowsHome.resolve("AppData/Local/net.vetcafe.jtetris/scores.properties"),
                ScoreDataPaths.resolve("Windows 11", windowsHome, null, " "));
    }

    @Test
    void unknownPlatformUsesXdgStyleFallback() {
        assertEquals(
                home.resolve(".local/share/net.vetcafe.jtetris/scores.properties"),
                ScoreDataPaths.resolve("Plan 9", home, null, null));
    }

    @Test
    void resolvesLegacyHomeDirectoryFile() {
        assertEquals(home.resolve(".tetris_scores.properties"), ScoreDataPaths.legacy(home));
    }
}
