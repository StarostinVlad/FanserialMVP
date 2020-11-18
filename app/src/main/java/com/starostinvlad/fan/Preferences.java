package com.starostinvlad.fan;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    final static String FILE_NAME = "preferences";

    final static String PREF_TOKEN = "token";
    final static String PREF_COOKIE = "cookie";

    private SharedPreferences preferences;

    public Preferences(Context context) {
        preferences = context.getSharedPreferences(FILE_NAME, 0);
    }

    private SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

    public String getToken() {
        return preferences.getString(PREF_TOKEN, "");
    }

    public void setToken(String data) {
        getEditor().putString(PREF_TOKEN, data).commit();
    }

    public String getCookie() {
        return preferences.getString(PREF_COOKIE, "");
    }

    public void setCookie(String data) {
        getEditor().putString(PREF_COOKIE, data).commit();
    }
}