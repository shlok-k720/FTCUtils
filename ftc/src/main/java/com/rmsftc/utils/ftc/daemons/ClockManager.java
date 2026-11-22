package com.rmsftc.utils.ftc.daemons;

import com.rmsftc.utils.Clock;
import com.rmsftc.utils.math.units.NumericalValue;
import com.rmsftc.utils.math.units.basic.TimeUnit;

import java.util.Objects;

/**
 * The classic daemon usage: one static {@link Clock} for the whole robot,
 * and a {@code @FTCDaemon} that runs every loop to snapshot the time and
 * compute {@code dt} since the previous loop. Code anywhere can then read
 * {@link #getTime()}, {@link #getDt()}, or {@link #getDtSeconds()} without
 * threading a clock through every class.
 *
 * <p>Swap the time source with {@link #setClock(Clock)} (e.g. a fake clock
 * in tests); it defaults to {@link Clock#SYSTEM}.
 */
public class ClockManager {
    private static Clock clock = Clock.SYSTEM;
    private static NumericalValue<TimeUnit> time = new NumericalValue<>(0.0, TimeUnit.NANOSECOND);
    private static NumericalValue<TimeUnit> lastTime;
    private static NumericalValue<TimeUnit> dt = new NumericalValue<>(0.0, TimeUnit.NANOSECOND);

    public static Clock getClock() {
        return clock;
    }

    public static void setClock(Clock clock) {
        ClockManager.clock = Objects.requireNonNull(clock, "clock");
        reset();
    }

    /**
     * Runs every loop (registered by {@code DaemonManager}): snapshots the
     * clock and updates {@code dt}. The first run after a reset reports a
     * dt of zero.
     */
    @FTCDaemon
    public static void daemon() {
        NumericalValue<TimeUnit> now = clock.get();
        dt = lastTime == null
                ? new NumericalValue<>(0.0, now.getUnit())
                : now.subtract(lastTime);
        lastTime = now;
        time = now;
    }

    /** The time snapshot from the most recent {@link #daemon()} run. */
    public static NumericalValue<TimeUnit> getTime() {
        return time;
    }

    /** Time between the last two {@link #daemon()} runs. */
    public static NumericalValue<TimeUnit> getDt() {
        return dt;
    }

    public static double getDtSeconds() {
        return dt.convertTo(TimeUnit.SECOND).getValue();
    }

    public static double getTimeSeconds() {
        return time.convertTo(TimeUnit.SECOND).getValue();
    }

    /** Forgets the previous snapshot; the next daemon run reports dt = 0. */
    public static void reset() {
        lastTime = null;
        dt = new NumericalValue<>(0.0, TimeUnit.NANOSECOND);
    }
}
