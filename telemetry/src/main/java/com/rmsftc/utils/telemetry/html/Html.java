package com.rmsftc.utils.telemetry.html;

/**
 * A tiny HTML string builder for dashboard telemetry: escaping plus the
 * handful of tags a robot log actually uses. Everything is static and
 * returns plain strings, ready for {@link AdvancedLogger#logHTML(String)}.
 */
public final class Html {

    private Html() {}

    /** Escapes {@code &}, {@code <}, and {@code >} for safe inclusion as text. */
    public static String escape(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    public static String bold(String text) {
        return "<b>" + escape(text) + "</b>";
    }

    public static String italic(String text) {
        return "<i>" + escape(text) + "</i>";
    }

    public static String br() {
        return "<br/>";
    }

    public static String hr() {
        return "<hr/>";
    }

    /** Escaped text followed by a line break. */
    public static String line(String text) {
        return escape(text) + br();
    }

    /** An unordered list of escaped items. */
    public static String list(String... items) {
        StringBuilder builder = new StringBuilder("<ul>");
        for (String item : items) {
            builder.append("<li>").append(escape(item)).append("</li>");
        }
        return builder.append("</ul>").toString();
    }
}
