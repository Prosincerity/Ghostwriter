package com.prosincerity.ghostwriter.data

import android.database.sqlite.SQLiteDatabase
import com.prosincerity.ghostwriter.logic.AssonanceFormFilter
import com.prosincerity.ghostwriter.logic.CompoundRhymeFilter
import com.prosincerity.ghostwriter.logic.DictionaryHeadword
import com.prosincerity.ghostwriter.logic.IpaSearchKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

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
        val plan = DictionarySearchPlan.fromIpa(pronunciation?.ipa.orEmpty(), language, mode)
        val matches = linkedMapOf<String, DictionaryMatch>()
        fun visibleMatches(): List<DictionaryMatch> {
            val collected = matches.values.toList()
            val excluded = when (mode) {
                DictionarySearchMode.RHYME -> CompoundRhymeFilter.excludedWords(
                    collected.map { CompoundRhymeFilter.Entry(it.word, it.ipa) },
                    pronunciation?.ipa.orEmpty().map { CompoundRhymeFilter.Entry(normalized, it) },
                    language,
                )
                DictionarySearchMode.ASSONANCE -> AssonanceFormFilter.excludedWords(
                    collected.map { AssonanceFormFilter.Entry(it.word, it.ipa) }, language,
                )
                else -> emptySet()
            }
            return collected.filterNot { it.word in excluded }
        }
        fun enough(): Boolean = visibleMatches().size > nextPageIndex
        fun result(): DictionarySearchResult {
            val visible = visibleMatches()
            return DictionarySearchResult(
                pronunciation = pronunciation,
                matches = visible.drop(firstIndex.toInt()).take(PAGE_SIZE),
                hasNext = visible.size > nextPageIndex,
            )
        }

        // Every stage visits curated data first and closes each database before advancing.
        suspend fun scanSources(scan: suspend (SQLiteDatabase, PronunciationSource) -> Unit) {
            for (source in DictionarySource.entries) {
                coroutineContext.ensureActive()
                installer.openReadOnly(language, source)?.use { database ->
                    scan(database, source.pronunciationSource)
                }
                if (enough()) break
            }
        }

        if (mode == DictionarySearchMode.ASSONANCE) {
            scanSources { database, source ->
                for (key in plan.prefixes) {
                    collectRows(
                        database,
                        "SELECT word, ipa FROM dictionary WHERE assonance_reversed = ? " +
                            "ORDER BY word, ipa",
                        arrayOf(key.value), normalized, source, matches, ::enough,
                    ) { ipa -> IpaSearchKeys.fromIpa(ipa, language)?.assonance == key.value }
                    if (enough()) break
                }
            }
            if (enough()) return@withContext result()

            // Finish exact matches across both sources before appending shorter vowel tiers.
            // Rebuild the same ordered sequence before slicing each requested page.
            for (prefix in plan.assonanceFallbacks) {
                scanSources { database, source ->
                    database.execSQL("PRAGMA case_sensitive_like = ON")
                    collectRows(
                        database,
                        "SELECT word, ipa FROM dictionary WHERE assonance_reversed LIKE ? ESCAPE '!' " +
                            "ORDER BY assonance_reversed, word, ipa",
                        arrayOf("${escapeLike(prefix)}%"), normalized, source, matches, ::enough,
                    ) { ipa ->
                        val candidate = IpaSearchKeys.fromIpa(ipa, language)?.assonance
                        candidate == prefix || candidate?.startsWith("$prefix ") == true
                    }
                }
                if (enough()) break
            }
            return@withContext result()
        }
        if (mode == DictionarySearchMode.WORD_SUFFIX) {
            for (searchPrefix in plan.prefixes) {
                scanSources { database, source ->
                    database.execSQL("PRAGMA case_sensitive_like = ON")
                    val suffix = searchPrefix.tokens ?: return@scanSources
                    val pattern = suffix.asReversed().joinToString("%") { escapeLike(it) } + "%"
                    collectRows(
                        database,
                        "SELECT word, ipa FROM dictionary WHERE ipa_reversed LIKE ? ESCAPE '!' " +
                            "ORDER BY ipa_reversed, word, ipa",
                        arrayOf(pattern), normalized, source, matches, ::enough,
                    ) { ipa ->
                        val candidate = IpaSearchKeys.fromIpa(ipa, language) ?: return@collectRows false
                        IpaSearchKeys.phonemeTokens(candidate).takeLast(suffix.size) == suffix
                    }
                }
                if (enough()) break
            }
            return@withContext result()
        }
        scanSources { database, source ->
            database.execSQL("PRAGMA case_sensitive_like = ON")
            if (mode == DictionarySearchMode.WORD_PREFIX) {
                collectRows(
                    database,
                    "SELECT word, ipa FROM dictionary WHERE word LIKE ? ESCAPE '!' " +
                        "ORDER BY word, ipa",
                    arrayOf("${escapeLike(normalized)}%"), normalized, source, matches, ::enough,
                ) { true }
            } else {
                for (searchPrefix in plan.prefixes) {
                    val prefix = searchPrefix.value
                    val sql = "SELECT word, ipa FROM dictionary WHERE ipa_reversed LIKE ? ESCAPE '!' " +
                        "ORDER BY length(ipa_reversed), ipa_reversed, word, ipa"
                    collectRows(database, sql, arrayOf("${escapeLike(prefix)}%"),
                        normalized, source, matches, ::enough) { ipa ->
                        val candidateKeys = IpaSearchKeys.fromIpa(ipa, language) ?: return@collectRows false
                        IpaSearchKeys.rimeTokens(candidateKeys, language) == searchPrefix.tokens
                    }
                    if (enough()) break
                }
            }
        }
        result()
    }

    private suspend fun collectRows(
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
            val rows = database.readDictionaryRows("$sql LIMIT $SCAN_LIMIT OFFSET $offset", args) { cursor ->
                val candidate = cursor.getString(0)
                if (candidate != input && candidate !in matches) {
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
}
