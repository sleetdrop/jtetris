package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ElapsedTimeFormatterTest {

    @Test
    void formatsMinutesAndSecondsBelowOneHour() {
        assertEquals("00:00", ElapsedTimeFormatter.format(0));
        assertEquals("00:01", ElapsedTimeFormatter.format(1_999));
        assertEquals("59:59", ElapsedTimeFormatter.format(3_599_000));
    }

    @Test
    void formatsHoursWithoutClippingMinutesOrSeconds() {
        assertEquals("1:00:00", ElapsedTimeFormatter.format(3_600_000));
        assertEquals("12:34:56", ElapsedTimeFormatter.format(45_296_000));
    }

    @Test
    void clampsNegativeElapsedTimeToZero() {
        assertEquals("00:00", ElapsedTimeFormatter.format(-1));
    }
}
