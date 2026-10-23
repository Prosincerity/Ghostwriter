package com.prosincerity.ghostwriter.dictionary.ui

internal fun dictionaryLanguageLabel(language: String): String = when (language) {
    "en" -> "English"
    "de" -> "German"
    "tr" -> "Turkish"
    else -> language
}
