/*
 * TJVD License (TJ Valentineâ€™s Discretionary License) â€” Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package org.tavall.logging;

import org.tavall.logging.style.LogColors;
import org.tavall.logging.style.LogText;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Asynchronous console logger used by Tavall modules.
 *
 * <p>Public logging methods enqueue fully formatted messages onto a shared queue. A daemon logging
 * thread drains that queue and writes to standard output, so callers do not block on console I/O.
 * Color placeholders embedded in string messages are expanded immediately before enqueueing.</p>
 */
public class Log {
    private static final BlockingQueue<String> asyncQueue = new LinkedBlockingQueue<>();
    private static final Thread logThread;

    static {
        logThread = new Thread(() -> {
            while (true) {
                try {
                    String message = asyncQueue.take();
                    System.out.println(message);
                } catch (InterruptedException ignored) {
                }
            }
        }, "LogThread");
        logThread.setDaemon(true);
        logThread.start();
    }

    /**
     * Enqueues a success-level message using the success color treatment.
     *
     * @param msg message text to log
     */
    public static void success(String msg) {
        log("[SUCCESS] ", LogColors.GREEN, msg);
    }

    /**
     * Builds and enqueues a success-level styled message.
     *
     * @param text styled text to render before logging
     */
    public static void success(LogText text) {
        success(text.build());
    }

    /**
     * Enqueues an informational message.
     *
     * @param msg message text to log
     */
    public static void info(String msg) {
        log("[INFO] ", LogColors.WHITE, msg);
    }

    /**
     * Builds and enqueues an informational styled message.
     *
     * @param text styled text to render before logging
     */
    public static void info(LogText text) {
        info(text.build());
    }

    /**
     * Enqueues a warning message using the warning color treatment.
     *
     * @param msg message text to log
     */
    public static void warn(String msg) {
        log("[WARN] ", LogColors.YELLOW, msg);
    }

    /**
     * Builds and enqueues a warning styled message.
     *
     * @param text styled text to render before logging
     */
    public static void warn(LogText text) {
        warn(text.build());
    }

    /**
     * Enqueues an error message using the error color treatment.
     *
     * @param msg message text to log
     */
    public static void error(String msg) {
        log("[ERROR] ", LogColors.RED, msg);
    }

    /**
     * Builds and enqueues an error-level styled message.
     *
     * @param text styled text to render before logging
     */
    public static void error(LogText text) {
        error(text.build());
    }

    /**
     * Enqueues a critical message rendered with a red background to distinguish it from ordinary
     * error output.
     *
     * @param msg message text to log
     */
    public static void critical(String msg) {
        log("[CRITICAL] ", LogColors.WHITE, LogColors.bgRed(msg));
    }

    /**
     * Builds and enqueues a critical styled message.
     *
     * @param text styled text to render before logging
     */
    public static void critical(LogText text) {
        critical(text.build());
    }

    /**
     * Starts a fluent styled-text builder suitable for any of the {@code LogText} overloads.
     *
     * @return a new empty text builder
     */
    public static LogText text() {
        return LogText.create();
    }

    private static void log(String level, String color, String msg) {
        String processed = LogColors.applyPlaceholders(msg);
        String output = color + level + " " + processed + LogColors.RESET;
        asyncQueue.offer(output);
    }

    /**
     * Emits a compact application-focused diagnostic for an exception.
     *
     * <p>The diagnostic includes the exception type and message, the first Tavall/application stack
     * frame when one exists, a summarized cause chain, and up to eight application frames from the
     * original throwable. If no application frame is found, up to five raw stack frames are used
     * instead. A {@code null} throwable is logged as an error rather than throwing from the logger.</p>
     *
     * @param t throwable to inspect and log
     */
    public static void exception(Throwable t) {
        if (t == null) {
            error("Exception: No exception to log, is it null? ");
            return;
        }

        Throwable root = rootCause(t);
        StackTraceElement[] stack = t.getStackTrace();
        StackTraceElement appTop = firstAppFrame(stack);

        // Main exception info
        error("Exception: " + t.getClass().getName() + (t.getMessage() != null ? ": " + t.getMessage() : ""));
        if (appTop != null) {
            error("Source: " + formatFrame(appTop));
        } else if (stack.length > 0) {
            error("Source: " + formatFrame(stack[0]));
        }

        // Cause chain with first app frame per cause
        if (root != t) {
            error("Cause chain:");
            Throwable current = t.getCause();
            while (current != null) {
                StackTraceElement causeAppFrame = firstAppFrame(current.getStackTrace());
                String causeInfo = current.getClass().getName() +
                        (current.getMessage() != null ? ": " + current.getMessage() : "");
                if (causeAppFrame != null) {
                    error("  -> " + causeInfo + " at " + formatFrame(causeAppFrame));
                } else {
                    error("  -> " + causeInfo);
                }
                current = current.getCause();
                if (current == root) break; // avoid infinite loops
            }
        }

        // App stack with metadata-aware verbosity
        int printed = 0;
        error("App stack:");
        Set<Class<?>> culpritClasses = new HashSet<>();

        for (StackTraceElement ste : stack) {
            if (isAppFrame(ste)) {
                error("  at " + formatFrame(ste));

                // Track culprit classes for summary
                try {
                    Class<?> frameClass = Class.forName(ste.getClassName());
                    culpritClasses.add(frameClass);
                } catch (ClassNotFoundException ignored) {
                }

                if (++printed >= 8) break;
            }
        }

        if (printed == 0) {
            int lim = Math.min(5, stack.length);
            for (int i = 0; i < lim; i++) {
                error("  at " + formatFrame(stack[i]));
            }
        }
        // Culprit summary using metadata
//        if (!culpritClasses.isEmpty()) {
//            error("Culprit summary (classes in exception):");
//            for (Class<?> clazz : culpritClasses) {
//                if (getAbstractClassMetaDataInstance() != null) {
//                    error("  " + clazz.getSimpleName() +
//                          " (registrations=" +   getRegistrationCount() +
//                          ", instantiations=" +   getInstantiationCount() +
//                          ", dependencies=" +   getAttachedDependencies().size() + ")");
//                } else {
//                    error("  " + clazz.getSimpleName() + " (metadata unavailable)");
//                }
//            }
//        }
    }

    private static boolean isAppFrame(StackTraceElement ste) {
        String cn = ste.getClassName();
        return cn.startsWith("org.tavall.") || cn.startsWith("com.tjxnjoobie");
    }

    private static String formatFrame(StackTraceElement ste) {
        String file = ste.getFileName();
        int line = ste.getLineNumber();
        return ste.getClassName() + "." + ste.getMethodName() + "(" + (file != null ? file : "Unknown Source") + (line >= 0 ? ":" + line : "") + ")";
    }

    private static StackTraceElement firstAppFrame(StackTraceElement[] stack) {
        if (stack == null) return null;
        for (StackTraceElement ste : stack) {
            if (isAppFrame(ste)) return ste;
        }
        return null;
    }

    private static Throwable rootCause(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur;
    }
}
