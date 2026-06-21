package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class TetrisFrameInputTimingTest {

    @Test
    void defaultDasKeepsObservedTapSingleCellAndRepeatsAtDeadline() throws Exception {
        InputRepeater repeater = new InputRepeater(
                staticIntField("DAS_MS"),
                staticIntField("ARR_MS")
        );

        assertEquals(1, repeater.pressRight(0));
        assertEquals(0, repeater.poll(176));
        assertEquals(1, repeater.poll(180));
    }

    private static int staticIntField(String name) throws Exception {
        Field field = TetrisFrame.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(null);
    }
}
