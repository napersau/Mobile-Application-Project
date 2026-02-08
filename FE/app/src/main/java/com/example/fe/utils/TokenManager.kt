package com.example.fe.utils

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_TOKEN = "token"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        getPreferences(context).edit().apply {
            putString(KEY_TOKEN, token)
            apply()
        }
    }

    fun getToken(context: Context): String? {
        return getPreferences(context).getString(KEY_TOKEN, null)
    }

    fun clearTokens(context: Context) {
        getPreferences(context).edit().clear().apply()
    }

    fun hasToken(context: Context): Boolean {
        return getToken(context) != null
    }
}
