package net.vetcafe.jtetris.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class EdtWatchdogTest {

    @Test
    void timelyAcknowledgementDoesNotWarn() {
        FakeClock clock = new FakeClock();
        List<Runnable> dispatched = new ArrayList<>();
        List<Long> warnings = new ArrayList<>();
        EdtWatchdog watchdog = new EdtWatchdog(500, clock::now, dispatched::add, (delay, stack) -> warnings.add(delay));

        watchdog.probe();
        clock.advance(100);
        dispatched.remove(0).run();
        watchdog.probe();

        assertTrue(warnings.isEmpty());
    }

    @Test
    void delayedAcknowledgementWarnsOnceUntilRecovery() {
        FakeClock clock = new FakeClock();
        List<Runnable> dispatched = new ArrayList<>();
        List<Long> warnings = new ArrayList<>();
        EdtWatchdog watchdog = new EdtWatchdog(500, clock::now, dispatched::add, (delay, stack) -> warnings.add(delay));

        watchdog.probe();
        clock.advance(600);
        watchdog.probe();
        clock.advance(600);
        watchdog.probe();

        assertEquals(List.of(600L), warnings);

        dispatched.remove(0).run();
        watchdog.probe();
        clock.advance(600);
        watchdog.probe();

        assertEquals(List.of(600L, 600L), warnings);
    }

    @Test
    void closeStopsFurtherProbes() {
        FakeClock clock = new FakeClock();
        List<Runnable> dispatched = new ArrayList<>();
        EdtWatchdog watchdog = new EdtWatchdog(500, clock::now, dispatched::add, (delay, stack) -> {});

        watchdog.close();
        watchdog.probe();

        assertTrue(dispatched.isEmpty());
        assertFalse(watchdog.isRunning());
    }

    private static final class FakeClock {
        private long now;

        long now() {
            return now;
        }

        void advance(long millis) {
            now += millis;
        }
    }
}
