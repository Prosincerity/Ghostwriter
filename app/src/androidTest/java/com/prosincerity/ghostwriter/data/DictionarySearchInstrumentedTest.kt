package com.prosincerity.ghostwriter.data

import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class DictionarySearchInstrumentedTest {
    @Test
    fun searchesRhymeAssonanceAndWordsWithSourcePrecedence() = withDictionaryFixture { installer ->
        val pronunciations = DictionaryPronunciations(installer, IpaGenerator { _, _ -> null })
        val search = DictionarySearch(installer, pronunciations)

        val rhymes = runBlocking { search.search("cat", "en", DictionarySearchMode.RHYME) }
        assertEquals(PronunciationSource.WIKTIONARY, rhymes.pronunciation?.source)
        assertEquals(listOf("kat", "cot", "bat", "flat", "mat"), rhymes.matches.map { it.word })
        assertEquals(PronunciationSource.WIKTIONARY, rhymes.matches.first().source)
        assertEquals(2, rhymes.pronunciation?.ipa?.size)
        assertFalse(rhymes.matches.any { it.word == "cat" })

        val writing = runBlocking { search.search("writing", "en", DictionarySearchMode.RHYME) }
        assertEquals(listOf("lighting"), writing.matches.map { it.word })

        val assonance = runBlocking { search.search("cat", "en", DictionarySearchMode.ASSONANCE) }
        assertTrue(assonance.matches.any { it.word == "bat" })
        assertFalse(assonance.matches.any { it.word == "cut" })

        val prefix = runBlocking { search.search("ba", "en", DictionarySearchMode.WORD_PREFIX) }
        assertEquals(listOf("bat"), prefix.matches.map { it.word })
        val suffix = runBlocking { search.search("writing", "en", DictionarySearchMode.WORD_SUFFIX) }
        assertEquals(listOf("lighting", "baiting"), suffix.matches.take(2).map { it.word })
        assertTrue(suffix.matches.indexOfFirst { it.word == "sing" } > 1)
        assertFalse(suffix.matches.any { it.word == "writing" })

        val stressShift = runBlocking { search.search("suffixlong", "en", DictionarySearchMode.WORD_SUFFIX) }
        assertEquals("stressshift", stressShift.matches.first().word)

        val jointSuffix = runBlocking { search.search("joint", "en", DictionarySearchMode.WORD_SUFFIX) }
        assertTrue(jointSuffix.matches.any { it.word == "point" })
        assertTrue(jointSuffix.matches.any { it.word == "checkpoint" })
        val pointSuffix = runBlocking { search.search("point", "en", DictionarySearchMode.WORD_SUFFIX) }
        assertTrue(pointSuffix.matches.any { it.word == "checkpoint" })

        val escaped = runBlocking { search.search("a%", "en", DictionarySearchMode.WORD_PREFIX) }
        assertEquals(listOf("a%mazing"), escaped.matches.map { it.word })
        val underscore = runBlocking { search.search("a_", "en", DictionarySearchMode.WORD_PREFIX) }
        assertEquals(listOf("a_mazing"), underscore.matches.map { it.word })
        val escapeCharacter = runBlocking { search.search("a!", "en", DictionarySearchMode.WORD_PREFIX) }
        assertEquals(listOf("a!mazing"), escapeCharacter.matches.map { it.word })

        val realFallback = DictionaryPronunciations(
            installer,
            EspeakIpa(InstrumentationRegistry.getInstrumentation().targetContext),
        )
        val generated = runBlocking { realFallback.lookup("ghostwriter", "en") }
        assertEquals(PronunciationSource.ESPEAK_GENERATED, generated?.source)
        assertEquals(listOf("ɡˈəʊstɹaɪtə"), generated?.ipa)
    }

    private fun withDictionaryFixture(block: (DictionaryInstaller) -> Unit) {
        val base = InstrumentationRegistry.getInstrumentation().targetContext
        val root = File(base.cacheDir, "dictionary-search-${System.nanoTime()}")
        val context = object : ContextWrapper(base) {
            override fun getApplicationContext(): Context = this
            override fun getFilesDir(): File = File(root, "files").apply { mkdirs() }
        }
        try {
            val installer = DictionaryInstaller(context)
            val version = File(context.filesDir, "dictionaries/en/${installer.release.tag}")
            version.mkdirs()
            database(File(version, "wiktionary.db"), listOf(
                Row("cat", "/ˈkæt/", "tækˈ", "æ"),
                Row("cat", "/ˈkɑt/", "tɑkˈ", "ɑ"),
                Row("kat", "/ˈkæt/", "tækˈ", "æ"),
                Row("bat", "/ˈbæt/", "tæbˈ", "æ"),
                Row("flat", "/ˈflæt/", "tælfˈ", "æ"),
                Row("cot", "/ˈkɑt/", "tɑkˈ", "ɑ"),
                Row("cut", "/ˈkʌt/", "tʌkˈ", "ʌ"),
                Row("writing", "/ˈɹaɪtɪŋ/", "ŋɪtaɪɹˈ", "ɪ aɪ"),
                Row("lighting", "/ˈlaɪtɪŋ/", "ŋɪtaɪlˈ", "ɪ aɪ"),
                Row("sing", "/ˈsɪŋ/", "ŋɪsˈ", "ɪ"),
                Row("baiting", "/ˈbeɪtɪŋ/", "ŋɪteɪbˈ", "ɪ eɪ"),
                Row("suffixlong", "/ˈkatɪŋab/", "baŋɪtakˈ", "a ɪ a"),
                Row("stressshift", "/katɪˈŋab/", "baŋˈɪtak", "a ɪ a"),
                Row("joint", "/ˈdʒɔɪnt/", "tnɔɪdʒˈ", "ɔɪ"),
                Row("point", "/ˈpɔɪnt/", "tnɔɪpˈ", "ɔɪ"),
                Row("checkpoint", "/ˈtʃɛkpɔɪnt/", "tnɔɪpkɛtʃˈ", "ɔɪ ɛ"),
                Row("outpoint", "/aʊtˈpɔɪnt/", "tnɔɪpˈtaʊ", "ɔɪ aʊ"),
                Row("a%mazing", "/əˈmeɪzɪŋ/", "ŋɪzeɪmˈə", "ɪ eɪ ə"),
                Row("a_mazing", "/əˈmeɪzɪŋ/", "ŋɪzeɪmˈə", "ɪ eɪ ə"),
                Row("a!mazing", "/əˈmeɪzɪŋ/", "ŋɪzeɪmˈə", "ɪ eɪ ə"),
            ))
            database(File(version, "espeak.db"), listOf(
                Row("bat", "/ˈbæt/", "tæbˈ", "æ"),
                Row("mat", "/ˈmæt/", "tæmˈ", "æ"),
            ))
            block(installer)
        } finally {
            root.deleteRecursively()
        }
    }

    private fun database(file: File, rows: List<Row>) {
        SQLiteDatabase.openOrCreateDatabase(file, null).use { db ->
            db.execSQL("CREATE TABLE dictionary (word TEXT NOT NULL, ipa TEXT NOT NULL, " +
                "ipa_reversed TEXT NOT NULL, assonance_reversed TEXT NOT NULL, " +
                "PRIMARY KEY (word, ipa)) WITHOUT ROWID")
            db.execSQL("CREATE INDEX idx_ipa_reversed ON dictionary(ipa_reversed)")
            db.execSQL("CREATE INDEX idx_assonance_reversed ON dictionary(assonance_reversed)")
            rows.forEach { row ->
                db.execSQL("INSERT INTO dictionary VALUES (?, ?, ?, ?)",
                    arrayOf(row.word, row.ipa, row.reversed, row.assonance))
            }
        }
    }

    private data class Row(val word: String, val ipa: String, val reversed: String, val assonance: String)
}
