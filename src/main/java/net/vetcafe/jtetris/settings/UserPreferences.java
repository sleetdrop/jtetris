package net.vetcafe.jtetris.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import net.vetcafe.jtetris.platform.ApplicationDataPaths;
import net.vetcafe.jtetris.ui.UiTheme;

public class UserPreferences {
    private static final String THEME_MODE = "theme.mode";

    private final Path storeFile;

    public UserPreferences() {
        this(ApplicationDataPaths.preferencesFile(ApplicationDataPaths.currentRoot()));
    }

    UserPreferences(Path storeFile) {
        this.storeFile = storeFile;
    }

    public Optional<UiTheme.Mode> loadThemeMode() {
        String value = load().getProperty(THEME_MODE);
        if (value == null) {
            return Optional.empty();
        }
        return parseThemeMode(value);
    }

    public boolean saveThemeMode(UiTheme.Mode mode) {
        if (mode == null) {
            return false;
        }
        Properties properties = load();
        properties.setProperty(THEME_MODE, mode.name().toLowerCase(Locale.ROOT));
        return save(properties);
    }

    private Optional<UiTheme.Mode> parseThemeMode(String value) {
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "auto" -> Optional.of(UiTheme.Mode.AUTO);
            case "light" -> Optional.of(UiTheme.Mode.LIGHT);
            case "dark" -> Optional.of(UiTheme.Mode.DARK);
            default -> Optional.empty();
        };
    }

    private Properties load() {
        Properties properties = new Properties();
        if (!Files.exists(storeFile)) {
            return properties;
        }
        try (InputStream input = Files.newInputStream(storeFile)) {
            properties.load(input);
        } catch (IOException | IllegalArgumentException ignored) {
            return new Properties();
        }
        return properties;
    }

    private boolean save(Properties properties) {
        Path parent = storeFile.toAbsolutePath().getParent();
        Path temporary = null;
        try {
            if (parent != null) {
                Files.createDirectories(parent);
                temporary = Files.createTempFile(parent, "jtetris-preferences-", ".tmp");
            } else {
                temporary = Files.createTempFile("jtetris-preferences-", ".tmp");
            }
            try (OutputStream output = Files.newOutputStream(temporary)) {
                properties.store(output, "JTetris local preferences");
            }
            try {
                Files.move(temporary, storeFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, storeFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException ignored) {
            return false;
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException ignored) {
                    // Best-effort temporary-file cleanup.
                }
            }
        }
    }
}
