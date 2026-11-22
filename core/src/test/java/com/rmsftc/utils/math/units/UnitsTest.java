package com.rmsftc.utils.math.units;

import com.rmsftc.utils.math.units.basic.AngleUnit;
import com.rmsftc.utils.math.units.basic.DistanceUnit;
import com.rmsftc.utils.math.units.basic.TimeUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UnitsTest {

    private static final double EPS = 1e-9;

    @Test
    void centimetersToMeters() {
        NumericalValue<DistanceUnit> cm = DistanceUnit.CENTIMETER.of(100.0);
        assertEquals(1.0, cm.convertTo(DistanceUnit.METER).getValue(), EPS);
    }

    @Test
    void metersToCentimeters() {
        NumericalValue<DistanceUnit> m = DistanceUnit.METER.of(2.5);
        assertEquals(250.0, m.convertTo(DistanceUnit.CENTIMETER).getValue(), EPS);
    }

    @Test
    void inchesToMeters() {
        NumericalValue<DistanceUnit> in = DistanceUnit.INCH.of(39.3701);
        assertEquals(1.0, in.convertTo(DistanceUnit.METER).getValue(), EPS);
    }

    @Test
    void feetToInches() {
        NumericalValue<DistanceUnit> ft = DistanceUnit.FOOT.of(1.0);
        assertEquals(12.0, ft.convertTo(DistanceUnit.INCH).getValue(), 1e-4);
    }

    @Test
    void degreesToRadians() {
        NumericalValue<AngleUnit> deg = AngleUnit.DEGREE.of(180.0);
        assertEquals(Math.PI, deg.convertTo(AngleUnit.RADIAN).getValue(), EPS);
    }

    @Test
    void radiansToDegrees() {
        NumericalValue<AngleUnit> rad = AngleUnit.RADIAN.of(Math.PI / 2.0);
        assertEquals(90.0, rad.convertTo(AngleUnit.DEGREE).getValue(), EPS);
    }

    @Test
    void minutesToSeconds() {
        NumericalValue<TimeUnit> min = TimeUnit.MINUTE.of(2.0);
        assertEquals(120.0, min.convertTo(TimeUnit.SECOND).getValue(), EPS);
    }

    @Test
    void millisecondsToSeconds() {
        NumericalValue<TimeUnit> ms = TimeUnit.MILLISECOND.of(1500.0);
        assertEquals(1.5, ms.convertTo(TimeUnit.SECOND).getValue(), EPS);
    }

    @Test
    void addAcrossUnits() {
        NumericalValue<DistanceUnit> sum = DistanceUnit.METER.of(1.0)
                .add(DistanceUnit.CENTIMETER.of(50.0));
        assertEquals(1.5, sum.getValue(), EPS);
        assertEquals(DistanceUnit.METER, sum.getUnit());
    }

    @Test
    void subtractKeepsOperandOrder() {
        NumericalValue<DistanceUnit> diff = DistanceUnit.METER.of(5.0)
                .subtract(DistanceUnit.METER.of(2.0));
        assertEquals(3.0, diff.getValue(), EPS);
    }

    @Test
    void divideKeepsOperandOrder() {
        NumericalValue<DistanceUnit> ratio = DistanceUnit.METER.of(10.0)
                .divide(DistanceUnit.METER.of(4.0));
        assertEquals(2.5, ratio.getValue(), EPS);
    }

    @Test
    void remainderKeepsOperandOrder() {
        NumericalValue<DistanceUnit> rem = DistanceUnit.METER.of(10.0)
                .remainder(DistanceUnit.METER.of(3.0));
        assertEquals(1.0, rem.getValue(), EPS);
    }

    @Test
    void scalarMultiplyAndDivide() {
        NumericalValue<TimeUnit> t = TimeUnit.SECOND.of(4.0);
        assertEquals(12.0, t.multiply(3.0).getValue(), EPS);
        assertEquals(2.0, t.divide(2.0).getValue(), EPS);
    }

    @Test
    void equalsAndToString() {
        NumericalValue<DistanceUnit> a = DistanceUnit.METER.of(3.5);
        NumericalValue<DistanceUnit> b = DistanceUnit.METER.of(3.5);
        NumericalValue<DistanceUnit> c = DistanceUnit.CENTIMETER.of(3.5);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertEquals("3.5 METER", a.toString());
    }
}
