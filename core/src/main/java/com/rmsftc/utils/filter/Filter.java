package com.rmsftc.utils.filter;

import com.rmsftc.utils.math.MathOperand;

import java.util.ArrayDeque;
import java.util.Deque;

@FunctionalInterface
public interface Filter extends MathOperand<Filter> {
    /**
     * One filtering step for a scalar signal. Stateless filters compute the
     * new output from the given inputs; stateful filters (KalmanFilter,
     * ExtendedKalmanFilter) implement this as a 1-D convenience — a predict
     * step plus a correction with the scalar measurement, returning the
     * first state component — and ignore {@code covariance} and
     * {@code oldOutput}, because they keep their own covariance and state.
     */
    double update(double newInput, double covariance, double oldOutput);

    /**
     * Advances the filter's internal state one step (the predict step of a
     * Kalman-style filter). Default is a no-op: stateless filters have
     * nothing to predict. Stateful filters override it.
     */
    default void predict() {}

    /**
     * Corrects the filter with a measurement vector (the update step of a
     * Kalman-style filter). Default is a no-op. Stateful filters override it.
     */
    default void update(double[] measurement) {}

    /**
     * Resets the filter to its initial state. Default is a no-op.
     * Stateful filters override it.
     */
    default void reset() {}


    static Filter lowPassFilter(double alpha) {
        return (newInput, covariance, oldOutput) -> alpha * newInput + (1 - alpha) * oldOutput;
    }

    /**
     * A high-pass filter: passes fast changes, blocks steady values.
     * {@code alpha} in [0, 1] is how much of the previous output is kept;
     * closer to 1 means a slower cutoff. The filter remembers the previous
     * input, so each factory call returns an independent, stateful filter.
     */
    static Filter highPassFilter(double alpha) {
        if (alpha < 0.0 || alpha > 1.0) {
            throw new IllegalArgumentException("alpha must be in [0, 1]");
        }
        double[] previousInput = {0.0};
        return (newInput, covariance, oldOutput) -> {
            double output = alpha * (oldOutput + newInput - previousInput[0]);
            previousInput[0] = newInput;
            return output;
        };
    }

    /**
     * A moving-average filter over the last {@code windowSize} inputs.
     * {@code windowSize} must be at least 1. The filter remembers past
     * inputs, so each factory call returns an independent, stateful filter.
     */
    static Filter movingAverageFilter(int windowSize) {
        if (windowSize < 1) {
            throw new IllegalArgumentException("windowSize must be at least 1");
        }
        Deque<Double> window = new ArrayDeque<>();
        double[] sum = {0.0};
        return (newInput, covariance, oldOutput) -> {
            window.addLast(newInput);
            sum[0] += newInput;
            if (window.size() > windowSize) {
                sum[0] -= window.removeFirst();
            }
            return sum[0] / window.size();
        };
    }


    @Override
    default Filter add(Filter other) {
        return (newInput, covariance, oldOutput) -> this.update(newInput, covariance, oldOutput) + other.update(newInput, covariance, oldOutput);
    }

    @Override
    default Filter subtract(Filter other) {
        return (newInput, covariance, oldOutput) -> this.update(newInput, covariance, oldOutput) - other.update(newInput, covariance, oldOutput);
    }

    @Override
    default Filter multiply(Filter other) {
        return (newInput, covariance, oldOutput) -> this.update(newInput, covariance, oldOutput) * other.update(newInput, covariance, oldOutput);
    }

    @Override
    default Filter divide(Filter other) {
        return (newInput, covariance, oldOutput) -> this.update(newInput, covariance, oldOutput) / other.update(newInput, covariance, oldOutput);
    }

    @Override
    default Filter remainder(Filter other) {
        return (newInput, covariance, oldOutput) -> this.update(newInput, covariance, oldOutput) % other.update(newInput, covariance, oldOutput);
    }
}
