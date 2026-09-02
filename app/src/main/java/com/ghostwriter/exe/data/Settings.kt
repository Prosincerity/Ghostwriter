package com.ghostwriter.exe.data

import android.content.Context

/**
 * Thin wrapper around SharedPreferences for the app's settings.
 *
 * Deliberately not using DataStore — for a couple of small int settings,
 * plain SharedPreferences (already part of the Android framework) does
 * the job with zero new dependencies.
 */
object Settings {
    private const val PREFS_NAME = "ghostwriter_settings"
    private const val KEY_AUTOSAVE_INTERVAL_SECONDS = "autosave_interval_seconds"
    private const val KEY_AUTOSAVE_COUNT = "autosave_count"

    const val DEFAULT_INTERVAL_SECONDS = 60
    const val DEFAULT_AUTOSAVE_COUNT = 3

    /** Selectable autosave intervals, in seconds. */
    val INTERVAL_OPTIONS_SECONDS = listOf(10, 30, 60, 120, 300)

    /** Selectable number of rolling autosave backups to keep. */
    val COUNT_OPTIONS = listOf(3, 4, 5)

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAutosaveIntervalSeconds(context: Context): Int =
        prefs(context).getInt(KEY_AUTOSAVE_INTERVAL_SECONDS, DEFAULT_INTERVAL_SECONDS)

    fun setAutosaveIntervalSeconds(context: Context, seconds: Int) {
        prefs(context).edit().putInt(KEY_AUTOSAVE_INTERVAL_SECONDS, seconds).apply()
    }

    fun getAutosaveCount(context: Context): Int =
        prefs(context).getInt(KEY_AUTOSAVE_COUNT, DEFAULT_AUTOSAVE_COUNT)

    fun setAutosaveCount(context: Context, count: Int) {
        prefs(context).edit().putInt(KEY_AUTOSAVE_COUNT, count).apply()
    }
}
