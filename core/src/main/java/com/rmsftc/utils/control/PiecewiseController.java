package com.rmsftc.utils.control;

import java.util.NavigableMap;
import java.util.TreeMap;

public class PiecewiseController implements Controller {
    private final NavigableMap<Double, Controller> controllers;

    public PiecewiseController(Controller baseline) {
        this.controllers = new TreeMap<>();
        this.controllers.put(0.0, baseline);
    }

    /**
     * Adds a controller to the piecewise controller.
     * The controller will be used when the error (|setpoint - input|) is
     * greater than or equal to the threshold and less than the next threshold.
     *
     * @return this
     */
    public PiecewiseController put(double threshold, Controller controller) {
        controllers.put(Math.abs(threshold), controller);
        return this;
    }

    @Override
    public double evaluate(double setpoint, double input) {
        // Select on the ERROR magnitude (|setpoint - input|) and pass the
        // (setpoint, input) pair through unchanged, per the interface
        // contract. Selecting on |input| switched gains on the measurement
        // instead of on how far the system is from its target.
        double absError = Math.abs(setpoint - input);
        return controllers.floorEntry(absError).getValue().evaluate(setpoint, input);
    }
    @Override
    public void reset() {
        controllers.values().forEach(Controller::reset);
    }
}