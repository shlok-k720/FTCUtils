package com.rmsftc.utils.math.units;

public interface NumericalUnit<U extends NumericalUnit<U>> {
    /** What you multiply by to get the base unit */
    NumericalValue<U> baseFactor();
    default NumericalValue<U> convertTo(NumericalValue<U> value, NumericalUnit<U> targetUnit) {
        return targetUnit.convertFrom(value, this);
    }
    default NumericalValue<U> convertFrom(NumericalValue<U> value, NumericalUnit<U> valueUnit) {
        return value.multiply(valueUnit.baseFactor()).divide(baseFactor());
    }

    default NumericalValue<U> of(double value) {
        return new NumericalValue<>(value, (U) this);
    }
}
