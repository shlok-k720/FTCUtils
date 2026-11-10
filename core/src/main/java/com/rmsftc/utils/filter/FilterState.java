package com.rmsftc.utils.filter;

/**
 * A stateful wrapper around a {@link Filter}: it holds the previous output
 * (and the covariance) so callers don't have to carry them between loops.
 *
 * <pre>
 * FilterState speed = new FilterState(Filter.lowPassFilter(0.2));
 * // in the loop:
 * double filtered = speed.update(rawSpeed);
 * </pre>
 */
public class FilterState {
    private final Filter filter;
    private double output;
    private double covariance;

    public FilterState(Filter filter) {
        this(filter, 0.0);
    }

    public FilterState(Filter filter, double initialOutput) {
        this.filter = filter;
        this.output = initialOutput;
        this.covariance = 0.0;
    }

    /** Filters {@code newInput} using the stored covariance and output. */
    public double update(double newInput) {
        return update(newInput, covariance);
    }

    /** Filters {@code newInput}, remembering the given covariance. */
    public double update(double newInput, double covariance) {
        this.covariance = covariance;
        output = filter.update(newInput, covariance, output);
        return output;
    }

    /** The most recent filtered output. */
    public double get() {
        return output;
    }

    /** The covariance passed to the most recent update. */
    public double getCovariance() {
        return covariance;
    }

    public void setCovariance(double covariance) {
        this.covariance = covariance;
    }

    /** Resets the output to 0. */
    public void reset() {
        reset(0.0);
    }

    /** Resets the output to {@code initialOutput}. */
    public void reset(double initialOutput) {
        this.output = initialOutput;
    }
}
