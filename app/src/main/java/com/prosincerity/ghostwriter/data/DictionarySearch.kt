package com.prosincerity.ghostwriter.data

import android.database.sqlite.SQLiteDatabase
import com.prosincerity.ghostwriter.logic.IpaSearchKeys
import com.prosincerity.ghostwriter.logic.DictionaryHeadword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal enum class DictionarySearchMode { RHYME, WORD_PREFIX, WORD_SUFFIX, ASSONANCE }

internal data class DictionaryMatch(
    val word: String,
    val ipa: String,
    val source: PronunciationSource,
)

internal data class DictionarySearchResult(
    val pronunciation: PronunciationResult?,
    val matches: List<DictionaryMatch>,
)

/** Reads the producer's existing indexes; no database is modified. */
internal class DictionarySearch(
    private val installer: DictionaryInstaller,
    private val pronunciations: DictionaryPronunciations = DictionaryPronunciations(installer),
) {
    suspend fun search(
        word: String,
        language: String,
        mode: DictionarySearchMode,
    ): DictionarySearchResult = withContext(Dispatchers.IO) {
        require(language in installer.release.languages)
        val normalized = DictionaryHeadword.normalize(word.trim())
        if (normalized.isEmpty()) return@withContext DictionarySearchResult(null, emptyList())
        val pronunciation = pronunciations.lookup(normalized, language)
        val keys = pronunciation?.ipa.orEmpty().mapNotNull { IpaSearchKeys.fromIpa(it, language) }
        val prefixes = when (mode) {
            DictionarySearchMode.RHYME -> keys.flatMap { key ->
                IpaSearchKeys.rhymePrefixes(key, language).mapIndexed { index, prefix ->
                    SearchPrefix(prefix, key.tokens.size - index)
                }
            }
            DictionarySearchMode.ASSONANCE -> keys.flatMap { key ->
                val variants = IpaSearchKeys.assonancePrefixes(key)
                variants.mapIndexed { index, prefix -> SearchPrefix(prefix, variants.size - index) }
            }
            else -> emptyList()
        }.sortedWith(compareByDescending<SearchPrefix> { it.tokenCount }.thenByDescending { it.value.length })
            .distinctBy { it.value }
        val matches = linkedMapOf<String, DictionaryMatch>()
        for ((source, label) in listOf(
            DictionarySource.WIKTIONARY to PronunciationSource.WIKTIONARY,
            DictionarySource.ESPEAK to PronunciationSource.ESPEAK_DATABASE,
        )) {
            installer.openReadOnly(language, source)?.use { database ->
                database.execSQL("PRAGMA case_sensitive_like = ON")
                when (mode) {
                    DictionarySearchMode.RHYME, DictionarySearchMode.ASSONANCE -> {
                        for (searchPrefix in prefixes) {
                            val prefix = searchPrefix.value
                            val column = if (mode == DictionarySearchMode.RHYME) "ipa_reversed" else "assonance_reversed"
                            val sql = "SELECT word, ipa FROM dictionary WHERE $column LIKE ? ESCAPE '!' " +
                                "ORDER BY $column, word LIMIT $SCAN_LIMIT"
                            collect(database, sql, arrayOf("${escapeLike(prefix)}%"),
                                normalized, language, mode, prefix, label, matches)
                            if (matches.size >= RESULT_LIMIT) break
                        }
                    }
                    DictionarySearchMode.WORD_PREFIX, DictionarySearchMode.WORD_SUFFIX -> {
                        val pattern = if (mode == DictionarySearchMode.WORD_PREFIX) {
                            "${escapeLike(normalized)}%"
                        } else {
                            "%${escapeLike(normalized)}"
                        }
                        collect(
                            database,
                            "SELECT word, ipa FROM dictionary WHERE word LIKE ? ESCAPE '!' " +
                                "ORDER BY word, ipa LIMIT $SCAN_LIMIT",
                            arrayOf(pattern), normalized, language, mode, "", label, matches,
                        )
                    }
                }
            }
            if (matches.size >= RESULT_LIMIT) break
        }
        DictionarySearchResult(pronunciation, matches.values.take(RESULT_LIMIT))
    }

    private fun collect(
        database: SQLiteDatabase,
        sql: String,
        args: Array<String>,
        input: String,
        language: String,
        mode: DictionarySearchMode,
        prefix: String,
        source: PronunciationSource,
        matches: MutableMap<String, DictionaryMatch>,
    ) {
        database.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext() && matches.size < RESULT_LIMIT) {
                val candidate = cursor.getString(0)
                if (candidate == input || candidate in matches) continue
                val ipa = cursor.getString(1)
                if (mode == DictionarySearchMode.RHYME || mode == DictionarySearchMode.ASSONANCE) {
                    val keys = IpaSearchKeys.fromIpa(ipa, language) ?: continue
                    val valid = if (mode == DictionarySearchMode.RHYME) {
                        IpaSearchKeys.rhymePrefixes(keys, language).any { it == prefix }
                    } else {
                        IpaSearchKeys.assonancePrefixes(keys).any { it == prefix }
                    }
                    if (!valid) continue
                }
                matches[candidate] = DictionaryMatch(candidate, ipa, source)
            }
        }
    }

    private companion object {
        const val RESULT_LIMIT = 60
        const val SCAN_LIMIT = 200

        fun escapeLike(value: String): String = buildString {
            value.forEach { character ->
                if (character == '!' || character == '%' || character == '_') append('!')
                append(character)
            }
        }
    }

    private data class SearchPrefix(val value: String, val tokenCount: Int)
}
