package net.vetcafe.jtetris.score;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

/**
 * Simple per-user high score store backed by a platform-local properties file.
 */
public class ScoreManager {
    interface Persistence {
        boolean exists(Path path);

        Properties load(Path path);

        boolean save(Path path, Properties properties);

        boolean delete(Path path);
    }

    static final class NioPersistence implements Persistence {
        @Override
        public boolean exists(Path path) {
            return Files.exists(path);
        }

        @Override
        public Properties load(Path path) {
            Properties loaded = new Properties();
            try (InputStream input = Files.newInputStream(path)) {
                loaded.load(input);
            } catch (IOException | IllegalArgumentException ignored) {
                return null;
            }
            return loaded;
        }

        @Override
        public boolean save(Path path, Properties properties) {
            Path parent = path.toAbsolutePath().getParent();
            Path temporary = null;
            try {
                if (parent != null) {
                    Files.createDirectories(parent);
                    temporary = Files.createTempFile(parent, "jtetris-scores-", ".tmp");
                } else {
                    temporary = Files.createTempFile("jtetris-scores-", ".tmp");
                }
                try (OutputStream output = Files.newOutputStream(temporary)) {
                    properties.store(output, "JTetris local scores");
                }
                try {
                    Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException ignored) {
                    Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
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

        @Override
        public boolean delete(Path path) {
            try {
                return Files.deleteIfExists(path);
            } catch (IOException ignored) {
                return false;
            }
        }
    }

    private final Properties props = new Properties();
    private final Path storeFile;
    private final Path legacyFile;
    private final Persistence persistence;
    private final LinkedHashMap<String, String> userNames = new LinkedHashMap<>();

    public ScoreManager() {
        this(ScoreDataPaths.current(), ScoreDataPaths.legacy(Path.of(System.getProperty("user.home", "."))));
    }

    ScoreManager(Path storeFile, Path legacyFile) {
        this(storeFile, legacyFile, new NioPersistence());
    }

    ScoreManager(Path storeFile, Path legacyFile, Persistence persistence) {
        this.storeFile = storeFile;
        this.legacyFile = legacyFile;
        this.persistence = persistence;
        load();
    }

    public synchronized int getBest(String user) {
        String key = key(user);
        String val = props.getProperty(key);
        if (val == null) {
            return 0;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public synchronized int updateIfHigher(String user, int score) {
        String normalized = key(user);
        userNames.putIfAbsent(normalized, user == null ? "" : user.trim());
        int best = getBest(user);
        int newBest = Math.max(score, best);
        props.setProperty(normalized, Integer.toString(newBest));
        save();
        return newBest;
    }

    public synchronized List<String> getUsers() {
        List<String> users = new ArrayList<>(userNames.values());
        users.removeIf(String::isBlank);
        Collections.sort(users, String.CASE_INSENSITIVE_ORDER);
        return users;
    }

    public synchronized boolean deleteUser(String user) {
        String normalized = key(user);
        if (!props.containsKey(normalized)) {
            return false;
        }

        String previousScore = props.getProperty(normalized);
        String previousName = userNames.get(normalized);
        props.remove(normalized);
        userNames.remove(normalized);
        if (save()) {
            return true;
        }

        props.setProperty(normalized, previousScore);
        if (previousName != null) {
            userNames.put(normalized, previousName);
        }
        return false;
    }

    public static record ScoreEntry(String user, int score) {}

    public synchronized List<ScoreEntry> getLeaderboard() {
        List<ScoreEntry> list = new ArrayList<>();
        for (String propertyKey : props.stringPropertyNames()) {
            int score = 0;
            try {
                score = Integer.parseInt(props.getProperty(propertyKey, "0"));
            } catch (NumberFormatException ignored) {
                // Preserve the existing behavior of treating malformed scores as zero.
            }
            String name = userNames.getOrDefault(propertyKey, propertyKey);
            list.add(new ScoreEntry(name, score));
        }
        list.sort(Comparator.comparingInt(ScoreEntry::score)
                .reversed()
                .thenComparing(ScoreEntry::user, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    private void load() {
        if (persistence.exists(storeFile)) {
            Properties stored = persistence.load(storeFile);
            if (stored != null) {
                loadProperties(stored);
            }
            return;
        }
        if (!persistence.exists(legacyFile)) {
            return;
        }

        Properties legacy = persistence.load(legacyFile);
        if (legacy == null) {
            return;
        }
        loadProperties(legacy);
        if (save()) {
            persistence.delete(legacyFile);
        }
    }

    private void loadProperties(Properties loaded) {
        props.clear();
        props.putAll(loaded);
        userNames.clear();
        props.stringPropertyNames().forEach(propertyKey -> userNames.put(propertyKey, propertyKey));
    }

    private boolean save() {
        Properties snapshot = new Properties();
        snapshot.putAll(props);
        return persistence.save(storeFile, snapshot);
    }

    private String key(String user) {
        return user == null ? "" : user.trim().toLowerCase();
    }
}
