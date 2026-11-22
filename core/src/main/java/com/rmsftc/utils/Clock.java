package com.rmsftc.utils;

import com.rmsftc.utils.math.units.NumericalValue;
import com.rmsftc.utils.math.units.basic.TimeUnit;

import java.util.function.Supplier;

/**
 * Any supplier of time, as a unit-tagged {@link NumericalValue}. The
 * default {@link #SYSTEM} clock reads {@link System#nanoTime()} in
 * nanoseconds. For code that wants plain nanosecond longs, use
 * {@link #nowNanos()} or {@link #asLongSupplier()}.
 */
@FunctionalInterface
public interface Clock extends Supplier<NumericalValue<TimeUnit>> {

    Clock SYSTEM = () -> new NumericalValue<>(System.nanoTime(), TimeUnit.NANOSECOND);

    /** The current time, in whatever unit this clock reports. */
    default NumericalValue<TimeUnit> now() {
        return get();
    }

    /** The current time in nanoseconds. */
    default long nowNanos() {
        return (long) get().convertTo(TimeUnit.NANOSECOND).getValue();
    }

    /** The current time in seconds. */
    default double nowSeconds() {
        return get().convertTo(TimeUnit.SECOND).getValue();
    }

    /** This clock as a plain nanosecond {@link Supplier}. */
    default Supplier<Long> asLongSupplier() {
        return this::nowNanos;
    }

    /**
     * Tracks the time between {@link #update()} calls: call it once per
     * loop and read {@link #dt()} (or the seconds/nanos shortcuts).
     * The first update reports a dt of zero.
     */
    class Helper {
        private final Clock clock;
        private NumericalValue<TimeUnit> last;
        private NumericalValue<TimeUnit> dt = new NumericalValue<>(0.0, TimeUnit.NANOSECOND);

        public Helper(Clock clock) {
            this.clock = clock;
        }

        public void update() {
            NumericalValue<TimeUnit> now = clock.get();
            dt = last == null
                    ? new NumericalValue<>(0.0, now.getUnit())
                    : now.subtract(last);
            last = now;
        }

        /** Time since the previous {@link #update()} (zero on the first). */
        public NumericalValue<TimeUnit> dt() {
            return dt;
        }

        public double dtSeconds() {
            return dt.convertTo(TimeUnit.SECOND).getValue();
        }

        public long dtNanos() {
            return (long) dt.convertTo(TimeUnit.NANOSECOND).getValue();
        }
    }
}
