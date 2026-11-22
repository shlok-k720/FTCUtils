package com.rmsftc.utils.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class StatisticsTest {

    private val data = doubleArrayOf(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0)

    @Test
    fun meanMedianSumMinMax() {
        assertEquals(40.0, Statistics.sum(data))
        assertEquals(5.0, Statistics.mean(data))
        assertEquals(4.5, Statistics.median(data))
        assertEquals(2.0, Statistics.min(data))
        assertEquals(9.0, Statistics.max(data))
    }

    @Test
    fun medianOddCount() {
        assertEquals(3.0, Statistics.median(doubleArrayOf(5.0, 1.0, 3.0)))
    }

    @Test
    fun varianceAndStddev() {
        // Classic textbook set: population variance 4, stddev 2.
        assertEquals(4.0, Statistics.variance(data), 1e-12)
        assertEquals(2.0, Statistics.stddev(data), 1e-12)
        assertEquals(32.0 / 7.0, Statistics.sampleVariance(data), 1e-12)
        assertEquals(kotlin.math.sqrt(32.0 / 7.0), Statistics.sampleStddev(data), 1e-12)
    }

    @Test
    fun percentileInterpolates() {
        assertEquals(2.0, Statistics.percentile(data, 0.0))
        assertEquals(9.0, Statistics.percentile(data, 100.0))
        assertEquals(4.5, Statistics.percentile(data, 50.0))
        assertEquals(3.5, Statistics.percentile(doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0), 62.5))
    }

    @Test
    fun emptyInputThrows() {
        assertThrows(IllegalArgumentException::class.java) { Statistics.mean(doubleArrayOf()) }
        assertThrows(IllegalArgumentException::class.java) { Statistics.sampleVariance(doubleArrayOf(1.0)) }
        assertThrows(IllegalArgumentException::class.java) { Statistics.percentile(data, 101.0) }
    }
}
