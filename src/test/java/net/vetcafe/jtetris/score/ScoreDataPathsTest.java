package net.vetcafe.jtetris.score;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ScoreDataPathsTest {
    private final Path sandbox = Path.of(System.getProperty("java.io.tmpdir")).toAbsolutePath();
    private final Path home = sandbox.resolve("home");
    private final Path xdgDataHome = sandbox.resolve("xdg-data");

    @Test
    void resolvesMacApplicationSupportPath() {
        assertEquals(
                home.resolve("Library/Application Support/net.vetcafe.jtetris/scores.properties"),
                ScoreDataPaths.resolve("Mac OS X", home, null, null));
    }

    @Test
    void resolvesAbsoluteXdgDataHome() {
        assertEquals(
                xdgDataHome.resolve("net.vetcafe.jtetris/scores.properties"),
                ScoreDataPaths.resolve("Linux", home, xdgDataHome.toString(), null));
    }

    @Test
    void fallsBackWhenXdgDataHomeIsRelative() {
        assertEquals(
                home.resolve(".local/share/net.vetcafe.jtetris/scores.properties"),
                ScoreDataPaths.resolve("Linux", home, "relative/data", null));
    }

    @Test
    void resolvesWindowsLocalAppData() {
        Path localAppData = home.resolve("AppData/Local");

        assertEquals(
                localAppData.resolve("net.vetcafe.jtetris/scores.properties"),
                ScoreDataPaths.resolve("Windows 11", home, null, localAppData.toString()));
    }

    @Test
    void fallsBackWhenWindowsLocalAppDataIsBlank() {
        assertEquals(
                home.resolve("AppData/Local/net.vetcafe.jtetris/scores.properties"),
                ScoreDataPaths.resolve("Windows 11", home, null, " "));
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
