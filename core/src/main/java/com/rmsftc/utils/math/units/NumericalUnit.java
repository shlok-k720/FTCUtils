package com.rmsftc.utils.math.units;

/**
 * A unit of measure for a {@link NumericalValue}. Units of the same kind
 * (distance, time, angle, ...) share a type parameter, so values can only be
 * converted and combined within their own kind.
 *
 * <p>Each unit declares a <em>from-base</em> factor: how many of this unit
 * make one base unit. For example, with meters as the base distance unit,
 * {@code CENTIMETER} has factor 100 (1 m = 100 cm) and {@code INCH} has
 * factor 39.3701 (1 m = 39.3701 in).
 */
public interface NumericalUnit<U extends NumericalUnit<U>> {

    /**
     * What you multiply a value in the base unit by to get this unit
     * ("from base unit"). The base unit itself has factor 1.
     */
    NumericalValue<U> baseFactor();

    /**
     * Converts {@code value} (given in this unit) into {@code targetUnit}.
     */
    default NumericalValue<U> convertTo(NumericalValue<U> value, NumericalUnit<U> targetUnit) {
        return targetUnit.convertFrom(value, this);
    }

    /**
     * Converts {@code value} (given in {@code valueUnit}) into this unit.
     * With from-base factors {@code f}, the conversion is
     * {@code value * f(this) / f(valueUnit)}: dividing by the source factor
     * gives the amount in base units, and multiplying by this unit's factor
     * gives the amount in this unit.
     */
    default NumericalValue<U> convertFrom(NumericalValue<U> value, NumericalUnit<U> valueUnit) {
        // Same unit: return the value unchanged. The round-trip through
        // base units (value / f * f in double) is not exact and drifted
        // Clock.SYSTEM.nowNanos() by ±1 ns.
        if (valueUnit.equals(this)) {
            return value;
        }
        double inBase = value.getValue() / valueUnit.baseFactor().getValue();
        return new NumericalValue<>(inBase * baseFactor().getValue(), (U) this);
    }

    /** Creates a value of {@code value} in this unit. */
    default NumericalValue<U> of(double value) {
        return new NumericalValue<>(value, (U) this);
    }
}
