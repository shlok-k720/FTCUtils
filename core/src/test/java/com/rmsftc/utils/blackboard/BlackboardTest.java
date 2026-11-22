package com.rmsftc.utils.blackboard;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlackboardTest {

    enum Key { POSE, COUNT }

    @Test
    void setAndGet() {
        Blackboard board = new Blackboard();
        board.set("name", "chimera");
        assertEquals("chimera", board.get("name", String.class));
        assertTrue(board.has("name"));
        assertFalse(board.has("missing"));
        assertNull(board.get("missing", String.class));
    }

    @Test
    void enumKeys() {
        Blackboard board = new Blackboard();
        board.set(Key.COUNT, 3);
        assertEquals(3, board.get(Key.COUNT, Integer.class));
        assertTrue(board.has(Key.COUNT));
    }

    @Test
    void getOrDefaultStoresDefault() {
        Blackboard board = new Blackboard();
        assertEquals(7, board.getOrDefault("n", Integer.class, 7));
        assertTrue(board.has("n"));
        assertEquals(7, board.get("n", Integer.class));
    }

    @Test
    void getIfPresentDoesNotWrite() {
        Blackboard board = new Blackboard();
        assertNull(board.getIfPresent("n", Integer.class));
        assertFalse(board.has("n"));
        board.set("n", "text");
        // Wrong type: null, not a ClassCastException.
        assertNull(board.getIfPresent("n", Integer.class));
    }

    @Test
    void putIfAbsentKeepsExisting() {
        Blackboard board = new Blackboard();
        assertEquals(1, board.putIfAbsent("n", Integer.class, 1));
        assertEquals(1, board.putIfAbsent("n", Integer.class, 2));
        assertEquals(1, board.get("n", Integer.class));
    }

    @Test
    void callbacksFireOnSet() {
        Blackboard board = new Blackboard();
        List<String> seen = new ArrayList<>();
        board.registerCallback("k", (BlackboardCallback<String>) (key, value) -> seen.add(key + "=" + value));
        board.set("k", "v1");
        board.set("k", "v2");
        assertEquals(Arrays.asList("k=v1", "k=v2"), seen);
    }

    @Test
    void callbackFailuresDoNotSkipOthers() {
        Blackboard board = new Blackboard();
        List<String> seen = new ArrayList<>();
        board.registerCallback("k", (BlackboardCallback<String>) (key, value) -> {
            throw new RuntimeException("boom");
        });
        board.registerCallback("k", (BlackboardCallback<String>) (key, value) -> seen.add(value));
        assertThrows(RuntimeException.class, () -> board.set("k", "v"));
        assertEquals(Arrays.asList("v"), seen);
    }

    @Test
    void removeValueKeepsCallbacksRemoveDropsBoth() {
        Blackboard board = new Blackboard();
        List<String> seen = new ArrayList<>();
        board.registerCallback("k", (BlackboardCallback<String>) (key, value) -> seen.add(value));
        board.set("k", "a");
        board.removeValue("k");
        assertFalse(board.has("k"));
        board.set("k", "b");
        assertEquals(Arrays.asList("a", "b"), seen);
        board.remove("k");
        board.set("k", "c");
        assertEquals(Arrays.asList("a", "b"), seen);
    }

    @Test
    void clearAll() {
        Blackboard board = new Blackboard();
        board.set("a", 1);
        board.set("b", 2);
        board.clearAll();
        assertFalse(board.has("a"));
        assertFalse(board.has("b"));
    }
}
