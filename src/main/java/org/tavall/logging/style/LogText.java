/*
 * TJVD License (TJ Valentineâ€™s Discretionary License) â€” Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package org.tavall.logging.style;

/**
 * Mutable fluent builder for ANSI-styled log text.
 *
 * <p>The builder stores the exact sequence of text and style control codes appended to it. Color
 * segments added with {@link #append(LogColor, String)} are automatically reset after the supplied
 * text unless the color itself is the reset code.</p>
 */
public final class LogText {

    private final StringBuilder builder = new StringBuilder();

    private LogText() {
    }

    /**
     * Creates an empty styled-text builder.
     *
     * @return a new builder
     */
    public static LogText create() {
        return new LogText();
    }

    /**
     * Creates a builder initialized with raw text.
     *
     * @param text initial text to append
     * @return a new builder containing the supplied text
     */
    public static LogText of(String text) {
        return create().append(text);
    }

    /**
     * Appends raw text without changing the active style.
     *
     * @param text text to append
     * @return this builder
     */
    public LogText append(String text) {
        builder.append(text);
        return this;
    }

    /**
     * Appends only a color or style control code, leaving it active for later text.
     *
     * @param color control code to append
     * @return this builder
     */
    public LogText append(LogColor color) {
        builder.append(color.code());
        return this;
    }

    /**
     * Appends text wrapped in a color or style code.
     *
     * <p>Null or empty text is ignored. Non-reset styles are followed by an explicit reset so the
     * style does not leak into later segments.</p>
     *
     * @param color color or style to apply
     * @param text text to append
     * @return this builder
     */
    public LogText append(LogColor color, String text) {
        if (text != null && !text.isEmpty()) {
            builder.append(color.code()).append(text);
            if (!color.isReset()) {
                builder.append(LogColor.RESET.code());
            }
        }
        return this;
    }

    /**
     * Appends an explicit ANSI reset code.
     *
     * @return this builder
     */
    public LogText reset() {
        builder.append(LogColor.RESET.code());
        return this;
    }

    /**
     * Renders the accumulated text and control codes without mutating the builder.
     *
     * @return current rendered text
     */
    public String build() {
        return builder.toString();
    }

    /**
     * Returns the same rendered value as {@link #build()}.
     *
     * @return current rendered text
     */
    @Override
    public String toString() {
        return build();
    }
}
