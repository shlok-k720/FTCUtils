package com.rmsftc.utils;

import com.rmsftc.utils.math.units.NumericalValue;
import com.rmsftc.utils.math.units.basic.TimeUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClockTest {

    private static final double EPS = 1e-9;

    /** A clock the test drives by hand (seconds since "boot"). */
    private static class FakeClock implements Clock {
        double seconds = 0;

        @Override
        public NumericalValue<TimeUnit> get() {
            return new NumericalValue<>(seconds, TimeUnit.SECOND);
        }
    }

    @Test
    void systemClockReportsNanoseconds() {
        NumericalValue<TimeUnit> now = Clock.SYSTEM.now();
        assertEquals(TimeUnit.NANOSECOND, now.getUnit());
        assertTrue(Clock.SYSTEM.nowNanos() > 0);
        assertTrue(Clock.SYSTEM.nowSeconds() > 0);
    }

    @Test
    void conversionsAndLongSupplier() {
        FakeClock clock = new FakeClock();
        clock.seconds = 1.5;
        assertEquals(1_500_000_000L, clock.nowNanos());
        assertEquals(1.5, clock.nowSeconds(), EPS);
        assertEquals(1_500_000_000L, (long) clock.asLongSupplier().get());
    }

    @Test
    void helperTracksDt() {
        FakeClock clock = new FakeClock();
        Clock.Helper helper = new Clock.Helper(clock);
        helper.update();
        assertEquals(0.0, helper.dtSeconds(), EPS);
        clock.seconds = 0.25;
        helper.update();
        assertEquals(0.25, helper.dtSeconds(), EPS);
        assertEquals(250_000_000L, helper.dtNanos());
        clock.seconds = 0.75;
        helper.update();
        assertEquals(0.5, helper.dtSeconds(), EPS);
    }
}
