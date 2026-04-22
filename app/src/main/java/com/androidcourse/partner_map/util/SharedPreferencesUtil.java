package com.androidcourse.partner_map.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidcourse.partner_map.app.Constants;
import com.google.gson.Gson;

public class SharedPreferencesUtil {
    private static SharedPreferencesUtil instance;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    private SharedPreferencesUtil(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesUtil(context);
        }
        return instance;
    }

    public void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String def) {
        return prefs.getString(key, def);
    }

    public void putInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int def) {
        return prefs.getInt(key, def);
    }

    public void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean def) {
        return prefs.getBoolean(key, def);
    }

    public void putObject(String key, Object obj) {
        prefs.edit().putString(key, gson.toJson(obj)).apply();
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String json = prefs.getString(key, null);
        if (json == null) return null;
        return gson.fromJson(json, clazz);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    public void remove(String key) {
        prefs.edit().remove(key).apply();
    }
}
