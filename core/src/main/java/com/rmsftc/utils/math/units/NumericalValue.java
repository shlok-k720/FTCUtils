package com.rmsftc.utils.math.units;

import com.rmsftc.utils.math.MathOperand;

import java.util.Objects;

/**
 * A numeric value tagged with a {@link NumericalUnit}, e.g. {@code 3.5 m} or
 * {@code 90 deg}. Arithmetic converts the other operand into this value's
 * unit first, so {@code 1 m + 50 cm} is {@code 1.5 m}.
 */
public class NumericalValue<U extends NumericalUnit<U>> implements MathOperand<NumericalValue<U>> {
    double value;
    U unit;

    public NumericalValue(double value, U unit) {
        this.value = value;
        this.unit = unit;
    }

    /** The raw numeric amount, in {@link #getUnit()}. */
    public double getValue() {
        return value;
    }

    /** The unit this value is expressed in. */
    public U getUnit() {
        return unit;
    }

    /** This value converted into {@code targetUnit}. */
    public NumericalValue<U> convertTo(NumericalUnit<U> targetUnit) {
        return unit.convertTo(this, targetUnit);
    }

    @Override
    public NumericalValue<U> add(NumericalValue<U> other) {
        return of(value + other.unit.convertTo(other, unit).getValue(), unit);
    }

    @Override
    public NumericalValue<U> subtract(NumericalValue<U> other) {
        return of(value - other.unit.convertTo(other, unit).getValue(), unit);
    }

    @Override
    public NumericalValue<U> multiply(NumericalValue<U> other) {
        return of(value * other.unit.convertTo(other, unit).getValue(), unit);
    }

    @Override
    public NumericalValue<U> divide(NumericalValue<U> other) {
        return of(value / other.unit.convertTo(other, unit).getValue(), unit);
    }

    @Override
    public NumericalValue<U> remainder(NumericalValue<U> other) {
        return of(value % other.unit.convertTo(other, unit).getValue(), unit);
    }

    /** This value scaled by {@code scalar}, keeping the unit. */
    public NumericalValue<U> multiply(double scalar) {
        return of(value * scalar, unit);
    }

    /** This value divided by {@code scalar}, keeping the unit. */
    public NumericalValue<U> divide(double scalar) {
        return of(value / scalar, unit);
    }

    public static <U extends NumericalUnit<U>> NumericalValue<U> of(double value, U unit) {
        return new NumericalValue<>(value, unit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumericalValue)) return false;
        NumericalValue<?> that = (NumericalValue<?>) o;
        return Double.compare(value, that.value) == 0 && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }

    @Override
    public String toString() {
        return value + " " + unit;
    }
}
