package com.rmsftc.utils.config.vars;

import java.util.Objects;

/**
 * A single check on a value, used by {@link Validation} and enforced by
 * {@link Var#set(Object)}. Ported from the FTChimera team's vars system.
 */
@FunctionalInterface
public interface Validator<T> {
    boolean validate(T value);

    static Validator<Double> positive() {
        return v -> v > 0.0;
    }

    static Validator<Double> nonnegative() {
        return v -> v >= 0.0;
    }

    static Validator<Double> negative() {
        return v -> v < 0.0;
    }

    static Validator<Double> nonpositive() {
        return v -> v <= 0.0;
    }

    static <T> Validator<T> nonnull() {
        return Objects::nonNull;
    }

    static Validator<Double> inRange(double min, double max) {
        return v -> v >= min && v <= max;
    }

    static Validator<Double> nonZero() {
        return v -> v != 0.0;
    }
}
