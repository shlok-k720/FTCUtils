package com.rmsftc.utils.control;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PidConfigTest {

    @Test
    void defaultsAreZeroGains() {
        PidConfig config = new PidConfig();
        assertEquals(0.0, config.p.get());
        assertEquals(0.0, config.i.get());
        assertEquals(0.0, config.d.get());
        assertEquals(0.0, config.f.get());
        assertEquals(0.0, config.controller().evaluate(3.0, 1.0));
    }

    @Test
    void configuratorSetsGains() {
        PidConfig config = new PidConfig(c -> {
            c.p.set(2.0);
            c.f.set(0.5);
        });
        // Proportional 2 * (3 - 1) = 4, feedforward 0.5, I and D are zero.
        assertEquals(4.5, config.controller().evaluate(3.0, 1.0));
    }

    @Test
    void controllerReadsGainsLive() {
        PidConfig config = new PidConfig(c -> c.p.set(2.0));
        Controller controller = config.controller();
        assertEquals(4.0, controller.evaluate(3.0, 1.0));
        config.p.set(3.0);
        assertEquals(6.0, controller.evaluate(3.0, 1.0));
    }
}
