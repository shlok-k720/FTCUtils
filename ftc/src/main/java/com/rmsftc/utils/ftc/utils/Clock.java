package com.rmsftc.utils.ftc.utils;

import com.rmsftc.utils.math.units.NumericalValue;
import com.rmsftc.utils.math.units.basic.TimeUnit;

import java.util.function.Supplier;

@FunctionalInterface
public interface Clock extends Supplier<NumericalValue<TimeUnit>> {

    Clock SYSTEM = () -> new NumericalValue<>(System.nanoTime(), TimeUnit.NANOSECOND);

    class Helper {
        private Clock clock;
        public Helper(Clock clock) {
            this.clock = clock;
        }

        public void update() {}
    }
}
