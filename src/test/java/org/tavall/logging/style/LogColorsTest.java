package org.tavall.logging.style;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogColorsTest {

    @Test
    void applyPlaceholdersSubstitutesKnownTokens() {
        String result = LogColors.applyPlaceholders("%red%hello%reset%");
        assertEquals(LogColors.RED + "hello" + LogColors.RESET, result);
    }

    @Test
    void applyPlaceholdersLeavesUnknownTokensIntact() {
        assertEquals("keep %nope% here", LogColors.applyPlaceholders("keep %nope% here"));
    }

    @Test
    void applyPlaceholdersHandlesNullAndEmpty() {
        assertEquals(null, LogColors.applyPlaceholders(null));
        assertEquals("", LogColors.applyPlaceholders(""));
    }

    @Test
    void applyPlaceholdersIsCaseInsensitive() {
        assertEquals(LogColors.GREEN + "x" + LogColors.RESET, LogColors.applyPlaceholders("%GREEN%x%RESET%"));
    }

    @Test
    void helperMethodsWrapWithReset() {
        assertEquals(LogColors.RED + "m" + LogColors.RESET, LogColors.red("m"));
        assertEquals(LogColors.BOLD + "m" + LogColors.RESET, LogColors.bold("m"));
        assertEquals(LogColors.BG_RED + "m" + LogColors.RESET, LogColors.bgRed("m"));
    }

    @Test
    void getPlaceholdersReturnsUnmodifiableNonEmptyMap() {
        Map<String, String> placeholders = LogColors.getPlaceholders();
        assertNotNull(placeholders);
        assertFalse(placeholders.isEmpty());
        assertTrue(placeholders.containsKey("RED"));
        assertThrows(UnsupportedOperationException.class, () -> placeholders.put("X", "Y"));
    }
}
