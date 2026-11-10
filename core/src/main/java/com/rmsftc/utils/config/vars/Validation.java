package com.rmsftc.utils.config.vars;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A set of {@link Validator}s checked together. {@link #validate(Object)}
 * throws {@link IllegalArgumentException} listing every validator that
 * failed. Ported from the FTChimera team's vars system.
 */
public class Validation<T> {
    public Set<Validator<T>> validators;

    public Validation(Collection<Validator<T>> validators) {
        this.validators = Collections.unmodifiableSet(new HashSet<>(validators));
    }

    public void validate(T value) {
        Set<Validator<T>> failed = new HashSet<>();
        for (Validator<T> validator : validators) {
            if (!validator.validate(value)) {
                failed.add(validator);
            }
        }
        if (!failed.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + failed);
        }
    }
}
