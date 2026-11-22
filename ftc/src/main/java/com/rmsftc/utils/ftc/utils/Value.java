package com.rmsftc.utils.ftc.utils;

/**
 * How a value of type {@code T} is stored as a string (e.g. in
 * {@link Storage}'s SharedPreferences): {@link #format(Object)} turns it
 * into the stored string and {@link #parse(String)} reads it back.
 * Built-ins cover the common types; implement this interface for custom
 * types (poses, enums, ...).
 */
public interface Value<T> {

    String format(T value);

    T parse(String stored);

    Value<String> STRING = new Value<String>() {
        @Override
        public String format(String value) {
            return value;
        }

        @Override
        public String parse(String stored) {
            return stored;
        }
    };

    Value<Integer> INT = new Value<Integer>() {
        @Override
        public String format(Integer value) {
            return Integer.toString(value);
        }

        @Override
        public Integer parse(String stored) {
            return Integer.parseInt(stored);
        }
    };

    Value<Long> LONG = new Value<Long>() {
        @Override
        public String format(Long value) {
            return Long.toString(value);
        }

        @Override
        public Long parse(String stored) {
            return Long.parseLong(stored);
        }
    };

    Value<Double> DOUBLE = new Value<Double>() {
        @Override
        public String format(Double value) {
            return Double.toString(value);
        }

        @Override
        public Double parse(String stored) {
            return Double.parseDouble(stored);
        }
    };

    Value<Float> FLOAT = new Value<Float>() {
        @Override
        public String format(Float value) {
            return Float.toString(value);
        }

        @Override
        public Float parse(String stored) {
            return Float.parseFloat(stored);
        }
    };

    Value<Boolean> BOOLEAN = new Value<Boolean>() {
        @Override
        public String format(Boolean value) {
            return Boolean.toString(value);
        }

        @Override
        public Boolean parse(String stored) {
            return Boolean.parseBoolean(stored);
        }
    };
}
