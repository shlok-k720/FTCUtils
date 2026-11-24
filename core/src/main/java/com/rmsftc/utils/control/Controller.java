package com.rmsftc.utils.control;


import com.rmsftc.utils.math.MathOperand;

import java.util.function.DoubleSupplier;

@FunctionalInterface
public interface Controller extends MathOperand<Controller> {
    double evaluate(double setpoint, double input);
    default double getError(double setpoint, double input) {
        return setpoint - input;
    }

    default void reset() {}

    Controller zero = constant(0.0);
    Controller setpoint = (setpoint, input) -> setpoint;
    Controller error = proportional(1.0);
    Controller negError = proportional(-1.0);

    static Controller constant(double constant) {
        return (setpoint, input) -> constant;
    }

    static Controller constant(DoubleSupplier constant) {
        return (setpoint, input) -> constant.getAsDouble();
    }

    static Controller proportional(DoubleSupplier kP) {
        return (setpoint, input) -> kP.getAsDouble() * (setpoint - input);
    }

    static Controller proportional(double kP) {
        return proportional(() -> kP);
    }

    @Override
    default Controller add(Controller other) {
        return (setpoint, input) -> this.evaluate(setpoint, input) + other.evaluate(setpoint, input);
    }

    @Override
    default Controller subtract(Controller other) {
        return (setpoint, input) -> this.evaluate(setpoint, input) - other.evaluate(setpoint, input);
    }

    @Override
    default Controller multiply(Controller other) {
        return (setpoint, input) -> this.evaluate(setpoint, input) * other.evaluate(setpoint, input);
    }

    @Override
    default Controller divide(Controller other) {
        return (setpoint, input) -> this.evaluate(setpoint, input) / other.evaluate(setpoint, input);
    }

    @Override
    default Controller remainder(Controller other) {
        return (setpoint, input) -> this.evaluate(setpoint, input) % other.evaluate(setpoint, input);
    }

    default Controller negate() {
        return (setpoint, input) -> -this.evaluate(setpoint, input);
    }

    default Controller abs() {
        return (setpoint, input) -> Math.abs(this.evaluate(setpoint, input));
    }

    default Controller times(double scalar) {
        return (setpoint, input) -> scalar * this.evaluate(setpoint, input);
    }

    static Controller sum(Controller... controllers) {
        return (setpoint, input) -> {
            double sum = 0.0;
            for (Controller controller : controllers) {
                sum += controller.evaluate(setpoint, input);
            }
            return sum;
        };
    }

    static Controller multiply(Controller... controllers) {
        return (setpoint, input) -> {
            double product = 1.0;
            for (Controller controller : controllers) {
                product *= controller.evaluate(setpoint, input);
            }
            return product;
        };
    }

    // ----------------------- Timed Controllers -----------------------

    abstract class TimedController implements Controller {
        double previousTime;
        double dt;

        public double findDt(double target, double error) {
            if (previousTime == 0) {
                previousTime = System.nanoTime();
                return 0;
            }
            // One capture: reading nanoTime() twice (once for dt, once for
            // previousTime) measured a shorter interval than the real one.
            long now = System.nanoTime();
            dt = (now - previousTime) * 1e-9;
            previousTime = now;
            return 0;
        }

        @Override
        public void reset() {
            previousTime = 0;
            dt = 0;
        }
    }

    static Controller integral(
            DoubleSupplier kI, DoubleSupplier iZone, DoubleSupplier decay, DoubleSupplier maxI) {
        return new TimedController() {
            double integral = 0;

            @Override
            public double evaluate(double setpoint, double input) {
                // The interface contract is evaluate(setpoint, input): the
                // integral accumulates the ERROR (setpoint - input), not the
                // raw input. Integrating the input meant the I-term never
                // grew with a zero input, so it could not correct
                // steady-state error.
                double error = setpoint - input;
                findDt(setpoint, input);

                if (Math.abs(error) < iZone.getAsDouble()) {
                    integral *= decay.getAsDouble();
                    integral += error * dt;
                    double cap = maxI.getAsDouble();
                    integral = Math.max(-cap, Math.min(cap, integral));
                }
                return kI.getAsDouble() * integral;
            }

            @Override
            public void reset() {
                super.reset();
                integral = 0;
            }
        };
    }

    static Controller integral(double kI, double iZone, double decay, double maxI) {
        return integral(() -> kI, () -> iZone, () -> decay, () -> maxI);
    }

    static Controller integral(DoubleSupplier kI) {
        return integral(kI, () -> Double.POSITIVE_INFINITY, () -> 1.0, () -> Double.POSITIVE_INFINITY);
    }
    static Controller integral(double kI) {
        return integral(() -> kI);
    }

    static Controller derivative(DoubleSupplier kD) {
        return new TimedController() {
            double prevError = 0;
            boolean firstUpdate = true;

            @Override
            public double evaluate(double setpoint, double input) {
                // Differentiate the ERROR, per the interface contract.
                // Differentiating the raw input instead made the D-term
                // respond to measurement changes with the wrong sign.
                double error = setpoint - input;
                findDt(setpoint, input);

                if (dt < 1e-3 || firstUpdate) {
                    firstUpdate = false;
                    prevError = error;
                    return 0;
                }
                double output = kD.getAsDouble() * (error - prevError) / dt;
                prevError = error;
                return output;
            }

            @Override
            public void reset() {
                super.reset();
                prevError = 0;
                firstUpdate = true;
            }
        };
    }

    static Controller derivative(double kD) {
        return derivative(() -> kD);
    }

    static Controller pid(DoubleSupplier p, DoubleSupplier i, DoubleSupplier d) {
        return sum(proportional(p), integral(i), derivative(d));
    }

    static Controller pid(double p, double i, double d) {
        return pid(() -> p, () -> i, () -> d);
    }

    static Controller pidf(DoubleSupplier p, DoubleSupplier i, DoubleSupplier d, DoubleSupplier f) {
        return sum(proportional(p), integral(i), derivative(d), constant(f));
    }

    static Controller pidf(double p, double i, double d, double f) {
        return pidf(() -> p, () -> i, () -> d, () -> f);
    }


    // ----------------------- Controller Composition -----------------------

    static PiecewiseController piecewise(Controller base) {
        if (base instanceof PiecewiseController) return (PiecewiseController) base;
        return new PiecewiseController(base);
    }

    default PiecewiseController piecewise() {
        return piecewise(this);
    }

    default PiecewiseController with(Controller other, double threshold) {
        return piecewise().put(threshold, other);
    }
}