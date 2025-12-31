package tetris.score;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Properties;

/**
 * Simple per-user high score store backed by a local properties file under user.home.
 */
public class ScoreManager {
    private final Properties props = new Properties();
    private final File storeFile;
    private final LinkedHashMap<String, String> userNames = new LinkedHashMap<>(); // key(lowercase) -> original

    public ScoreManager() {
        String home = System.getProperty("user.home", ".");
        this.storeFile = new File(home, ".tetris_scores.properties");
        load();
    }

    public synchronized int getBest(String user) {
        String key = key(user);
        String val = props.getProperty(key);
        if (val == null) return 0;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public synchronized int updateIfHigher(String user, int score) {
        String k = key(user);
        userNames.putIfAbsent(k, user == null ? "" : user.trim());
        int best = getBest(user);
        int newBest = Math.max(score, best);
        // Always write to ensure the user persists even on a tie or first entry.
        props.setProperty(k, Integer.toString(newBest));
        save();
        return newBest;
    }

    public synchronized List<String> getUsers() {
        List<String> users = new ArrayList<>(userNames.values());
        users.removeIf(String::isBlank);
        Collections.sort(users, String.CASE_INSENSITIVE_ORDER);
        return users;
    }

    public static record ScoreEntry(String user, int score) {}

    public synchronized java.util.List<ScoreEntry> getLeaderboard() {
        java.util.List<ScoreEntry> list = new java.util.ArrayList<>();
        for (String key : props.stringPropertyNames()) {
            int score = 0;
            try {
                score = Integer.parseInt(props.getProperty(key, "0"));
            } catch (NumberFormatException ignored) { }
            String name = userNames.getOrDefault(key, key);
            list.add(new ScoreEntry(name, score));
        }
        list.sort(Comparator.comparingInt(ScoreEntry::score).reversed().thenComparing(ScoreEntry::user, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    private void load() {
        if (!storeFile.exists()) return;
        try (FileInputStream in = new FileInputStream(storeFile)) {
            props.load(in);
            userNames.clear();
            props.stringPropertyNames().forEach(k -> userNames.put(k, k));
        } catch (IOException ignored) {
            // ignore corrupt file; treat as empty
        }
    }

    private void save() {
        try (FileOutputStream out = new FileOutputStream(storeFile)) {
            props.store(out, "Tetris local scores");
        } catch (IOException ignored) {
            // best-effort; ignore write failures
        }
    }

    private String key(String user) {
        return user == null ? "" : user.trim().toLowerCase();
    }
}
