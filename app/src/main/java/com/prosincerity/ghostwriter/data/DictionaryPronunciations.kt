package com.prosincerity.ghostwriter.data

import android.database.sqlite.SQLiteDatabase
import java.text.Normalizer

internal enum class PronunciationSource { WIKTIONARY, ESPEAK_DATABASE }

internal data class PronunciationResult(
    val ipa: List<String>,
    val source: PronunciationSource,
)

/** Reads the producer's documented schema without modifying the release files. */
internal class DictionaryPronunciations(private val installer: DictionaryInstaller) {
    fun lookup(word: String, language: String): PronunciationResult? {
        require(language in installer.release.languages)
        val normalized = Normalizer.normalize(word, Normalizer.Form.NFC)
        if (normalized.isBlank()) return null
        for ((source, resultSource) in listOf(
            DictionarySource.WIKTIONARY to PronunciationSource.WIKTIONARY,
            DictionarySource.ESPEAK to PronunciationSource.ESPEAK_DATABASE,
        )) {
            installer.openReadOnly(language, source)?.use { database ->
                pronunciations(database, normalized).takeIf { it.isNotEmpty() }?.let {
                    return PronunciationResult(it, resultSource)
                }
            }
        }
        return null
    }

    private fun pronunciations(database: SQLiteDatabase, word: String): List<String> =
        database.rawQuery(
            "SELECT ipa FROM dictionary WHERE word = ? ORDER BY ipa",
            arrayOf(word),
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(cursor.getString(0))
            }
        }
}
