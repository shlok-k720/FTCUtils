package com.rmsftc.utils.config.vars.kt

import com.rmsftc.utils.config.vars.kt.BoolVar
import com.rmsftc.utils.config.vars.kt.DoubleVar
import com.rmsftc.utils.config.vars.kt.IntVar
import com.rmsftc.utils.config.vars.kt.StrVar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TypesTest {

    @Test
    fun factoriesCreateNamedVars() {
        val speed: DoubleVar = DoubleVar("Speed", 0.8)
        assertEquals("Speed", speed.getName())
        assertEquals(0.8, speed.get())

        val count: IntVar = IntVar("Count", 7)
        assertEquals(7, count.get())

        val flag: BoolVar = BoolVar("Flag", true)
        assertTrue(flag.get())

        val text: StrVar = StrVar("Text", "hi")
        assertEquals("hi", text.get())
    }
}
