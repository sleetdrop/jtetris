package net.vetcafe.jtetris.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ApplicationDataPathsTest {
    private final Path home = Path.of("/Users/test");

    @Test
    void resolvesMacApplicationDirectory() {
        assertEquals(
                Path.of("/Users/test/Library/Application Support/net.vetcafe.jtetris"),
                ApplicationDataPaths.resolveRoot("Mac OS X", home, null, null));
    }

    @Test
    void resolvesLinuxXdgApplicationDirectory() {
        assertEquals(
                Path.of("/data/net.vetcafe.jtetris"), ApplicationDataPaths.resolveRoot("Linux", home, "/data", null));
    }

    @Test
    void ignoresRelativeLinuxXdgDirectory() {
        assertEquals(
                Path.of("/Users/test/.local/share/net.vetcafe.jtetris"),
                ApplicationDataPaths.resolveRoot("Linux", home, "relative/data", null));
    }

    @Test
    void resolvesWindowsApplicationDirectory() {
        assertEquals(
                Path.of("C:/Users/test/AppData/Local/net.vetcafe.jtetris"),
                ApplicationDataPaths.resolveRoot(
                        "Windows 11", Path.of("C:/Users/test"), null, "C:/Users/test/AppData/Local"));
    }

    @Test
    void derivesScoreFileAndLogDirectoryFromSameRoot() {
        Path root = Path.of("/data/net.vetcafe.jtetris");

        assertEquals(root.resolve("scores.properties"), ApplicationDataPaths.scoreFile(root));
        assertEquals(root.resolve("logs"), ApplicationDataPaths.logDirectory(root));
    }
}
