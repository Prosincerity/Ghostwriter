package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.data.DictionaryMatch
import com.prosincerity.ghostwriter.data.DictionarySearchMode
import com.prosincerity.ghostwriter.data.DictionarySearchResult
import com.prosincerity.ghostwriter.data.PronunciationResult
import com.prosincerity.ghostwriter.data.PronunciationSource
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DictionaryScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun searchesAndReturnsToEditor() {
        var searched: Triple<String, String, DictionarySearchMode>? = null
        var returned = false
        composeRule.setContent {
            GhostwriterTheme {
                DictionaryScreen(
                    onBack = { returned = true },
                    onOpenDownloads = {},
                    installedLanguages = listOf("en"),
                    search = { word, language, mode ->
                        searched = Triple(word, language, mode)
                        DictionarySearchResult(
                            PronunciationResult(listOf("/ˈkæt/"), PronunciationSource.WIKTIONARY),
                            listOf(DictionaryMatch("bat", "/ˈbæt/", PronunciationSource.WIKTIONARY)),
                        )
                    },
                )
            }
        }

        composeRule.onNodeWithText("Word").performTextInput("cat")
        composeRule.onNodeWithText("Search").performClick()
        composeRule.onNodeWithText("Matches (1)").assertExists()
        composeRule.onNodeWithText("bat").assertExists()
        composeRule.runOnIdle { assertEquals(Triple("cat", "en", DictionarySearchMode.RHYME), searched) }
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.runOnIdle { assertTrue(returned) }
    }

    @Test
    fun noInstalledDictionaryOpensDownloads() {
        var openedDownloads = false
        composeRule.setContent {
            GhostwriterTheme {
                DictionaryScreen(
                    onBack = {},
                    onOpenDownloads = { openedDownloads = true },
                    installedLanguages = emptyList(),
                )
            }
        }

        composeRule.onNodeWithText("No rhyme dictionaries are installed").assertExists()
        composeRule.onNodeWithText("Search").assertDoesNotExist()
        composeRule.onNodeWithText("Download dictionaries").performClick()
        composeRule.runOnIdle { assertTrue(openedDownloads) }
    }

    @Test
    fun onlyInstalledLanguageAppearsInMenu() {
        composeRule.setContent {
            GhostwriterTheme {
                DictionaryScreen(
                    onBack = {},
                    onOpenDownloads = {},
                    installedLanguages = listOf("de"),
                )
            }
        }

        composeRule.onNodeWithText("Language: German").performClick()
        composeRule.onNodeWithText("German").assertExists()
        composeRule.onNodeWithText("English").assertDoesNotExist()
        composeRule.onNodeWithText("Turkish").assertDoesNotExist()
        composeRule.onNodeWithText("German").performClick()
        composeRule.onNodeWithText("Mode: Rhyme").performClick()
        composeRule.onNodeWithText("Assonance").performClick()
        composeRule.onNodeWithText("Mode: Assonance").assertExists()
    }
}
