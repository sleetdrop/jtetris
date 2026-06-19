package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameSessionLifecycleTest {

    @Test
    void sessionRunsOnlyDuringUnblockedActivePlay() {
        assertTrue(TetrisFrame.shouldRunSessionTimer(false, false, false));
        assertFalse(TetrisFrame.shouldRunSessionTimer(true, false, false));
        assertFalse(TetrisFrame.shouldRunSessionTimer(false, true, false));
        assertFalse(TetrisFrame.shouldRunSessionTimer(false, false, true));
    }
}
