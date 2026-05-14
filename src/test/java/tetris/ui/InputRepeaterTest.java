package tetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InputRepeaterTest {

    @Test
    void pressStartsWithImmediateStepThenWaitsForDas() {
        InputRepeater repeater = new InputRepeater(120, 40);

        assertEquals(-1, repeater.pressLeft(0));
        assertEquals(0, repeater.poll(119));
        assertEquals(-1, repeater.poll(120));
        assertEquals(-1, repeater.poll(160));
    }

    @Test
    void latestPressedDirectionWinsWhenBothHeld() {
        InputRepeater repeater = new InputRepeater(120, 40);

        assertEquals(-1, repeater.pressLeft(0));
        assertEquals(1, repeater.pressRight(10));
        assertEquals(0, repeater.poll(100));
        assertEquals(1, repeater.poll(130));
    }

    @Test
    void releasingActiveDirectionSwitchesToOtherDirectionImmediately() {
        InputRepeater repeater = new InputRepeater(120, 40);

        assertEquals(-1, repeater.pressLeft(0));
        assertEquals(1, repeater.pressRight(10));
        assertEquals(-1, repeater.releaseRight(20));
        assertEquals(0, repeater.poll(100));
        assertEquals(-1, repeater.poll(140));
    }

    @Test
    void resetClearsState() {
        InputRepeater repeater = new InputRepeater(120, 40);
        repeater.pressLeft(0);
        repeater.reset();

        assertEquals(0, repeater.poll(1000));
        assertEquals(1, repeater.pressRight(1001));
    }
}

