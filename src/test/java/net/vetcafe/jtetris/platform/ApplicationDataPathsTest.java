package net.vetcafe.jtetris.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ApplicationDataPathsTest {
    private final Path sandbox = Path.of(System.getProperty("java.io.tmpdir")).toAbsolutePath();
    private final Path home = sandbox.resolve("home");
    private final Path xdgDataHome = sandbox.resolve("xdg-data");

    @Test
    void resolvesMacApplicationDirectory() {
        assertEquals(
                home.resolve("Library/Application Support/net.vetcafe.jtetris"),
                ApplicationDataPaths.resolveRoot("Mac OS X", home, null, null));
    }

    @Test
    void resolvesLinuxXdgApplicationDirectory() {
        assertEquals(
                xdgDataHome.resolve("net.vetcafe.jtetris"),
                ApplicationDataPaths.resolveRoot("Linux", home, xdgDataHome.toString(), null));
    }

    @Test
    void ignoresRelativeLinuxXdgDirectory() {
        assertEquals(
                home.resolve(".local/share/net.vetcafe.jtetris"),
                ApplicationDataPaths.resolveRoot("Linux", home, "relative/data", null));
    }

    @Test
    void resolvesWindowsApplicationDirectory() {
        Path localAppData = home.resolve("AppData/Local");

        assertEquals(
                localAppData.resolve("net.vetcafe.jtetris"),
                ApplicationDataPaths.resolveRoot("Windows 11", home, null, localAppData.toString()));
    }

    @Test
    void derivesScoreFileAndLogDirectoryFromSameRoot() {
        Path root = xdgDataHome.resolve("net.vetcafe.jtetris");

        assertEquals(root.resolve("scores.properties"), ApplicationDataPaths.scoreFile(root));
        assertEquals(root.resolve("preferences.properties"), ApplicationDataPaths.preferencesFile(root));
        assertEquals(root.resolve("logs"), ApplicationDataPaths.logDirectory(root));
    }
}
