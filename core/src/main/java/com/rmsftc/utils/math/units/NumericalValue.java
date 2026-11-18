package com.rmsftc.utils.math.units;

import com.rmsftc.utils.math.MathOperand;

public class NumericalValue<U extends NumericalUnit> implements MathOperand<NumericalValue<U>> {
    double value;
    U unit;

    public NumericalValue(double value, U unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public NumericalValue<U> add(NumericalValue<U> other) {
        return of(other.unit.convertTo(other, unit).value + value, unit);
    }

    @Override
    public NumericalValue<U> subtract(NumericalValue<U> other) {
        return of(other.unit.convertTo(other, unit).value - value, unit);
    }

    @Override
    public NumericalValue<U> multiply(NumericalValue<U> other) {
        return of(other.unit.convertTo(other, unit).value * value, unit);
    }

    @Override
    public NumericalValue<U> divide(NumericalValue<U> other) {
        return of(other.unit.convertTo(other, unit).value / value, unit);
    }

    @Override
    public NumericalValue<U> remainder(NumericalValue<U> other) {
        return of(other.unit.convertTo(other, unit).value % value, unit);
    }

    public static <U extends NumericalUnit> NumericalValue<U> of(double value, U unit) {
        return new NumericalValue<>(value, unit);
    }
}
