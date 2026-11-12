package org.tavall.logging.style;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogTextTest {

    @Test
    void createAndOfBuildEmptyAndSeeded() {
        assertEquals("", LogText.create().build());
        assertEquals("hi", LogText.of("hi").build());
    }

    @Test
    void appendStringAccumulates() {
        assertEquals("abc", LogText.create().append("a").append("b").append("c").build());
    }

    @Test
    void appendColorEmitsCode() {
        String built = LogText.create().append(LogColor.RED).build();
        assertEquals(LogColor.RED.code(), built);
    }

    @Test
    void appendColorWithTextResetsUnlessResetColor() {
        String withColor = LogText.create().append(LogColor.GREEN, "x").build();
        assertEquals(LogColor.GREEN.code() + "x" + LogColor.RESET.code(), withColor);

        String withReset = LogText.create().append(LogColor.RESET, "y").build();
        assertEquals(LogColor.RESET.code() + "y", withReset);
    }

    @Test
    void resetEmitsResetCode() {
        assertEquals(LogColor.RESET.code(), LogText.create().reset().build());
    }

    @Test
    void toStringEqualsBuild() {
        LogText text = LogText.create().append("z");
        assertEquals(text.build(), text.toString());
    }
}
