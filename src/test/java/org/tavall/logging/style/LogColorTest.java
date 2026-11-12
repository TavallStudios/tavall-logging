package org.tavall.logging.style;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogColorTest {

    @Test
    void codeMatchesWrappedConstant() {
        assertEquals(LogColors.RED, LogColor.RED.code());
        assertEquals(LogColors.RESET, LogColor.RESET.code());
    }

    @Test
    void isResetOnlyForReset() {
        assertTrue(LogColor.RESET.isReset());
        assertFalse(LogColor.RED.isReset());
        assertFalse(LogColor.GREEN.isReset());
    }

    @Test
    void toStringReturnsCode() {
        assertNotNull(LogColor.BLUE.toString());
        assertEquals(LogColor.BLUE.code(), LogColor.BLUE.toString());
    }
}
