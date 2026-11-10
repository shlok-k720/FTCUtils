package com.rmsftc.utils.filter;

import com.rmsftc.utils.math.MathOperand;

import java.util.ArrayDeque;
import java.util.Deque;

@FunctionalInterface
public interface Filter extends MathOperand<Filter> {
    double update(double newInput, double covariance, double oldOutput);


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
