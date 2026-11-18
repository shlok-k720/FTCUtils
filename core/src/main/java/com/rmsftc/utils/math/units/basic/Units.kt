package com.rmsftc.utils.math.units.basic

import com.rmsftc.utils.math.units.NumericalUnit

enum class AngleUnit(val factor: Double) : NumericalUnit<AngleUnit> {
    DEGREE(Math.PI / 180.0),
    RADIAN(1.0);
    override fun baseFactor(): com.rmsftc.utils.math.units.NumericalValue<AngleUnit> {
        return com.rmsftc.utils.math.units.NumericalValue.of(factor, this)
    }
}

enum class DistanceUnit(val factor: Double) : NumericalUnit<DistanceUnit> {
    METER(1.0),
    CENTIMETER(100.0),
    MILLIMETER(1_000.0),
    INCH(39.3701),
    FOOT(3.28084);
    override fun baseFactor(): com.rmsftc.utils.math.units.NumericalValue<DistanceUnit> {
        return com.rmsftc.utils.math.units.NumericalValue.of(factor, this)
    }
}

enum class TimeUnit(val factor: Double) : NumericalUnit<TimeUnit> {
    MINUTE(1 / 60.0),
    SECOND(1.0),
    MILLISECOND(1_000.0),
    MICROSECOND(1_000_000.0),
    NANOSECOND(1_000_000_000.0);
    override fun baseFactor(): com.rmsftc.utils.math.units.NumericalValue<TimeUnit> {
        return com.rmsftc.utils.math.units.NumericalValue.of(factor, this)
    }
}

enum class NumberUnit : NumericalUnit<NumberUnit> {
    INST;
    override fun baseFactor(): com.rmsftc.utils.math.units.NumericalValue<NumberUnit> {
        return com.rmsftc.utils.math.units.NumericalValue.of(1.0, this)
    }
}