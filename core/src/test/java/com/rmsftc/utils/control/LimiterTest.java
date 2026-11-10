package com.rmsftc.utils.control;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LimiterTest {

    @Test
    void rateLimitsTheStepTowardTheTarget() {
        Limiter limiter = new LimiterConfig(c -> c.maxRate.set(1.0)).limiter();
        assertEquals(0.1, limiter.limit(1.0, 0.1));
        assertEquals(0.2, limiter.limit(1.0, 0.1));
        assertEquals(0.1, limiter.limit(0.0, 0.1));
    }

    @Test
    void clampsToTheOutputRange() {
        Limiter limiter = new LimiterConfig().limiter();
        assertEquals(1.0, limiter.limit(2.0, 1.0));
        assertEquals(-1.0, limiter.limit(-2.0, 10.0));
    }

    @Test
    void resetRestoresTheOutput() {
        Limiter limiter = new LimiterConfig().limiter();
        limiter.limit(1.0, 1.0);
        assertEquals(1.0, limiter.get());
        limiter.reset();
        assertEquals(0.0, limiter.get());
        limiter.reset(0.5);
        assertEquals(0.5, limiter.get());
    }

    @Test
    void retuningTheConfigRetunesTheLimiter() {
        LimiterConfig config = new LimiterConfig(c -> c.maxRate.set(1.0));
        Limiter limiter = config.limiter();
        assertEquals(0.1, limiter.limit(1.0, 0.1));
        config.maxRate.set(10.0);
        assertEquals(1.0, limiter.limit(1.0, 0.1));
    }
}
