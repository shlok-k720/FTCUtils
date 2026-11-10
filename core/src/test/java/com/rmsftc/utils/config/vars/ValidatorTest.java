package com.rmsftc.utils.config.vars;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorTest {

    @Test
    void positive() {
        assertTrue(Validator.positive().validate(1.0));
        assertFalse(Validator.positive().validate(0.0));
        assertFalse(Validator.positive().validate(-1.0));
    }

    @Test
    void nonnegative() {
        assertTrue(Validator.nonnegative().validate(0.0));
        assertTrue(Validator.nonnegative().validate(1.0));
        assertFalse(Validator.nonnegative().validate(-0.1));
    }

    @Test
    void negative() {
        assertTrue(Validator.negative().validate(-1.0));
        assertFalse(Validator.negative().validate(0.0));
        assertFalse(Validator.negative().validate(1.0));
    }

    @Test
    void nonpositive() {
        assertTrue(Validator.nonpositive().validate(0.0));
        assertTrue(Validator.nonpositive().validate(-1.0));
        assertFalse(Validator.nonpositive().validate(0.1));
    }

    @Test
    void nonnull() {
        assertTrue(Validator.nonnull().validate("x"));
        assertFalse(Validator.<String>nonnull().validate(null));
    }

    @Test
    void inRangeIsInclusive() {
        Validator<Double> range = Validator.inRange(0.0, 10.0);
        assertTrue(range.validate(0.0));
        assertTrue(range.validate(10.0));
        assertTrue(range.validate(5.0));
        assertFalse(range.validate(-0.1));
        assertFalse(range.validate(10.1));
    }

    @Test
    void nonZero() {
        assertTrue(Validator.nonZero().validate(0.5));
        assertFalse(Validator.nonZero().validate(0.0));
    }
}
