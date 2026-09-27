package com.prosincerity.ghostwriter.data

import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prosincerity.ghostwriter.logic.IpaSearchKeys
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
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
    fun cancellationStopsReadingRowsAndClosesCursor() = withDictionaryFixture { installer ->
        installer.openReadOnly("en", DictionarySource.WIKTIONARY)!!.use { database ->
            var visited = 0
            var readCursor: Cursor? = null
            val failure = runCatching {
                runBlocking {
                    val queryContext = coroutineContext
                    database.readDictionaryRows("SELECT word, ipa FROM dictionary", emptyArray()) { cursor ->
                        visited++
                        readCursor = cursor
                        queryContext.cancel()
                    }
                }
            }.exceptionOrNull()

            assertTrue(failure is CancellationException)
            assertEquals(1, visited)
            assertTrue(readCursor?.isClosed == true)
            database.rawQuery("SELECT count(*) FROM dictionary", null).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertTrue(cursor.getInt(0) > 1)
            }
        }
    }

    @Test
    fun paginatesPastResultAndDatabaseScanLimits() = withDictionaryFixture { installer ->
        val file = File(installer.appContext.filesDir, "dictionaries/en/${installer.release.tag}/wiktionary.db")
        SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READWRITE).use { db ->
            db.beginTransaction()
            try {
                repeat(205) { index ->
                    db.execSQL("INSERT INTO dictionary VALUES (?, ?, ?, ?)", arrayOf(
                        "rhyme${index.toString().padStart(3, '0')}", "/ˈbæt/", "tæbˈ", "æ",
                    ))
                }
                repeat(70) { index ->
                    db.execSQL("INSERT INTO dictionary VALUES (?, ?, ?, ?)", arrayOf(
                        "ending${index.toString().padStart(3, '0')}", "/ˈlaɪtɪŋ/", "ŋɪtaɪlˈ", "ɪ aɪ",
                    ))
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
        val search = DictionarySearch(installer, DictionaryPronunciations(installer) { _, _ -> null })

        val rhymePages = (0..3).map { page ->
            runBlocking { search.search("cat", "en", DictionarySearchMode.RHYME, page) }
        }
        assertEquals(listOf(60, 60, 60, 30), rhymePages.map { it.matches.size })
        assertEquals(listOf(true, true, true, false), rhymePages.map { it.hasNext })
        assertEquals(210, rhymePages.flatMap { it.matches }.map { it.word }.toSet().size)

        val prefixPages = (0..3).map { page ->
            runBlocking { search.search("rhyme", "en", DictionarySearchMode.WORD_PREFIX, page) }
        }
        assertEquals(listOf(60, 60, 60, 25), prefixPages.map { it.matches.size })
        assertEquals(205, prefixPages.flatMap { it.matches }.map { it.word }.toSet().size)

        val suffixFirst = runBlocking { search.search("writing", "en", DictionarySearchMode.WORD_SUFFIX, 0) }
        val suffixSecond = runBlocking { search.search("writing", "en", DictionarySearchMode.WORD_SUFFIX, 1) }
        assertEquals(60, suffixFirst.matches.size)
        assertTrue(suffixFirst.hasNext)
        assertEquals(12, suffixSecond.matches.size)
        assertFalse(suffixSecond.hasNext)
        assertFalse((suffixFirst.matches + suffixSecond.matches).any { it.word == "sing" })
        assertTrue(suffixFirst.matches.map { it.word }.toSet().intersect(
            suffixSecond.matches.map { it.word }.toSet()).isEmpty())
    }

    @Test
    fun unstressedGermanRhymesSurviveCommonEndingScanLimit() = withDictionaryFixture { installer ->
        val version = File(installer.appContext.filesDir, "dictionaries/de/${installer.release.tag}")
        version.mkdirs()
        val longIpa = "/ˈa${"b".repeat(10)}aft/"
        val longReversed = "tfa${"b".repeat(10)}aˈ"
        database(File(version, "wiktionary.db"),
            (0..205).map { Row("decoy$it", longIpa, longReversed, "a a") } + listOf(
                Row("Kraft", "/kʁaft/", "tfaʁk", "a"),
                Row("Saft", "/zaft/", "tfaz", "a"),
                Row("Schaft", "[ʃaft]", "tfaʃ", "a"),
                Row("pafft", "[paft]", "tfap", "a"),
            ))
        val search = DictionarySearch(installer, DictionaryPronunciations(installer) { _, _ -> null })

        val rhymes = runBlocking { search.search("Kraft", "de", DictionarySearchMode.RHYME) }
        assertEquals(setOf("Saft", "Schaft", "pafft"), rhymes.matches.map { it.word }.toSet())
    }

    @Test
    fun shortGermanSuffixesUseAllInputPhonemes() = withDictionaryFixture { installer ->
        val version = File(installer.appContext.filesDir, "dictionaries/de/${installer.release.tag}")
        version.mkdirs()
        database(File(version, "wiktionary.db"), listOf(
            Row("und", "/ʊnt/", "tnʊ", "ʊ"),
            Row("Hund", "/hʊnt/", "tnʊh", "ʊ"),
            Row("Windhund", "/vɪnthʊnt/", "tnʊhtnɪv", "ʊ ɪ"),
            Row("Knall", "/knal/", "lank", "a"),
            Row("Urknall", "/uːɐ̯knal/", "lankɐ̯uː", "a ɐ̯ uː"),
            Row("Ah", "/a/", "a", "a"),
            Row("Bah", "/ba/", "ab", "a"),
        ))
        val search = DictionarySearch(installer, DictionaryPronunciations(installer) { _, _ -> null })

        val und = runBlocking { search.search("und", "de", DictionarySearchMode.WORD_SUFFIX) }
        assertEquals(setOf("Hund", "Windhund"), und.matches.map { it.word }.toSet())
        val hund = runBlocking { search.search("Hund", "de", DictionarySearchMode.WORD_SUFFIX) }
        assertEquals(listOf("Windhund"), hund.matches.map { it.word })
        val knall = runBlocking { search.search("Knall", "de", DictionarySearchMode.WORD_SUFFIX) }
        assertEquals(listOf("Urknall"), knall.matches.map { it.word })
        val ah = runBlocking { search.search("Ah", "de", DictionarySearchMode.WORD_SUFFIX) }
        assertTrue(ah.matches.any { it.word == "Bah" })
    }

    @Test
    fun assonanceFallsBackOnlyWhenNoExactKeyMatchesAcrossSources() = withDictionaryFixture { installer ->
        val version = File(installer.appContext.filesDir, "dictionaries/de/${installer.release.tag}")
        version.mkdirs()
        fun row(word: String, ipa: String): Row {
            val keys = IpaSearchKeys.fromIpa(ipa, "de")!!
            return Row(word, ipa, keys.reversed, keys.assonance)
        }
        database(File(version, "wiktionary.db"), listOf(
            row("source", "/aeiou/"),
            row("partial", "/iou/"),
            row("absent", "/ɑeiou/"),
            row("short", "/æɒ/"),
            row("extended", "/uæɒ/"),
        ))
        database(File(version, "espeak.db"), listOf(row("exact", "/baeiosu/")))
        val search = DictionarySearch(installer, DictionaryPronunciations(installer) { _, _ -> null })

        val exact = runBlocking { search.search("source", "de", DictionarySearchMode.ASSONANCE) }
        assertEquals(listOf("exact"), exact.matches.map { it.word })
        assertTrue(runBlocking { search.search("source", "de", DictionarySearchMode.ASSONANCE, 1) }
            .matches.isEmpty())

        val fallback = runBlocking { search.search("absent", "de", DictionarySearchMode.ASSONANCE) }
        assertEquals(setOf("partial", "source", "exact"), fallback.matches.map { it.word }.toSet())
        val short = runBlocking { search.search("short", "de", DictionarySearchMode.ASSONANCE) }
        assertEquals(listOf("extended"), short.matches.map { it.word })
    }

    @Test
    fun assonancePaginatesDistinctBaseForms() = withDictionaryFixture { installer ->
        val version = File(installer.appContext.filesDir, "dictionaries/de/${installer.release.tag}")
        version.mkdirs()
        val rows = buildList {
            add(Row("source", "/ae/", "ea", "e a"))
            repeat(65) { index ->
                val base = "stem${index.toString().padStart(3, '0')}"
                add(Row(base, "/bae/", "eab", "e a"))
                add(Row("${base}n", "/baen/", "neab", "e a"))
            }
        }
        database(File(version, "wiktionary.db"), rows)
        val search = DictionarySearch(installer, DictionaryPronunciations(installer) { _, _ -> null })

        val first = runBlocking { search.search("source", "de", DictionarySearchMode.ASSONANCE) }
        val second = runBlocking { search.search("source", "de", DictionarySearchMode.ASSONANCE, 1) }
        assertEquals(60, first.matches.size)
        assertTrue(first.hasNext)
        assertEquals(5, second.matches.size)
        assertFalse(second.hasNext)
        assertEquals(65, (first.matches + second.matches).map { it.word }.toSet().size)
        assertFalse((first.matches + second.matches).any { it.word.endsWith("n") })
    }

    @Test
    fun assonanceChecksShorterExactKeyBeforeLargeLongerKey() = withDictionaryFixture { installer ->
        val version = File(installer.appContext.filesDir, "dictionaries/de/${installer.release.tag}")
        version.mkdirs()
        fun row(word: String, ipa: String): Row {
            val keys = IpaSearchKeys.fromIpa(ipa, "de")!!
            return Row(word, ipa, keys.reversed, keys.assonance)
        }
        database(File(version, "wiktionary.db"), buildList {
            add(row("source", "/aeio/"))
            add(row("source", "/eio/"))
            add(row("base", "/beio/"))
            repeat(70) { index -> add(row("form$index", "/baeio/")) }
        })
        val search = DictionarySearch(installer, DictionaryPronunciations(installer) { _, _ -> null })

        val first = runBlocking { search.search("source", "de", DictionarySearchMode.ASSONANCE) }
        assertEquals("base", first.matches.first().word)
        assertTrue(first.hasNext)
    }

    @Test
    fun assonanceCollapsesReleasedGermanInflections() = withDictionaryFixture { installer ->
        val version = File(installer.appContext.filesDir, "dictionaries/de/${installer.release.tag}")
        version.mkdirs()
        fun row(word: String, ipa: String): Row {
            val keys = IpaSearchKeys.fromIpa(ipa, "de")!!
            return Row(word, ipa, keys.reversed, keys.assonance)
        }
        database(File(version, "wiktionary.db"), listOf(
            row("verfolgen", "/fɛɐ̯ˈfɔlɡən/"),
            row("verfolgen", "[fɛɐ̯ˈfɔlɡn̩]"),
            row("erblond", "[ɛɐ̯ˈblɔnt]"),
            row("erblonde", "[ɛɐ̯ˈblɔndə]"),
            row("erblonden", "[ɛɐ̯ˈblɔndn̩]"),
            row("erblondende", "[ɛɐ̯ˈblɔndn̩də]"),
            row("erblondendem", "[ɛɐ̯ˈblɔndn̩dəm]"),
            row("erborg", "[ɛɐ̯ˈbɔʁk]"),
            row("erborge", "[ɛɐ̯ˈbɔʁɡə]"),
            row("erborgende", "[ɛɐ̯ˈbɔʁɡn̩də]"),
            row("erborgendem", "[ɛɐ̯ˈbɔʁɡn̩dəm]"),
            row("erborgte", "[ɛɐ̯ˈbɔʁktə]"),
            row("erborgtem", "[ɛɐ̯ˈbɔʁktəm]"),
            row("erborget", "[ɛɐ̯ˈbɔʁɡət]"),
            row("erborgtet", "[ɛɐ̯ˈbɔʁktət]"),
            row("erdrossel", "[ɛɐ̯ˈdʁɔsl̩]"),
            row("erdrosselnde", "[ɛɐ̯ˈdʁɔsl̩ndə]"),
            row("erdrosselte", "[ɛɐ̯ˈdʁɔsl̩tə]"),
            row("verdoppel", "[fɛɐ̯ˈdɔpl̩]"),
            row("verdoppelnde", "[fɛɐ̯ˈdɔpl̩ndə]"),
            row("verborgen", "[fɛɐ̯ˈbɔʁɡn̩]"),
            row("verborgenste", "[fɛɐ̯ˈbɔʁɡn̩stə]"),
            row("erfolg", "[ɛɐ̯ˈfɔlk]"),
            row("erfolgende", "[ɛɐ̯ˈfɔlɡn̩də]"),
        ))
        val search = DictionarySearch(installer, DictionaryPronunciations(installer) { _, _ -> null })

        val result = runBlocking { search.search("verfolgen", "de", DictionarySearchMode.ASSONANCE) }
        assertEquals(setOf("erblonden", "erborg", "erdrossel", "verdoppel", "verborgen", "erfolg"),
            result.matches.map { it.word }.toSet())
        assertFalse(result.hasNext)
    }

    @Test
    fun searchesRhymeAssonanceAndWordsWithSourcePrecedence() = withDictionaryFixture { installer ->
        val pronunciations = DictionaryPronunciations(installer) { _, _ -> null }
        val search = DictionarySearch(installer, pronunciations)

        val rhymes = runBlocking { search.search("cat", "en", DictionarySearchMode.RHYME) }
        assertEquals(PronunciationSource.WIKTIONARY, rhymes.pronunciation?.source)
        assertEquals(listOf("bat", "kat", "flat", "cot", "mat"), rhymes.matches.map { it.word })
        assertEquals(PronunciationSource.WIKTIONARY, rhymes.matches.first().source)
        assertEquals(2, rhymes.pronunciation?.ipa?.size)
        assertFalse(rhymes.matches.any { it.word == "cat" })

        val writing = runBlocking { search.search("writing", "en", DictionarySearchMode.RHYME) }
        assertEquals(listOf("lighting"), writing.matches.map { it.word })

        val assonance = runBlocking { search.search("cat", "en", DictionarySearchMode.ASSONANCE) }
        assertTrue(assonance.matches.any { it.word == "bat" })
        assertFalse(assonance.matches.any { it.word == "cut" })

        val prefix = runBlocking { search.search("ba", "en", DictionarySearchMode.WORD_PREFIX) }
        assertEquals(listOf("baiting", "bat"), prefix.matches.map { it.word })
        val suffix = runBlocking { search.search("writing", "en", DictionarySearchMode.WORD_SUFFIX) }
        assertEquals(listOf("lighting", "baiting"), suffix.matches.take(2).map { it.word })
        assertFalse(suffix.matches.any { it.word == "sing" })
        assertFalse(suffix.matches.any { it.word == "writing" })

        val stressShift = runBlocking { search.search("suffixlong", "en", DictionarySearchMode.WORD_SUFFIX) }
        assertEquals("stressshift", stressShift.matches.first().word)

        val jointSuffix = runBlocking { search.search("joint", "en", DictionarySearchMode.WORD_SUFFIX) }
        assertTrue(jointSuffix.matches.isEmpty())
        val pointSuffix = runBlocking { search.search("point", "en", DictionarySearchMode.WORD_SUFFIX) }
        assertEquals(setOf("checkpoint", "outpoint"), pointSuffix.matches.map { it.word }.toSet())
        val batSuffix = runBlocking { search.search("bat", "en", DictionarySearchMode.WORD_SUFFIX) }
        assertTrue(batSuffix.matches.isEmpty())
        val outpointSuffix = runBlocking { search.search("outpoint", "en", DictionarySearchMode.WORD_SUFFIX) }
        assertTrue(outpointSuffix.matches.any { it.word == "point" })
        assertTrue(outpointSuffix.matches.any { it.word == "checkpoint" })
        val jointRhymes = runBlocking { search.search("joint", "en", DictionarySearchMode.RHYME) }
        assertTrue(jointRhymes.matches.any { it.word == "point" })
        assertFalse(jointRhymes.matches.any { it.word == "outpoint" })

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

        val file = File(installer.appContext.filesDir, "dictionaries/en/${installer.release.tag}/wiktionary.db")
        SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READWRITE).use { db ->
            listOf(
                "acrobat" to "/ˈækrəbæt/",
                "action" to "/ˈækʃən/",
                "information" to "/ˌɪnfəˈmeɪʃən/",
            ).forEach { (word, ipa) ->
                val keys = IpaSearchKeys.fromIpa(ipa, "en")!!
                db.execSQL("INSERT INTO dictionary VALUES (?, ?, ?, ?)",
                    arrayOf(word, ipa, keys.reversed, keys.assonance))
            }
        }
        val batEnding = runBlocking { search.search("bat", "en", DictionarySearchMode.WORD_SUFFIX) }
        assertEquals(listOf("acrobat"), batEnding.matches.map { it.word })
        val actionEnding = runBlocking { search.search("action", "en", DictionarySearchMode.WORD_SUFFIX) }
        assertEquals(listOf("information"), actionEnding.matches.map { it.word })
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
