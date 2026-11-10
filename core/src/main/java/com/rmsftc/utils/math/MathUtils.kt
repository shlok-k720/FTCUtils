package com.rmsftc.utils.math

import kotlin.math.abs

/**
 * Stateless math helpers for FTC robot code: clamping, interpolation,
 * angle wrapping, deadbands, and joystick-curve shaping.
 *
 * <p>All functions are pure (no hidden state) and Java-friendly: call them
 * from Java as <code>MathUtils.clamp(...)</code>.
 */
object MathUtils {

    /** Returns [value] limited to the range [`min`, `max`]. */
    @JvmStatic
    fun clamp(value: Double, min: Double, max: Double): Double =
        maxOf(min, minOf(value, max))

    /** Returns [value] limited to the range [`min`, `max`]. */
    @JvmStatic
    fun clamp(value: Int, min: Int, max: Int): Int =
        maxOf(min, minOf(value, max))

    /** Returns [value] limited to the range [`min`, `max`]. */
    @JvmStatic
    fun clamp(value: Long, min: Long, max: Long): Long =
        maxOf(min, minOf(value, max))

    /**
     * Linear interpolation: `a` at `t = 0`, `b` at `t = 1`.
     * `t` is not clamped, so values outside [0, 1] extrapolate.
     */
    @JvmStatic
    fun lerp(a: Double, b: Double, t: Double): Double = a + (b - a) * t

    /**
     * Maps [value] from the range [`fromMin`, `fromMax`] to
     * [`toMin`, `toMax`]. Throws [IllegalArgumentException] if
     * `fromMin == fromMax`.
     */
    @JvmStatic
    fun mapRange(
        value: Double,
        fromMin: Double,
        fromMax: Double,
        toMin: Double,
        toMax: Double
    ): Double {
        require(fromMin != fromMax) { "fromMin must not equal fromMax" }
        return toMin + (value - fromMin) * (toMax - toMin) / (fromMax - fromMin)
    }

    /** Wraps an angle in degrees to [-180, 180). */
    @JvmStatic
    fun wrapAngleDegrees(angle: Double): Double {
        var wrapped = (angle + 180.0) % 360.0
        if (wrapped < 0) wrapped += 360.0
        return wrapped - 180.0
    }

    /** Wraps an angle in radians to [-PI, PI). */
    @JvmStatic
    fun wrapAngleRadians(angle: Double): Double {
        var wrapped = (angle + Math.PI) % (2.0 * Math.PI)
        if (wrapped < 0) wrapped += 2.0 * Math.PI
        return wrapped - Math.PI
    }

    /**
     * Shortest signed error from [current] to [target] in degrees:
     * positive means turn counterclockwise (toward increasing angle).
     * The result is in [-180, 180).
     */
    @JvmStatic
    fun angleErrorDegrees(target: Double, current: Double): Double =
        wrapAngleDegrees(target - current)

    /**
     * Shortest signed error from [current] to [target] in radians.
     * The result is in [-PI, PI).
     */
    @JvmStatic
    fun angleErrorRadians(target: Double, current: Double): Double =
        wrapAngleRadians(target - current)

    /**
     * Zeroes inputs inside the deadband: returns 0 when
     * `|value| < deadband`, otherwise [value] unchanged.
     */
    @JvmStatic
    fun deadband(value: Double, deadband: Double): Double =
        if (abs(value) < deadband) 0.0 else value

    /**
     * Moves [previous] toward [target] by at most [maxChange] — a pure,
     * stateless rate limit. Returns [target] when it is within [maxChange]
     * of [previous].
     */
    @JvmStatic
    fun rateLimit(previous: Double, target: Double, maxChange: Double): Double =
        previous + clamp(target - previous, -maxChange, maxChange)

    /** Sign-preserving square: `value * |value|`, for joystick curves. */
    @JvmStatic
    fun signSquare(value: Double): Double = value * abs(value)

    /** Sign-preserving cube: `value^3`, for joystick curves. */
    @JvmStatic
    fun signCube(value: Double): Double = value * value * value

    /** Returns true when `|a - b| <= epsilon`. */
    @JvmStatic
    fun epsilonEquals(a: Double, b: Double, epsilon: Double): Boolean =
        abs(a - b) <= epsilon

    /** Returns true when `|a - b| <= 1e-9`. */
    @JvmStatic
    fun epsilonEquals(a: Double, b: Double): Boolean =
        epsilonEquals(a, b, 1e-9)
}
