package com.example.storyapp.preferences

import android.content.Context

internal class AuthPreference(context: Context) {

    companion object{
        private const val PREFS_NAME = "auth"
        private const val KEY = "key"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setKey(value: String){
        val editor = preferences.edit()
        editor.putString(KEY, value)
        editor.apply()
    }

    fun getKey(): String?{
        return preferences.getString(KEY, "")
    }

    fun clear(){
        preferences.edit().clear().apply()
    }
}