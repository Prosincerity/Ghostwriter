package com.prosincerity.ghostwriter.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EspeakIpaInstrumentedTest {
    private val generator: EspeakIpa
        get() = EspeakIpa(InstrumentationRegistry.getInstrumentation().targetContext)

    @Test
    fun matchesPinnedCliForThreeLanguages() = runBlocking {
        assertEquals("ɡˈəʊstɹaɪtə", generator.ipa("ghostwriter", "en"))
        assertEquals("ˌyːbɜmˈuːt", generator.ipa("Übermut", "de"))
        assertEquals("ɯʃˈɯk", generator.ipa("ışık", "tr"))
    }

    @Test
    fun rejectsUnsupportedAndEmptyInput() = runBlocking {
        assertNull(generator.ipa("word", "fr"))
        assertNull(generator.ipa(" ", "en"))
        assertNull(generator.ipa("two words", "en"))
    }
}
