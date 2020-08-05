package com.tranhaison.englishportugesedictionary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class DictionaryState {

    /**
     * Save the value of latest state
     * @param activity
     */
    public static void saveState(Activity activity, String key, String value) {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Return the value of latest state
     * @param activity
     * @param key
     * @return
     */
    public static String getState(Activity activity, String key) {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, null);
        return value;
    }
}
