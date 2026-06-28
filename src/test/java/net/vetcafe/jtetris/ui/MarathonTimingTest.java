package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MarathonTimingTest {

    @Test
    void gravityDelayGetsShorterAsLevelIncreases() {
        assertEquals(700, MarathonTiming.gravityDelayMs(1));
        assertTrue(MarathonTiming.gravityDelayMs(5) < MarathonTiming.gravityDelayMs(1));
        assertTrue(MarathonTiming.gravityDelayMs(10) < MarathonTiming.gravityDelayMs(5));
    }

    @Test
    void gravityDelayHasPlayableFloorForHighLevels() {
        assertEquals(50, MarathonTiming.gravityDelayMs(20));
        assertEquals(50, MarathonTiming.gravityDelayMs(99));
    }

    @Test
    void lockDelayStaysIndependentFromGravityLevel() {
        assertEquals(500, MarathonTiming.lockDelayMs());
        assertTrue(MarathonTiming.lockDelayMs() > MarathonTiming.gravityDelayMs(20));
    }
}
