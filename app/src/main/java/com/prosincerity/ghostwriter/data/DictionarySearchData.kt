package com.prosincerity.ghostwriter.data

/** Keeps search ordering and pagination independent of Android's SQLite implementation. */
internal interface DictionarySearchData {
    val languages: Set<String>
    suspend fun lookup(word: String, language: String): PronunciationResult?
    suspend fun withSource(language: String, source: DictionarySource, scan: suspend (DictionarySearchRows) -> Unit)
}

internal fun interface DictionarySearchRows {
    suspend fun read(sql: String, args: Array<String>, onRow: (String, String) -> Unit): Int
}

internal class InstalledDictionarySearchData(
    private val installer: DictionaryInstaller,
    private val pronunciations: DictionaryPronunciations,
) : DictionarySearchData {
    override val languages get() = installer.release.languages.keys

    override suspend fun lookup(word: String, language: String) = pronunciations.lookup(word, language)

    override suspend fun withSource(
        language: String,
        source: DictionarySource,
        scan: suspend (DictionarySearchRows) -> Unit,
    ) {
        installer.openReadOnly(language, source)?.use { database ->
            database.execSQL("PRAGMA case_sensitive_like = ON")
            scan(DictionarySearchRows { sql, args, onRow ->
                database.readDictionaryRows(sql, args) { cursor ->
                    onRow(cursor.getString(0), cursor.getString(1))
                }
            })
        }
    }
}
