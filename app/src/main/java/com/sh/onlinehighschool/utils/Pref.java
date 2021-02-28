package com.sh.onlinehighschool.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Pref {

    private static final String PREF_NAME = "MY_PREFERENCES";
    private static final int PRIVATE_MODE = 0;

    public static final String UID = "UID";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String AVATAR = "AVATAR";

    public static final String YEAR = "YEAR";
    public static final String FACULTY = "FACULTY";

    private SharedPreferences preferences;

    public Pref(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public void saveData(String key, int value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void saveData(String key, String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getData(String key){
        return preferences.getString(key, null);
    }

    public static final String DEFAULT_YEAR = "Tất cả các năm";
    public String getYear(){
        return preferences.getString(YEAR, DEFAULT_YEAR);
    }

    public static final String DEFAULT_FACULTY = "Tất cả các khoa";
    //public static final String DEFAULT_FACULTY = "Công nghệ thông tin";
    public String getFaculty(){
        return preferences.getString(FACULTY, DEFAULT_FACULTY);
    }
}
