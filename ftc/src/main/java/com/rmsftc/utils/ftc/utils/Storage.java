package com.rmsftc.utils.ftc.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

/**
 * Persistent key-value storage backed by SharedPreferences. Values are
 * stored as strings; typed access goes through {@link Value} (built-ins
 * for the common types, custom implementations for anything else).
 */
public class Storage {
    public static Storage instance = c();
    public static Storage c() {
        return new Storage();
    }
    private SharedPreferences sharedPreferences;

    public Storage(Context appContext) {
        this.sharedPreferences = appContext.getSharedPreferences("comRmsFtcFTCUtilsStorageManager", Context.MODE_PRIVATE);
    }

    public Storage() {
        this(AppUtil.getInstance().getApplication().getApplicationContext());
    }


    public Storage(HardwareMap hardwareMap) {this(hardwareMap.appContext);}

    public void setPref(String key, Object value) {
        sharedPreferences.edit()
                .putString(key, value.toString())
                .apply();
    }

    /** Stores {@code value} under {@code key}, formatted by {@code type}. */
    public <T> void set(String key, Value<T> type, T value) {
        sharedPreferences.edit()
                .putString(key, type.format(value))
                .apply();
    }

    /**
     * Reads {@code key} parsed by {@code type}, or {@code defaultValue}
     * when the key is absent or its stored string fails to parse.
     */
    public <T> T get(String key, Value<T> type, T defaultValue) {
        String stored = sharedPreferences.getString(key, null);
        if (stored == null) {
            return defaultValue;
        }
        try {
            return type.parse(stored);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    public String getString(String key, String defaultValue) {
        return get(key, Value.STRING, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return get(key, Value.INT, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return get(key, Value.LONG, defaultValue);
    }

    public double getDouble(String key, double defaultValue) {
        return get(key, Value.DOUBLE, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return get(key, Value.FLOAT, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return get(key, Value.BOOLEAN, defaultValue);
    }

    public boolean has(String key) {
        return sharedPreferences.contains(key);
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }
}
