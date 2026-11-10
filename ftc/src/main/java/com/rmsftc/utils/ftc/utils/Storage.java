package com.rmsftc.utils.ftc.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

public class Storage {
    public static Storage instance = c();
    public static Storage c() {
        return new Storage();
    }
    private SharedPreferences sharedPreferences;

    public Storage(Context appContext) {
        this.sharedPreferences = appContext.getSharedPreferences("Storage", Context.MODE_PRIVATE);
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
    // todo = create a generic Value interface for getting from String and putting String - for this
    
}
