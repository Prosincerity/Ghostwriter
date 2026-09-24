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
    val hasNext: Boolean = false,
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
        page: Int = 0,
    ): DictionarySearchResult = withContext(Dispatchers.IO) {
        require(page >= 0)
        require(language in installer.release.languages)
        val normalized = DictionaryHeadword.normalize(word.trim())
        if (normalized.isEmpty()) return@withContext DictionarySearchResult(null, emptyList())
        val firstIndex = page.toLong() * PAGE_SIZE
        require(firstIndex < Int.MAX_VALUE - PAGE_SIZE)
        val nextPageIndex = (firstIndex + PAGE_SIZE).toInt()
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
        fun visibleMatches(): List<DictionaryMatch> {
            val collected = matches.values.toList()
            val excluded = if (mode == DictionarySearchMode.RHYME) {
                CompoundRhymeFilter.excludedWords(
                    collected.map { CompoundRhymeFilter.Entry(it.word, it.ipa) },
                    pronunciation?.ipa.orEmpty().map { CompoundRhymeFilter.Entry(normalized, it) },
                    language,
                )
            } else emptySet()
            return collected.filterNot { it.word in excluded }
        }
        fun enough(): Boolean = visibleMatches().size > nextPageIndex
        fun result(): DictionarySearchResult {
            val visible = visibleMatches()
            return DictionarySearchResult(pronunciation,
                visible.drop(firstIndex.toInt()).take(PAGE_SIZE), visible.size > nextPageIndex)
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
                        val suffix = searchPrefix.tokens ?: return@use
                        val pattern = suffix.asReversed().joinToString("%") { escapeLike(it) } + "%"
                        collectRows(
                            database,
                            "SELECT word, ipa FROM dictionary WHERE ipa_reversed LIKE ? ESCAPE '!' " +
                                "ORDER BY ipa_reversed, word, ipa",
                            arrayOf(pattern), normalized, label, matches, ::enough,
                        ) { ipa ->
                            val candidate = IpaSearchKeys.fromIpa(ipa, language) ?: return@collectRows false
                            IpaSearchKeys.phonemeTokens(candidate).takeLast(suffix.size) == suffix
                        }
                    }
                    if (enough()) break
                }
                if (enough()) break
            }
            return@withContext result()
        }
        for ((source, label) in sources) {
            installer.openReadOnly(language, source)?.use { database ->
                database.execSQL("PRAGMA case_sensitive_like = ON")
                if (mode == DictionarySearchMode.WORD_PREFIX) {
                    collectRows(
                        database,
                        "SELECT word, ipa FROM dictionary WHERE word LIKE ? ESCAPE '!' " +
                            "ORDER BY word, ipa",
                        arrayOf("${escapeLike(normalized)}%"), normalized, label, matches, ::enough,
                    ) { true }
                } else {
                    for (searchPrefix in prefixes) {
                        val prefix = searchPrefix.value
                        val column = if (mode == DictionarySearchMode.RHYME) "ipa_reversed" else "assonance_reversed"
                        val order = if (mode == DictionarySearchMode.RHYME) {
                            "length(ipa_reversed), ipa_reversed, word, ipa"
                        } else "$column, word, ipa"
                        val sql = "SELECT word, ipa FROM dictionary WHERE $column LIKE ? ESCAPE '!' " +
                            "ORDER BY $order"
                        collectRows(database, sql, arrayOf("${escapeLike(prefix)}%"),
                            normalized, label, matches, ::enough) { ipa ->
                            val keys = IpaSearchKeys.fromIpa(ipa, language) ?: return@collectRows false
                            if (mode == DictionarySearchMode.RHYME) {
                                IpaSearchKeys.rimeTokens(keys, language) == searchPrefix.tokens
                            } else {
                                IpaSearchKeys.assonancePrefixes(keys).contains(searchPrefix.value)
                            }
                        }
                        if (enough()) break
                    }
                }
            }
            if (enough()) break
        }
        result()
    }

    private fun collectRows(
        database: SQLiteDatabase,
        sql: String,
        args: Array<String>,
        input: String,
        source: PronunciationSource,
        matches: MutableMap<String, DictionaryMatch>,
        enough: () -> Boolean,
        validIpa: (String) -> Boolean,
    ) {
        var offset = 0
        while (!enough()) {
            var rows = 0
            database.rawQuery("$sql LIMIT $SCAN_LIMIT OFFSET $offset", args).use { cursor ->
                while (cursor.moveToNext()) {
                    rows++
                    val candidate = cursor.getString(0)
                    if (candidate == input || candidate in matches) continue
                    val ipa = cursor.getString(1)
                    if (validIpa(ipa)) matches[candidate] = DictionaryMatch(candidate, ipa, source)
                }
            }
            if (rows < SCAN_LIMIT) break
            offset += rows
        }
    }

    private companion object {
        const val PAGE_SIZE = 60
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
