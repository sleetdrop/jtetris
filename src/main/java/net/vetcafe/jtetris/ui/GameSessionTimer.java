package net.vetcafe.jtetris.ui;

import java.util.Objects;
import java.util.function.LongSupplier;

final class GameSessionTimer {
    private static final long NANOS_PER_MILLI = 1_000_000L;

    private final LongSupplier nanoTime;
    private long accumulatedNanos;
    private long startedAtNanos;
    private long lastElapsedNanos;
    private boolean running;

    GameSessionTimer() {
        this(System::nanoTime);
    }

    GameSessionTimer(LongSupplier nanoTime) {
        this.nanoTime = Objects.requireNonNull(nanoTime, "nanoTime");
    }

    void start() {
        if (running) {
            return;
        }
        startedAtNanos = nanoTime.getAsLong();
        running = true;
    }

    void pause() {
        if (!running) {
            return;
        }
        accumulatedNanos = currentElapsedNanos();
        lastElapsedNanos = accumulatedNanos;
        running = false;
    }

    void resetAndStart() {
        accumulatedNanos = 0;
        lastElapsedNanos = 0;
        startedAtNanos = nanoTime.getAsLong();
        running = true;
    }

    void syncRunning(boolean shouldRun) {
        if (shouldRun) {
            start();
        } else {
            pause();
        }
    }

    long elapsedMillis() {
        long elapsedNanos = currentElapsedNanos();
        lastElapsedNanos = elapsedNanos;
        return elapsedNanos / NANOS_PER_MILLI;
    }

    private long currentElapsedNanos() {
        if (!running) {
            return Math.max(accumulatedNanos, lastElapsedNanos);
        }
        long runningNanos = Math.max(0, nanoTime.getAsLong() - startedAtNanos);
        return Math.max(lastElapsedNanos, accumulatedNanos + runningNanos);
    }
}
