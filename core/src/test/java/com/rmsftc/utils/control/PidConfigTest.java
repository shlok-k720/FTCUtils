package com.rmsftc.utils.control;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void integralAccumulatesErrorNotInput() {
        // Distinguishing case: input 0, setpoint 3 (error 3). Integrating
        // the input instead of the error meant the I-term never grew here.
        Controller integral = Controller.integral(1.0);
        integral.evaluate(3.0, 0.0); // first call: dt is 0
        double output = integral.evaluate(3.0, 0.0);
        assertTrue(output > 0.0, "I-term must grow with a nonzero error, even at zero input");
    }

    @Test
    void integralIsZeroAtZeroError() {
        Controller integral = Controller.integral(1.0);
        integral.evaluate(2.0, 2.0);
        assertEquals(0.0, integral.evaluate(2.0, 2.0));
    }

    @Test
    void derivativeDifferentiatesErrorNotInput() throws InterruptedException {
        // Error goes 2 -> 1 while the input rises 1 -> 2, so d(error)/dt < 0.
        // (Sleep past the 1 ms dt gate so the second call computes.)
        Controller derivative = Controller.derivative(1.0);
        derivative.evaluate(3.0, 1.0);
        Thread.sleep(2);
        double output = derivative.evaluate(3.0, 2.0);
        assertTrue(output < 0.0, "D-term must follow the error, not the input");
    }

    @Test
    void piecewiseSwitchesOnErrorMagnitude() {
        Controller piecewise = Controller.piecewise(Controller.constant(1.0))
                .put(2.0, Controller.constant(10.0));
        // Error 4 >= 2 -> aggressive branch, even though |input| is 1.
        assertEquals(10.0, piecewise.evaluate(5.0, 1.0));
        // Error 1 < 2 -> baseline branch.
        assertEquals(1.0, piecewise.evaluate(5.0, 4.0));
    }
}
