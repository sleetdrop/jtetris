package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class GameSessionTimerTest {

    @Test
    void accumulatesOnlyWhileRunning() {
        MutableNanoClock clock = new MutableNanoClock();
        GameSessionTimer timer = new GameSessionTimer(clock::read);

        timer.start();
        clock.advanceMillis(1_500);
        assertEquals(1_500, timer.elapsedMillis());

        timer.pause();
        clock.advanceMillis(2_000);
        assertEquals(1_500, timer.elapsedMillis());

        timer.start();
        clock.advanceMillis(500);
        assertEquals(2_000, timer.elapsedMillis());
    }

    @Test
    void resetAndStartClearsPreviousRun() {
        MutableNanoClock clock = new MutableNanoClock();
        GameSessionTimer timer = new GameSessionTimer(clock::read);
        timer.start();
        clock.advanceMillis(4_000);

        timer.resetAndStart();
        assertEquals(0, timer.elapsedMillis());
        clock.advanceMillis(750);

        assertEquals(750, timer.elapsedMillis());
    }

    @Test
    void repeatedStateChangesAreIdempotent() {
        MutableNanoClock clock = new MutableNanoClock();
        GameSessionTimer timer = new GameSessionTimer(clock::read);

        timer.start();
        timer.start();
        clock.advanceMillis(300);
        timer.pause();
        timer.pause();
        clock.advanceMillis(700);

        assertEquals(300, timer.elapsedMillis());
    }

    @Test
    void backwardClockMovementNeverReducesElapsedTime() {
        MutableNanoClock clock = new MutableNanoClock();
        GameSessionTimer timer = new GameSessionTimer(clock::read);
        timer.start();
        clock.advanceMillis(900);
        assertEquals(900, timer.elapsedMillis());

        clock.advanceMillis(-400);

        assertEquals(900, timer.elapsedMillis());
        timer.pause();
        assertEquals(900, timer.elapsedMillis());
    }

    private static final class MutableNanoClock {
        private final AtomicLong nanos = new AtomicLong();

        long read() {
            return nanos.get();
        }

        void advanceMillis(long millis) {
            nanos.addAndGet(millis * 1_000_000L);
        }
    }
}
