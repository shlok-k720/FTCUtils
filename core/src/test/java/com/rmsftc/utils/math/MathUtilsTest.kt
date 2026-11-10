package com.rmsftc.utils.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.PI

class MathUtilsTest {

    @Test
    fun clampDouble() {
        assertEquals(0.5, MathUtils.clamp(0.5, 0.0, 1.0))
        assertEquals(0.0, MathUtils.clamp(-1.0, 0.0, 1.0))
        assertEquals(1.0, MathUtils.clamp(2.0, 0.0, 1.0))
    }

    @Test
    fun clampIntAndLong() {
        assertEquals(3, MathUtils.clamp(3, 0, 10))
        assertEquals(0, MathUtils.clamp(-5, 0, 10))
        assertEquals(10, MathUtils.clamp(99, 0, 10))
        assertEquals(7L, MathUtils.clamp(7L, 0L, 10L))
        assertEquals(10L, MathUtils.clamp(100L, 0L, 10L))
    }

    @Test
    fun lerpInterpolatesAndExtrapolates() {
        assertEquals(0.0, MathUtils.lerp(0.0, 10.0, 0.0))
        assertEquals(5.0, MathUtils.lerp(0.0, 10.0, 0.5))
        assertEquals(10.0, MathUtils.lerp(0.0, 10.0, 1.0))
        assertEquals(15.0, MathUtils.lerp(0.0, 10.0, 1.5))
    }

    @Test
    fun mapRangeMapsAcrossRanges() {
        assertEquals(-1.0, MathUtils.mapRange(0.0, 0.0, 10.0, -1.0, 1.0))
        assertEquals(0.0, MathUtils.mapRange(5.0, 0.0, 10.0, -1.0, 1.0))
        assertEquals(1.0, MathUtils.mapRange(10.0, 0.0, 10.0, -1.0, 1.0))
        assertEquals(-1.0, MathUtils.mapRange(10.0, 10.0, 0.0, -1.0, 1.0))
    }

    @Test
    fun mapRangeRejectsEmptySourceRange() {
        assertThrows(IllegalArgumentException::class.java) {
            MathUtils.mapRange(1.0, 5.0, 5.0, 0.0, 1.0)
        }
    }

    @Test
    fun wrapAngleDegrees() {
        assertEquals(0.0, MathUtils.wrapAngleDegrees(0.0))
        assertEquals(-170.0, MathUtils.wrapAngleDegrees(190.0))
        assertEquals(170.0, MathUtils.wrapAngleDegrees(-190.0))
        assertEquals(-180.0, MathUtils.wrapAngleDegrees(180.0))
        assertEquals(0.0, MathUtils.wrapAngleDegrees(360.0))
        assertEquals(90.0, MathUtils.wrapAngleDegrees(450.0))
    }

    @Test
    fun wrapAngleRadians() {
        assertEquals(0.0, MathUtils.wrapAngleRadians(0.0))
        assertEquals(-PI / 2, MathUtils.wrapAngleRadians(3 * PI / 2))
        assertEquals(PI / 2, MathUtils.wrapAngleRadians(-3 * PI / 2))
        assertEquals(-PI, MathUtils.wrapAngleRadians(PI))
    }

    @Test
    fun angleErrorIsTheShortestSignedError() {
        assertEquals(10.0, MathUtils.angleErrorDegrees(10.0, 0.0))
        assertEquals(-10.0, MathUtils.angleErrorDegrees(0.0, 10.0))
        // 170 -> -170 is a 20-degree turn counterclockwise, not 340 clockwise.
        assertEquals(20.0, MathUtils.angleErrorDegrees(-170.0, 170.0))
        assertEquals(-20.0, MathUtils.angleErrorDegrees(170.0, -170.0))
        assertEquals(-PI / 2, MathUtils.angleErrorRadians(0.0, PI / 2))
    }

    @Test
    fun deadbandZeroesSmallInputs() {
        assertEquals(0.0, MathUtils.deadband(0.05, 0.1))
        assertEquals(0.0, MathUtils.deadband(-0.05, 0.1))
        assertEquals(0.5, MathUtils.deadband(0.5, 0.1))
        assertEquals(-0.5, MathUtils.deadband(-0.5, 0.1))
    }

    @Test
    fun rateLimitCapsTheStep() {
        assertEquals(0.1, MathUtils.rateLimit(0.0, 1.0, 0.1))
        assertEquals(-0.1, MathUtils.rateLimit(0.0, -1.0, 0.1))
        assertEquals(1.0, MathUtils.rateLimit(0.9, 1.0, 0.5))
        assertEquals(0.0, MathUtils.rateLimit(0.0, 1.0, 0.0))
    }

    @Test
    fun signSquareAndCubePreserveSign() {
        assertEquals(0.25, MathUtils.signSquare(0.5))
        assertEquals(-0.25, MathUtils.signSquare(-0.5))
        assertEquals(0.125, MathUtils.signCube(0.5))
        assertEquals(-0.125, MathUtils.signCube(-0.5))
        assertEquals(0.0, MathUtils.signSquare(0.0))
    }

    @Test
    fun epsilonEquals() {
        assertTrue(MathUtils.epsilonEquals(1.0, 1.0 + 5e-10))
        assertFalse(MathUtils.epsilonEquals(1.0, 1.0 + 5e-9))
        assertTrue(MathUtils.epsilonEquals(1.0, 1.05, 0.1))
        assertFalse(MathUtils.epsilonEquals(1.0, 1.2, 0.1))
    }
}
