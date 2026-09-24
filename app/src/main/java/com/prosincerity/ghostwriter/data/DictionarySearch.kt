package com.prosincerity.ghostwriter.data

import android.database.sqlite.SQLiteDatabase
import com.prosincerity.ghostwriter.logic.IpaSearchKeys
import com.prosincerity.ghostwriter.logic.DictionaryHeadword
import com.prosincerity.ghostwriter.logic.CompoundRhymeFilter
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
                val rime = IpaSearchKeys.rimeTokens(key, language)
                if (rime == null) emptyList()
                else listOf(SearchPrefix(rime.asReversed().joinToString(""), rime.size, rime))
            }
            DictionarySearchMode.WORD_SUFFIX -> keys.flatMap { key ->
                IpaSearchKeys.phonemeSuffixes(key).map { suffix ->
                    SearchPrefix(suffix.asReversed().joinToString(""), suffix.size, suffix)
                }
            }
            DictionarySearchMode.ASSONANCE -> keys.flatMap { key ->
                val variants = IpaSearchKeys.assonancePrefixes(key)
                variants.mapIndexed { index, prefix -> SearchPrefix(prefix, variants.size - index) }
            }
            DictionarySearchMode.WORD_PREFIX -> emptyList()
        }.sortedWith(compareByDescending<SearchPrefix> { it.tokenCount }.thenByDescending { it.value.length })
            .distinctBy { it.tokens ?: it.value }
        val matches = linkedMapOf<String, DictionaryMatch>()
        val collectionLimit = if (mode == DictionarySearchMode.RHYME) {
            CANDIDATE_LIMIT
        } else RESULT_LIMIT
        fun result(): DictionarySearchResult {
            val collected = matches.values.toList()
            val excluded = if (mode == DictionarySearchMode.RHYME) {
                CompoundRhymeFilter.excludedWords(
                    collected.map { CompoundRhymeFilter.Entry(it.word, it.ipa) },
                    pronunciation?.ipa.orEmpty().map { CompoundRhymeFilter.Entry(normalized, it) },
                    language,
                )
            } else emptySet()
            return DictionarySearchResult(pronunciation,
                collected.filterNot { it.word in excluded }.take(RESULT_LIMIT))
        }
        val sources = listOf(
            DictionarySource.WIKTIONARY to PronunciationSource.WIKTIONARY,
            DictionarySource.ESPEAK to PronunciationSource.ESPEAK_DATABASE,
        )
        if (mode == DictionarySearchMode.WORD_SUFFIX) {
            for (searchPrefix in prefixes) {
                for ((source, label) in sources) {
                    installer.openReadOnly(language, source)?.use { database ->
                        database.execSQL("PRAGMA case_sensitive_like = ON")
                        collectSuffix(database, searchPrefix, normalized, language, label, matches, collectionLimit)
                    }
                    if (matches.size >= collectionLimit) break
                }
                if (matches.size >= collectionLimit) break
            }
            return@withContext result()
        }
        for ((source, label) in sources) {
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
                                normalized, language, mode, searchPrefix, label, matches, collectionLimit)
                            if (matches.size >= collectionLimit) break
                        }
                    }
                    DictionarySearchMode.WORD_PREFIX -> {
                        collect(
                            database,
                            "SELECT word, ipa FROM dictionary WHERE word LIKE ? ESCAPE '!' " +
                                "ORDER BY word, ipa LIMIT $SCAN_LIMIT",
                            arrayOf("${escapeLike(normalized)}%"), normalized, language, mode, null, label, matches,
                            collectionLimit,
                        )
                    }
                    DictionarySearchMode.WORD_SUFFIX -> Unit
                }
            }
            if (matches.size >= collectionLimit) break
        }
        result()
    }

    private fun collect(
        database: SQLiteDatabase,
        sql: String,
        args: Array<String>,
        input: String,
        language: String,
        mode: DictionarySearchMode,
        searchPrefix: SearchPrefix?,
        source: PronunciationSource,
        matches: MutableMap<String, DictionaryMatch>,
        limit: Int,
    ) {
        database.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext() && matches.size < limit) {
                val candidate = cursor.getString(0)
                if (candidate == input || candidate in matches) continue
                val ipa = cursor.getString(1)
                if (mode == DictionarySearchMode.RHYME || mode == DictionarySearchMode.ASSONANCE) {
                    val keys = IpaSearchKeys.fromIpa(ipa, language) ?: continue
                    val valid = if (mode == DictionarySearchMode.RHYME) {
                        IpaSearchKeys.rimeTokens(keys, language) == searchPrefix?.tokens
                    } else {
                        IpaSearchKeys.assonancePrefixes(keys).any { it == searchPrefix?.value }
                    }
                    if (!valid) continue
                }
                matches[candidate] = DictionaryMatch(candidate, ipa, source)
            }
        }
    }

    private fun collectSuffix(
        database: SQLiteDatabase,
        searchPrefix: SearchPrefix,
        input: String,
        language: String,
        source: PronunciationSource,
        matches: MutableMap<String, DictionaryMatch>,
        limit: Int,
    ) {
        val suffix = searchPrefix.tokens ?: return
        val pattern = suffix.asReversed().joinToString("%") { escapeLike(it) } + "%"
        var offset = 0
        while (matches.size < limit) {
            var rows = 0
            database.rawQuery(
                "SELECT word, ipa FROM dictionary WHERE ipa_reversed LIKE ? ESCAPE '!' " +
                    "ORDER BY ipa_reversed, word LIMIT $SCAN_LIMIT OFFSET $offset",
                arrayOf(pattern),
            ).use { cursor ->
                while (cursor.moveToNext() && matches.size < limit) {
                    rows++
                    val word = cursor.getString(0)
                    if (word == input || word in matches) continue
                    val ipa = cursor.getString(1)
                    val candidate = IpaSearchKeys.fromIpa(ipa, language) ?: continue
                    val candidatePhonemes = IpaSearchKeys.phonemeTokens(candidate)
                    if (candidatePhonemes.size < suffix.size || candidatePhonemes.takeLast(suffix.size) != suffix) continue
                    matches[word] = DictionaryMatch(word, ipa, source)
                }
            }
            if (rows < SCAN_LIMIT) break
            offset += rows
        }
    }

    private companion object {
        const val RESULT_LIMIT = 60
        const val CANDIDATE_LIMIT = RESULT_LIMIT * 3
        const val SCAN_LIMIT = 200

        fun escapeLike(value: String): String = buildString {
            value.forEach { character ->
                if (character == '!' || character == '%' || character == '_') append('!')
                append(character)
            }
        }
    }

    private data class SearchPrefix(val value: String, val tokenCount: Int, val tokens: List<String>? = null)
}
