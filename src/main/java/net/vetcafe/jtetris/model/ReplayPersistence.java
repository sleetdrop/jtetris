package net.vetcafe.jtetris.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Text-based persistence for seeded replay payloads.
 */
public final class ReplayPersistence {
    private static final String HEADER = "JTETRIS_REPLAY_V1";
    private static final String SEED_PREFIX = "seed=";
    private static final String ACTIONS_PREFIX = "actions=";

    private ReplayPersistence() {
    }

    public static void save(Path path, long seed, List<ReplayAction> actions) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }
        if (actions == null) {
            throw new IllegalArgumentException("actions must not be null");
        }

        String serializedActions = String.join(",", actions.stream().map(Enum::name).toList());
        String content = String.join(
                System.lineSeparator(),
                HEADER,
                SEED_PREFIX + seed,
                ACTIONS_PREFIX + serializedActions,
                ""
        );

        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    public static LoadedReplay load(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.size() < 3) {
            throw new IOException("Invalid replay file: expected header, seed and actions lines");
        }
        if (!HEADER.equals(lines.get(0).trim())) {
            throw new IOException("Invalid replay file: unsupported format header");
        }

        String seedLine = lines.get(1).trim();
        if (!seedLine.startsWith(SEED_PREFIX)) {
            throw new IOException("Invalid replay file: missing seed line");
        }

        long seed;
        try {
            seed = Long.parseLong(seedLine.substring(SEED_PREFIX.length()));
        } catch (NumberFormatException e) {
            throw new IOException("Invalid replay file: seed is not a valid long", e);
        }

        String actionsLine = lines.get(2).trim();
        if (!actionsLine.startsWith(ACTIONS_PREFIX)) {
            throw new IOException("Invalid replay file: missing actions line");
        }

        String payload = actionsLine.substring(ACTIONS_PREFIX.length()).trim();
        if (payload.isEmpty()) {
            return new LoadedReplay(seed, List.of());
        }

        String[] tokens = payload.split(",");
        List<ReplayAction> actions = new ArrayList<>(tokens.length);
        for (String token : tokens) {
            try {
                actions.add(ReplayAction.valueOf(token.trim()));
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid replay file: unknown action '" + token + "'", e);
            }
        }
        return new LoadedReplay(seed, Collections.unmodifiableList(actions));
    }

    public record LoadedReplay(long seed, List<ReplayAction> actions) {
    }
}

