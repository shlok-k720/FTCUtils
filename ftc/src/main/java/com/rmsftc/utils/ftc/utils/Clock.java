package com.rmsftc.utils.ftc.utils;

import java.util.function.Supplier;

@FunctionalInterface
public interface Clock extends Supplier<Long> {

    class Helper {
        private Clock clock;
        public Helper(Clock clock) {
            this.clock = clock;
        }

        public void update() {}
    }
}
