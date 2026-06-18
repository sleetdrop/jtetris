package net.vetcafe.jtetris.score;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoreManagerTest {
    @TempDir
    Path tempDir;

    @Test
    void migratesLegacyScoresAndDeletesLegacyFileAfterSave() throws IOException {
        Path store = tempDir.resolve("data/net.vetcafe.jtetris/scores.properties");
        Path legacy = tempDir.resolve(".tetris_scores.properties");
        Files.writeString(legacy, "alice=1200\n");

        ScoreManager manager = new ScoreManager(store, legacy);

        assertEquals(1200, manager.getBest("Alice"));
        assertTrue(Files.exists(store));
        assertFalse(Files.exists(legacy));
        assertEquals("1200", load(store).getProperty("alice"));
    }

    @Test
    void existingNewStoreTakesPrecedenceOverLegacyStore() throws IOException {
        Path store = tempDir.resolve("data/net.vetcafe.jtetris/scores.properties");
        Path legacy = tempDir.resolve(".tetris_scores.properties");
        Files.createDirectories(store.getParent());
        Files.writeString(store, "alice=2400\n");
        Files.writeString(legacy, "alice=1200\nbob=800\n");

        ScoreManager manager = new ScoreManager(store, legacy);

        assertEquals(2400, manager.getBest("Alice"));
        assertEquals(0, manager.getBest("Bob"));
        assertTrue(Files.exists(legacy));
    }

    @Test
    void failedMigrationKeepsLegacyFileAndLoadedScores() throws IOException {
        Path store = tempDir.resolve("data/net.vetcafe.jtetris/scores.properties");
        Path legacy = tempDir.resolve(".tetris_scores.properties");
        Files.writeString(legacy, "alice=1200\n");

        ScoreManager manager = new ScoreManager(store, legacy, new FailingSavePersistence());

        assertEquals(1200, manager.getBest("Alice"));
        assertFalse(Files.exists(store));
        assertTrue(Files.exists(legacy));
    }

    @Test
    void unreadableLegacyStoreIsNotDeletedOrReplaced() throws IOException {
        Path store = tempDir.resolve("data/net.vetcafe.jtetris/scores.properties");
        Path legacy = tempDir.resolve(".tetris_scores.properties");
        Files.writeString(legacy, "alice=1200\n");
        ScoreManager.Persistence unreadable = new FailingSavePersistence() {
            @Override
            public Properties load(Path path) {
                return null;
            }

            @Override
            public boolean save(Path path, Properties properties) {
                throw new AssertionError("Unreadable legacy data must not be saved as an empty store");
            }
        };

        ScoreManager manager = new ScoreManager(store, legacy, unreadable);

        assertTrue(manager.getLeaderboard().isEmpty());
        assertFalse(Files.exists(store));
        assertTrue(Files.exists(legacy));
    }

    @Test
    void deletesExistingPlayerCaseInsensitivelyAndPersistsRemoval() {
        Path store = tempDir.resolve("data/net.vetcafe.jtetris/scores.properties");
        Path legacy = tempDir.resolve(".tetris_scores.properties");
        ScoreManager manager = new ScoreManager(store, legacy);
        manager.updateIfHigher("Alice", 1200);

        assertTrue(manager.deleteUser("aLiCe"));
        assertEquals(0, manager.getBest("Alice"));
        assertTrue(manager.getUsers().isEmpty());

        ScoreManager reloaded = new ScoreManager(store, legacy);
        assertTrue(reloaded.getLeaderboard().isEmpty());
    }

    @Test
    void deletingMissingPlayerDoesNotChangeStoredScores() {
        Path store = tempDir.resolve("data/net.vetcafe.jtetris/scores.properties");
        Path legacy = tempDir.resolve(".tetris_scores.properties");
        ScoreManager manager = new ScoreManager(store, legacy);
        manager.updateIfHigher("Alice", 1200);

        assertFalse(manager.deleteUser("Bob"));
        assertEquals(1200, manager.getBest("Alice"));
    }

    @Test
    void failedDeleteSaveRestoresInMemoryPlayer() throws IOException {
        Path store = tempDir.resolve("data/net.vetcafe.jtetris/scores.properties");
        Path legacy = tempDir.resolve(".tetris_scores.properties");
        Files.createDirectories(store.getParent());
        Files.writeString(store, "alice=1200\n");
        ScoreManager manager = new ScoreManager(store, legacy, new FailingSavePersistence());

        assertFalse(manager.deleteUser("Alice"));
        assertEquals(1200, manager.getBest("Alice"));
        assertEquals(1, manager.getLeaderboard().size());
        assertEquals("alice", manager.getLeaderboard().get(0).user());
    }

    private Properties load(Path path) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(path)) {
            properties.load(input);
        }
        return properties;
    }

    private static class FailingSavePersistence implements ScoreManager.Persistence {
        private final ScoreManager.Persistence delegate = new ScoreManager.NioPersistence();

        @Override
        public boolean exists(Path path) {
            return delegate.exists(path);
        }

        @Override
        public Properties load(Path path) {
            return delegate.load(path);
        }

        @Override
        public boolean save(Path path, Properties properties) {
            return false;
        }

        @Override
        public boolean delete(Path path) {
            return delegate.delete(path);
        }
    }
}
