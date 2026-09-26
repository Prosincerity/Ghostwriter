package com.prosincerity.ghostwriter.data

import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import com.prosincerity.ghostwriter.logic.DictionaryHeadword
import kotlin.coroutines.coroutineContext

internal enum class PronunciationSource { WIKTIONARY, ESPEAK_DATABASE, ESPEAK_GENERATED }

internal data class PronunciationResult(
    val ipa: List<String>,
    val source: PronunciationSource,
)

/** Reads the producer's documented schema without modifying the release files. */
internal class DictionaryPronunciations(
    private val installer: DictionaryInstaller,
    private val generator: IpaGenerator = EspeakIpa(installer.appContext),
) {
    suspend fun lookup(word: String, language: String): PronunciationResult? = withContext(Dispatchers.IO) {
        require(language in installer.release.languages)
        val normalized = DictionaryHeadword.normalizedEligible(word) ?: return@withContext null
        for (source in DictionarySource.entries) {
            coroutineContext.ensureActive()
            installer.openReadOnly(language, source)?.use { database ->
                pronunciations(database, normalized).takeIf { it.isNotEmpty() }?.let {
                    return@withContext PronunciationResult(it, source.pronunciationSource)
                }
            }
        }
        coroutineContext.ensureActive()
        generator.ipa(normalized, language)?.let {
            PronunciationResult(listOf(it), PronunciationSource.ESPEAK_GENERATED)
        }
    }

    private suspend fun pronunciations(database: SQLiteDatabase, word: String): List<String> = buildList {
        database.readDictionaryRows(
            "SELECT ipa FROM dictionary WHERE word = ? ORDER BY ipa",
            arrayOf(word),
        ) { cursor ->
            add(cursor.getString(0))
        }
    }
}
