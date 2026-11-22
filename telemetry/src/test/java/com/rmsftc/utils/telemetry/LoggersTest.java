package com.rmsftc.utils.telemetry;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoggersTest {

    @Test
    void stringLoggerCollects() {
        StringLogger logger = new StringLogger();
        logger.log("one");
        logger.log("two");
        assertEquals(Arrays.asList("one", "two"), logger.getLines());
        assertEquals("one\ntwo", logger.toString());
        logger.clear();
        assertEquals(0, logger.getLines().size());
    }

    @Test
    void multipleLoggerFansOut() {
        StringLogger a = new StringLogger();
        StringLogger b = new StringLogger();
        MultipleLogger both = new MultipleLogger(a).add(b);
        both.log("hello");
        assertEquals(Arrays.asList("hello"), a.getLines());
        assertEquals(Arrays.asList("hello"), b.getLines());
    }
}
