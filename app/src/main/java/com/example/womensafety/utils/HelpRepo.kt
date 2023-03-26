package com.example.womensafety.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.womensafety.helpers.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HelpRepo @Inject constructor(@ApplicationContext private val context: Context) {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    fun initSharedPreferences() {
        sharedPreferences = context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun setSharedPreferences(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun getSharedPreferences(key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }

    fun clearSharedPreferences() {
        editor.clear().apply()
    }
    fun deleteSharedPreferences(key: String) {
        editor.remove(key).apply()
    }
}
