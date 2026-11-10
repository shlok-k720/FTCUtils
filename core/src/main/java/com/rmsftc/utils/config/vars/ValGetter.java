package com.rmsftc.utils.config.vars;

import java.util.function.Supplier;

@FunctionalInterface
public interface ValGetter<T> extends Supplier<T> {
}
