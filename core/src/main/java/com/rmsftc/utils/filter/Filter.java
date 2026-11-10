package com.rmsftc.utils.filter;

import com.rmsftc.utils.math.MathOperand;

@FunctionalInterface
public interface Filter extends MathOperand<Filter> {
    double update(double newInput, double covariance, double oldOutput);


    static Filter lowPassFilter(double alpha) {
        return (newInput, covariance, oldOutput) -> alpha * newInput + (1 - alpha) * oldOutput;
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
