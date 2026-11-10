package com.rmsftc.utils.control;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LimiterConfigTest {

    @Test
    void defaultsAreUnlimitedRateAndMotorPowerRange() {
        LimiterConfig config = new LimiterConfig();
        assertEquals(Double.POSITIVE_INFINITY, config.maxRate.get());
        assertEquals(-1.0, config.minOutput.get());
        assertEquals(1.0, config.maxOutput.get());
    }

    @Test
    void configuratorOverridesDefaults() {
        LimiterConfig config = new LimiterConfig(c -> {
            c.maxRate.set(2.0);
            c.minOutput.set(-0.5);
            c.maxOutput.set(0.8);
        });
        assertEquals(2.0, config.maxRate.get());
        assertEquals(-0.5, config.minOutput.get());
        assertEquals(0.8, config.maxOutput.get());
    }

    @Test
    void limiterFactoryBuildsAWorkingLimiter() {
        Limiter limiter = new LimiterConfig(c -> c.maxRate.set(1.0)).limiter();
        assertEquals(0.1, limiter.limit(1.0, 0.1));
    }
}
