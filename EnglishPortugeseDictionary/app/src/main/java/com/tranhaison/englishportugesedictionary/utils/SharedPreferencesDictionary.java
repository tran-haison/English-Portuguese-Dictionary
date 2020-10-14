package com.tranhaison.englishportugesedictionary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesDictionary {

    public static final String preference_name = "Dictionary";

    /**
     * Save the value of latest state
     * @param activity
     */
    public static void saveWord(Activity activity, String key, String value) {
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
    public static String getWord(Activity activity, String key) {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, null);
        return value;
    }

    /**
     * Save the last time in millis when user left the app
     * @param activity
     */
    public static void saveLastTimeOpen(Activity activity, String key, boolean value) {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Get the last time in millis when user left the app
     * @param activity
     * @param key
     * @return
     */
    public static boolean getLastTimeOpen(Activity activity, String key) {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(key, false);
        return value;
    }

    /**
     * If text translator models have been downloaded -> set value to true
     * else set value to false
     * @param activity
     * @param key
     * @param value
     */
    public static void saveModelDownloadedState(Activity activity, String key, boolean value) {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Return true if text translator models have been downloaded
     * else return false
     * @param activity
     * @param key
     * @return
     */
    public static boolean getModelDownloadedState(Activity activity, String key) {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(key, false);
        return value;
    }

    public static boolean checkKeyExist(Context contextGetKey, String key) {
        SharedPreferences sharedPreferences = contextGetKey.getSharedPreferences(preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.contains(key);

    }

    public static void removeKey(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

}
