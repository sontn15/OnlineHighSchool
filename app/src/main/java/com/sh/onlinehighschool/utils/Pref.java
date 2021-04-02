package com.sh.onlinehighschool.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.sh.onlinehighschool.model.User;

public class Pref {

    private static final String PREF_NAME = "MY_PREFERENCES";
    private static final int PRIVATE_MODE = 0;

    public static final String USER = "USER";
    public static final String UID = "UID";
    public static final String ID_GV = "IDGV";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String AVATAR = "AVATAR";
    public static final String KHOI = "KHOI";

    public static final String YEAR = "YEAR";
    public static final String FACULTY = "FACULTY";

    private SharedPreferences preferences;

    public Pref(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }


    public void clearAllData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public void saveData(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void saveData(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getData(String key) {
        return preferences.getString(key, null);
    }

    public int getDataInt(String key) {
        return preferences.getInt(key, -1);
    }

    public static final String DEFAULT_YEAR = "Tất cả các khối";

    public String getYear() {
        return preferences.getString(YEAR, DEFAULT_YEAR);
    }

    public static final String DEFAULT_FACULTY = "Tất cả các ban";

    public String getFaculty() {
        return preferences.getString(FACULTY, DEFAULT_FACULTY);
    }

    public void putUser(String key, User customer) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String value = gson.toJson(customer);
        editor.putString(key, value);
        editor.apply();
    }

    public User getUser(String key) {
        Gson gson = new Gson();
        String json = preferences.getString(key, "");
        return gson.fromJson(json, User.class);
    }
}
