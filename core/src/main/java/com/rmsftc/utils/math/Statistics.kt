package com.rmsftc.utils.math

import kotlin.math.sqrt

/**
 * Descriptive statistics over samples of doubles: mean, median, variance,
 * standard deviation, percentiles. Handy for sensor characterization
 * (e.g. loop-time or encoder-noise measurements).
 *
 * <p>All functions are pure and Java-friendly: call them from Java as
 * <code>Statistics.mean(new double[]{...})</code>. Every function throws
 * [IllegalArgumentException] on an empty input; the sample variants also
 * require at least two values.
 */
object Statistics {

    private fun requireNonEmpty(values: DoubleArray) {
        require(values.isNotEmpty()) { "values must not be empty" }
    }

    /** Sum of all values. */
    @JvmStatic
    fun sum(values: DoubleArray): Double {
        requireNonEmpty(values)
        return values.sum()
    }

    /** Arithmetic mean. */
    @JvmStatic
    fun mean(values: DoubleArray): Double {
        requireNonEmpty(values)
        return values.sum() / values.size
    }

    /** Middle value (average of the two middle values for even counts). */
    @JvmStatic
    fun median(values: DoubleArray): Double {
        requireNonEmpty(values)
        val sorted = values.sorted()
        val mid = sorted.size / 2
        return if (sorted.size % 2 == 1) sorted[mid]
        else (sorted[mid - 1] + sorted[mid]) / 2.0
    }

    /** Smallest value. */
    @JvmStatic
    fun min(values: DoubleArray): Double {
        requireNonEmpty(values)
        return values.min()
    }

    /** Largest value. */
    @JvmStatic
    fun max(values: DoubleArray): Double {
        requireNonEmpty(values)
        return values.max()
    }

    /** Population variance: mean of squared deviations from the mean. */
    @JvmStatic
    fun variance(values: DoubleArray): Double {
        requireNonEmpty(values)
        val mean = mean(values)
        return values.sumOf { (it - mean) * (it - mean) } / values.size
    }

    /** Sample variance (Bessel-corrected, divides by n - 1). Needs >= 2 values. */
    @JvmStatic
    fun sampleVariance(values: DoubleArray): Double {
        require(values.size >= 2) { "sampleVariance needs at least 2 values" }
        val mean = mean(values)
        return values.sumOf { (it - mean) * (it - mean) } / (values.size - 1)
    }

    /** Population standard deviation: square root of [variance]. */
    @JvmStatic
    fun stddev(values: DoubleArray): Double = sqrt(variance(values))

    /** Sample standard deviation: square root of [sampleVariance]. */
    @JvmStatic
    fun sampleStddev(values: DoubleArray): Double = sqrt(sampleVariance(values))

    /**
     * The [p]-th percentile (p in [0, 100]) by linear interpolation between
     * closest ranks: the value at rank `p/100 * (n - 1)` in the sorted data.
     */
    @JvmStatic
    fun percentile(values: DoubleArray, p: Double): Double {
        requireNonEmpty(values)
        require(p in 0.0..100.0) { "percentile must be in [0, 100], was $p" }
        val sorted = values.sorted()
        if (sorted.size == 1) return sorted[0]
        val rank = p / 100.0 * (sorted.size - 1)
        val lower = rank.toInt()
        val fraction = rank - lower
        return if (lower + 1 < sorted.size) {
            sorted[lower] + fraction * (sorted[lower + 1] - sorted[lower])
        } else {
            sorted[lower]
        }
    }
}
