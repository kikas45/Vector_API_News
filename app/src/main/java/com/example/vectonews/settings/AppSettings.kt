package com.example.vectonews.settings

import android.content.Context
import android.content.SharedPreferences

class AppSettings(context: Context) {
    private val prefs: SharedPreferences
    private var theme: Int

    init {
        prefs = context.applicationContext.getSharedPreferences("prefApp", Context.MODE_PRIVATE)
        theme = prefs.getInt(THEME_KEY, 0) // default theme is 0 = Light Theme index
    }

    fun getTheme(): Int {
        return theme
    }

    fun setTheme(theme: Int) {
        this.theme = theme
        val editor = prefs.edit()
        editor.putInt(THEME_KEY, theme)
        editor.apply()
    }

    companion object {
        const val THEME_KEY = "theme"
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
    }
}
