package com.prosincerity.ghostwriter.data

import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream

internal fun <T> withDictionaryTestContext(block: (Context) -> T): T {
    val baseContext = InstrumentationRegistry.getInstrumentation().targetContext
    val root = File(baseContext.cacheDir, "dictionary-test-${System.nanoTime()}")
    val files = File(root, "files").apply { mkdirs() }
    val cache = File(root, "cache").apply { mkdirs() }
    val context = object : ContextWrapper(baseContext) {
        override fun getApplicationContext(): Context = this
        override fun getFilesDir(): File = files
        override fun getCacheDir(): File = cache
    }
    return try {
        block(context)
    } finally {
        root.deleteRecursively()
    }
}

internal fun dictionaryArchive(context: Context, entries: List<Pair<String, String>>): ByteArray {
    val file = File(context.cacheDir, "dictionary-fixture-${System.nanoTime()}.db")
    try {
        SQLiteDatabase.openOrCreateDatabase(file, null).use { database ->
            database.execSQL(
                "CREATE TABLE dictionary (word TEXT NOT NULL, ipa TEXT NOT NULL, " +
                    "ipa_reversed TEXT NOT NULL, assonance_reversed TEXT NOT NULL, " +
                    "PRIMARY KEY (word, ipa)) WITHOUT ROWID",
            )
            entries.forEach { (word, ipa) ->
                database.execSQL("INSERT INTO dictionary VALUES (?, ?, '', '')", arrayOf(word, ipa))
            }
        }
        return ByteArrayOutputStream().use { output ->
            GZIPOutputStream(output).use { gzip -> file.inputStream().use { it.copyTo(gzip) } }
            output.toByteArray()
        }
    } finally {
        file.delete()
    }
}
