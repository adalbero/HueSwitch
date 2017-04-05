package com.adalbero.app.hueswtich.common.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Adalbero on 05/04/2017.
 */

public class AppSettings {
    private static SharedPreferences getPreferences(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref;
    }

    public static boolean flagShowColor() { return false; }

    public static boolean flagShowBri(Context context) {
        SharedPreferences sharedPref = getPreferences(context);
        return sharedPref.getBoolean(SettingsActivity.PREF_BULB_COLOR, false);    }
}
