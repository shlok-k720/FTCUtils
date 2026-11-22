package com.rmsftc.utils

// Like the ones in the 20373 repo: the generic FTC enums every robot needs.

enum class AllianceColor {
    RED,
    BLUE,
    UNKNOWN;

    fun isRed(): Boolean {
        return this == RED
    }

    fun isBlue(): Boolean {
        return this == BLUE
    }

    fun isUnknown(): Boolean {
        return this == UNKNOWN
    }

    /** Heading offset (degrees) for field-centric driving on this alliance. */
    fun fieldCentricOffset(): Double {
        if (isRed()) return 0.0
        return 180.0
    }

    companion object {
        @JvmStatic
        fun from(isRed: Boolean): AllianceColor {
            if (isRed) {
                return RED
            } else {
                return BLUE
            }
        }
    }
}

enum class RobotMode(val timeLimit: Int) {
    AUTONOMOUS(30),
    TELEOP(120),
    TEST(Int.MAX_VALUE)
}

enum class MatchMode {
    PLAYOFFS, // maximize points
    QUALIFICATIONS // maximize ranking points
}
