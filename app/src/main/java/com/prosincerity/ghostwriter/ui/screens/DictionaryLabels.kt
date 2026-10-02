package com.prosincerity.ghostwriter.ui.screens

internal fun dictionaryLanguageLabel(language: String): String = when (language) {
    "en" -> "English"
    "de" -> "German"
    "tr" -> "Turkish"
    else -> language
}
