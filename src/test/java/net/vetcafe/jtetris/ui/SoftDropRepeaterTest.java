package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SoftDropRepeaterTest {

    @Test
    void pressTriggersImmediateSoftDropStep() {
        SoftDropRepeater repeater = new SoftDropRepeater(40);
        assertEquals(1, repeater.press(100));
        assertEquals(0, repeater.poll(139));
        assertEquals(1, repeater.poll(140));
    }

    @Test
    void delayedPollEmitsOneStepAndRebasesDeadline() {
        SoftDropRepeater repeater = new SoftDropRepeater(40);
        repeater.press(0);
        assertEquals(1, repeater.poll(120));
        assertEquals(0, repeater.poll(159));
        assertEquals(1, repeater.poll(160));
    }

    @Test
    void duplicatePressDoesNotEmitAnotherImmediateStep() {
        SoftDropRepeater repeater = new SoftDropRepeater(40);
        assertEquals(1, repeater.press(0));
        assertEquals(0, repeater.press(10));
    }

    @Test
    void releaseStopsRepeats() {
        SoftDropRepeater repeater = new SoftDropRepeater(40);
        repeater.press(0);
        repeater.release();
        assertEquals(0, repeater.poll(1000));
    }

    @Test
    void resetClearsHeldState() {
        SoftDropRepeater repeater = new SoftDropRepeater(40);
        repeater.press(0);
        repeater.reset();
        assertEquals(0, repeater.poll(100));
        assertEquals(1, repeater.press(101));
    }
}
