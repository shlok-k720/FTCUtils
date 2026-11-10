package com.rmsftc.utils.config.vars;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationTest {

    @Test
    void emptyValidationPassesAnything() {
        Validation<Double> validation = new Validation<>(Collections.emptyList());
        validation.validate(1.0);
        validation.validate(null);
    }

    @Test
    void validatePassesWhenAllValidatorsPass() {
        Validation<Double> validation = new Validation<>(
                Arrays.asList(Validator.nonnegative(), Validator.inRange(0.0, 10.0)));
        validation.validate(5.0);
    }

    @Test
    void validateThrowsWhenAnyValidatorFails() {
        Validation<Double> validation = new Validation<>(
                Arrays.asList(Validator.nonnegative(), Validator.inRange(0.0, 10.0)));
        IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> validation.validate(15.0));
        assertTrue(thrown.getMessage().contains("Validation failed"));
        assertThrows(IllegalArgumentException.class, () -> validation.validate(-1.0));
    }
}
