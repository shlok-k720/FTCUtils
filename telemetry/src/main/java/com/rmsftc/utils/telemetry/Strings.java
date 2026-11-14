package com.rmsftc.utils.telemetry;

import java.util.Locale;

public final class Strings {

    private Strings() {}

    public static String empty() {
        return "";
    }

    public static String newline() {
        return "\n";
    }

    public static String space() {
        return " ";
    }

    public static String indent() {
        return "\t";
    }

    public static String rowOfDashes() {
        return "--------"; // We don't put a lot of dashes - mainly because they can overflow.
    }

    public static String combine(String... strings) {
        return String.join(empty(), strings);
    }

    public static StringBuilder builder() {
        return new StringBuilder();
    }

    public static String format(String format, Object... args) {
        return String.format(Locale.US, format, args);
    }

}
