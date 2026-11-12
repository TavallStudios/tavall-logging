package org.tavall.logging;

import org.junit.jupiter.api.Test;
import org.tavall.logging.style.LogText;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LogTest {

    @Test
    void textFactoryReturnsUsableBuilder() {
        LogText text = Log.text();
        assertNotNull(text);
        assertNotNull(text.build());
    }

    @Test
    void levelLoggingDoesNotThrow() {
        assertDoesNotThrow(() -> {
            Log.success("success message");
            Log.info("info message");
            Log.warn("warn message");
            Log.error("error message");
            Log.critical("critical message");
        });
    }

    @Test
    void loggingLogTextDoesNotThrow() {
        assertDoesNotThrow(() -> {
            Log.success(Log.text().append("success"));
            Log.info(Log.text().append("info"));
            Log.warn(Log.text().append("warn"));
            Log.error(Log.text().append("error"));
            Log.critical(Log.text().append("critical"));
        });
    }

    @Test
    void exceptionWithNullIsHandled() {
        assertDoesNotThrow(() -> Log.exception(null));
    }

    @Test
    void exceptionWithCauseChainIsHandled() {
        RuntimeException root = new RuntimeException("root");
        RuntimeException mid = new RuntimeException("mid", root);
        RuntimeException top = new RuntimeException("top", mid);
        assertDoesNotThrow(() -> Log.exception(top));
    }
}
