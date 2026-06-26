package net.vetcafe.jtetris.ui;

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
    void delayedPollEmitsOneStepAndRebasesArrDeadline() {
        InputRepeater repeater = new InputRepeater(120, 40);

        assertEquals(-1, repeater.pressLeft(0));
        assertEquals(-1, repeater.poll(240));
        assertEquals(0, repeater.poll(279));
        assertEquals(-1, repeater.poll(280));
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
    void equalTimestampOppositePressUsesEventOrder() {
        InputRepeater repeater = new InputRepeater(120, 40);

        assertEquals(-1, repeater.pressLeft(50));
        assertEquals(1, repeater.pressRight(50));
        assertEquals(0, repeater.poll(169));
        assertEquals(1, repeater.poll(170));
    }

    @Test
    void duplicatePressDoesNotMoveOrStealPriority() {
        InputRepeater repeater = new InputRepeater(120, 40);

        assertEquals(-1, repeater.pressLeft(0));
        assertEquals(1, repeater.pressRight(10));
        assertEquals(0, repeater.pressLeft(20));
        assertEquals(0, repeater.poll(129));
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
