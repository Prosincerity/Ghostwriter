package com.prosincerity.ghostwriter.data

import android.content.Context
import androidx.core.content.edit

/**
 * Thin wrapper around SharedPreferences for the app's settings.
 *
 * Deliberately not using DataStore — for these small settings,
 * plain SharedPreferences (already part of the Android framework) does
 * the job with zero new dependencies.
 */
object Settings {
    private const val PREFS_NAME = "ghostwriter_settings"
    private const val KEY_PERSISTENT_LYRICS_FOLDER = "persistent_lyrics_folder"
    private const val KEY_AUTOSAVE_INTERVAL_SECONDS = "autosave_interval_seconds"
    private const val KEY_AUTOSAVE_COUNT = "autosave_count"
    private const val KEY_LYRIC_FONT_FAMILY = "lyric_font_family"
    private const val KEY_LYRIC_FONT_SIZE = "lyric_font_size_sp"
    private const val KEY_LYRIC_LINE_HEIGHT = "lyric_line_height_multiplier"
    private const val KEY_LYRIC_LETTER_SPACING = "lyric_letter_spacing_sp"
    private const val KEY_LYRIC_ALIGNMENT = "lyric_text_alignment"

    const val DEFAULT_INTERVAL_SECONDS = 60
    const val DEFAULT_AUTOSAVE_COUNT = 3

    /** Selectable autosave intervals, in seconds. */
    val INTERVAL_OPTIONS_SECONDS = listOf(10, 30, 60, 120, 300)

    /** Selectable number of rolling autosave backups to keep. */
    val COUNT_OPTIONS = listOf(3, 4, 5)

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getPersistentLyricsFolder(context: Context): String? =
        prefs(context).getString(KEY_PERSISTENT_LYRICS_FOLDER, null)

    fun setPersistentLyricsFolder(context: Context, uri: String?): Boolean =
        prefs(context).edit().putString(KEY_PERSISTENT_LYRICS_FOLDER, uri).commit()

    fun getAutosaveIntervalSeconds(context: Context): Int =
        prefs(context).getInt(KEY_AUTOSAVE_INTERVAL_SECONDS, DEFAULT_INTERVAL_SECONDS)

    fun setAutosaveIntervalSeconds(context: Context, seconds: Int) {
        prefs(context).edit { putInt(KEY_AUTOSAVE_INTERVAL_SECONDS, seconds) }
    }

    fun getAutosaveCount(context: Context): Int =
        prefs(context).getInt(KEY_AUTOSAVE_COUNT, DEFAULT_AUTOSAVE_COUNT)

    fun setAutosaveCount(context: Context, count: Int) {
        prefs(context).edit { putInt(KEY_AUTOSAVE_COUNT, count) }
    }

    fun getLyricTextSettings(context: Context): LyricTextSettings {
        val preferences = prefs(context)
        val defaults = LyricTextSettings()
        return LyricTextSettings(
            fontFamily = lyricFontFromPreference(preferences.getString(KEY_LYRIC_FONT_FAMILY, null)),
            fontSizeSp = preferences.getInt(KEY_LYRIC_FONT_SIZE, defaults.fontSizeSp),
            lineHeightMultiplier = preferences.getFloat(KEY_LYRIC_LINE_HEIGHT, defaults.lineHeightMultiplier),
            letterSpacingSp = preferences.getFloat(KEY_LYRIC_LETTER_SPACING, defaults.letterSpacingSp),
            alignment = LyricTextAlignment.entries.firstOrNull {
                it.name == preferences.getString(KEY_LYRIC_ALIGNMENT, null)
            } ?: defaults.alignment,
        ).normalized()
    }

    fun setLyricTextSettings(context: Context, settings: LyricTextSettings) {
        val normalized = settings.normalized()
        prefs(context).edit {
            putString(KEY_LYRIC_FONT_FAMILY, normalized.fontFamily.toPreferenceValue())
            putInt(KEY_LYRIC_FONT_SIZE, normalized.fontSizeSp)
            putFloat(KEY_LYRIC_LINE_HEIGHT, normalized.lineHeightMultiplier)
            putFloat(KEY_LYRIC_LETTER_SPACING, normalized.letterSpacingSp)
            putString(KEY_LYRIC_ALIGNMENT, normalized.alignment.name)
        }
    }
}
