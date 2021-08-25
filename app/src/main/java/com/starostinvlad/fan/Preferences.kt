package com.starostinvlad.fan

import android.content.Context
import android.content.SharedPreferences
import kotlin.Throws

class Preferences(context: Context) {
    private val preferences: SharedPreferences
    private val editor: SharedPreferences.Editor
        private get() = preferences.edit()
    var token: String?
        get() = preferences.getString(PREF_TOKEN, "")
        set(data) {
            editor.putString(PREF_TOKEN, data).commit()
        }
    var cookie: String?
        get() = preferences.getString(PREF_COOKIE, "")
        set(data) {
            editor.putString(PREF_COOKIE, data).commit()
        }

    companion object {
        const val FILE_NAME = "preferences"
        const val PREF_TOKEN = "token"
        const val PREF_COOKIE = "cookie"
    }

    init {
        preferences = context.getSharedPreferences(FILE_NAME, 0)
    }
}