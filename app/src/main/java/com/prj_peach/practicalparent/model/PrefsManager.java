package com.prj_peach.practicalparent.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Wrapper class for saving and loading data from shared prefs
 * Only provides methods for strings since we will be saving data as JSON
 */

public class PrefsManager {

    public static void saveString(Context context, String key, String value) {
        if (key == null || key == "" ||
            value == null || value == "")
        {
            throw new IllegalArgumentException("Provided key or value is empty or null");
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value);
        editor.apply();
    }

    public static String loadString(Context context, String key) {
        if (key == null || key == "") {
            throw new IllegalArgumentException("Provided key is null or empty");
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString(key, "");
        return value;
    }
}
