package net.vetcafe.jtetris.logging;

import java.util.Locale;
import java.util.Optional;

enum LogLevel {
    ERROR,
    WARN,
    INFO,
    DEBUG,
    TRACE;

    static Optional<LogLevel> parse(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(value.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}
