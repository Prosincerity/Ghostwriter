package com.prosincerity.ghostwriter.dictionary.ui

import com.prosincerity.ghostwriter.dictionary.ui.dictionaryLanguageLabel
import org.junit.Assert.assertEquals
import org.junit.Test

class DictionaryLabelsTest {
    @Test fun supportedLanguagesHaveNamesAndUnknownCodesRemainReadable() {
        for ((code, label) in mapOf("en" to "English", "de" to "German", "tr" to "Turkish",
            "fr" to "fr", "" to "")) {
            assertEquals(label, dictionaryLanguageLabel(code))
        }
    }
}
