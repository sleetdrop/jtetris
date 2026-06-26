package net.vetcafe.jtetris.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import net.vetcafe.jtetris.ui.UiTheme;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UserPreferencesTest {
    @TempDir
    Path tempDir;

    @Test
    void savesAndLoadsThemeMode() {
        Path store = tempDir.resolve("data/net.vetcafe.jtetris/preferences.properties");
        UserPreferences preferences = new UserPreferences(store);

        preferences.saveThemeMode(UiTheme.Mode.DARK);

        UserPreferences reloaded = new UserPreferences(store);
        assertEquals(Optional.of(UiTheme.Mode.DARK), reloaded.loadThemeMode());
    }

    @Test
    void invalidThemeModeIsIgnored() throws IOException {
        Path store = tempDir.resolve("data/net.vetcafe.jtetris/preferences.properties");
        Files.createDirectories(store.getParent());
        Files.writeString(store, "theme.mode=neon\n");

        UserPreferences preferences = new UserPreferences(store);

        assertTrue(preferences.loadThemeMode().isEmpty());
    }

    @Test
    void missingStoreFallsBackToEmptyPreferences() {
        UserPreferences preferences = new UserPreferences(tempDir.resolve("missing/preferences.properties"));

        assertTrue(preferences.loadThemeMode().isEmpty());
    }
}
