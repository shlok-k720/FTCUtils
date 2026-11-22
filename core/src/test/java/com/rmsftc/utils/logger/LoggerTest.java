package com.rmsftc.utils.logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerTest {

    @AfterEach
    void tearDown() {
        Logger.reset();
    }

    @Test
    void logReachesAttachedSystems() {
        List<String> events = new ArrayList<>();
        Logger.attachLogger((caption, data, ts) -> events.add(caption + ": " + data));
        Logger.log("test", "hello");
        assertEquals(Arrays.asList("test: hello"), events);
    }

    @Test
    void logWithNoSystemsIsNoOp() {
        // Must not throw.
        Logger.log("test", "nobody listening");
    }

    @Test
    void removeAndReset() {
        List<String> events = new ArrayList<>();
        LogSystem system = (caption, data, ts) -> events.add(data);
        Logger.attachLogger(system);
        Logger.log("c", "one");
        Logger.removeLogger(system);
        Logger.log("c", "two");
        assertEquals(Arrays.asList("one"), events);

        Logger.attachLogger(system);
        Logger.reset();
        Logger.log("c", "three");
        assertEquals(Arrays.asList("one"), events);
    }

    @Test
    void timestampIsPositive() {
        double[] ts = {0};
        Logger.attachLogger((caption, data, t) -> ts[0] = t);
        Logger.log("c", "d");
        assertTrue(ts[0] > 0);
    }
}
