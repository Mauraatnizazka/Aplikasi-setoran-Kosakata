package com.example.setorankosakata

import android.content.Context
import android.content.SharedPreferences

/**
 * Membungkus SharedPreferences supaya Activity/Fragment tidak perlu tahu
 * nama file maupun key-nya. Hanya menyimpan PENGATURAN kecil (key-value):
 *  - mode gelap/terang
 *  - target setoran kosakata per hari
 *  - bahasa terakhir yang dipakai saat menyetor
 * Data kosakata tetap disimpan di database Room.
 */
class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isDarkMode(): Boolean = prefs.getBoolean(KEY_DARK_MODE, false)

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun getDailyTarget(): Int =
        prefs.getInt(KEY_DAILY_TARGET, DEFAULT_TARGET).coerceIn(MIN_TARGET, MAX_TARGET)

    fun setDailyTarget(target: Int) {
        prefs.edit().putInt(KEY_DAILY_TARGET, target.coerceIn(MIN_TARGET, MAX_TARGET)).apply()
    }

    fun getLastLanguage(): String =
        prefs.getString(KEY_LAST_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE

    fun setLastLanguage(language: String) {
        prefs.edit().putString(KEY_LAST_LANGUAGE, language).apply()
    }

    companion object {
        const val PREFS_NAME = "setoran_settings"
        const val KEY_DARK_MODE = "dark_mode"
        const val KEY_DAILY_TARGET = "daily_target"
        const val KEY_LAST_LANGUAGE = "last_language"

        const val DEFAULT_LANGUAGE = "Inggris"
        const val DEFAULT_TARGET = 5
        const val MIN_TARGET = 1
        const val MAX_TARGET = 30
    }
}
