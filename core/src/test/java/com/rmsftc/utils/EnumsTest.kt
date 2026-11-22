package com.rmsftc.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EnumsTest {

    @Test
    fun allianceColor() {
        assertTrue(AllianceColor.RED.isRed())
        assertTrue(AllianceColor.BLUE.isBlue())
        assertTrue(AllianceColor.UNKNOWN.isUnknown())
        assertFalse(AllianceColor.RED.isBlue())
        assertEquals(0.0, AllianceColor.RED.fieldCentricOffset())
        assertEquals(180.0, AllianceColor.BLUE.fieldCentricOffset())
        assertEquals(AllianceColor.RED, AllianceColor.from(true))
        assertEquals(AllianceColor.BLUE, AllianceColor.from(false))
    }

    @Test
    fun robotModeTimeLimits() {
        assertEquals(30, RobotMode.AUTONOMOUS.timeLimit)
        assertEquals(120, RobotMode.TELEOP.timeLimit)
        assertEquals(Int.MAX_VALUE, RobotMode.TEST.timeLimit)
    }
}
