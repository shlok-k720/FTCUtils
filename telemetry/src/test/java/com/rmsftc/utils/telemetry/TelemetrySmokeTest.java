package com.rmsftc.utils.telemetry;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Smoke test for the telemetry module's first real code (Strings and
// DisplayFormat). Replaces the placeholder MyClassTest, whose MyClass
// was deleted in 1723308.
class TelemetrySmokeTest {

    @Test
    void stringsCombineAndFormat() {
        assertEquals("ab", Strings.combine("a", "b"));
        assertEquals("3", Strings.format("%d", 3));
        assertEquals("--------", Strings.rowOfDashes());
    }

    @Test
    void displayFormatDefaultInstance() {
        DisplayFormat format = DisplayFormat.instance;
        assertEquals(Locale.US, format.getLocale());
        assertEquals("", format.beforeQuestion());
        assertEquals("--------", format.afterAnswerChoicesAppear());
        assertEquals("> ", format.currentChoiceIndicator());
    }
}
