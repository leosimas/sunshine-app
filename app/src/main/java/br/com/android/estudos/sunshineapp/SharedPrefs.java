package br.com.android.estudos.sunshineapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Dustin on 25/08/2016.
 */
public class SharedPrefs {

    public static String getLocationPreference(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString( context.getString(R.string.pref_location_key), context.getString(R.string.pref_location_detault));
    }
}
