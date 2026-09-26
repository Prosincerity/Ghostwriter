package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
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
import kotlin.math.abs
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@RunWith(AndroidJUnit4::class)
class DictionaryScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun changingWordLanguageOrModeClearsPreviousError() {
        composeRule.setContent {
            GhostwriterTheme {
                DictionaryScreen(
                    onBack = {},
                    onOpenDownloads = {},
                    installedLanguages = listOf("en", "de"),
                    search = { _, _, _, _ -> error("Lookup failed") },
                )
            }
        }

        composeRule.onNodeWithText("Word").performTextInput("cat")
        composeRule.onNodeWithText("Search").performClick()
        composeRule.onNodeWithText("Lookup failed").assertExists()
        composeRule.onNodeWithText("Word").performTextReplacement("dog")
        composeRule.onNodeWithText("Lookup failed").assertDoesNotExist()

        composeRule.onNodeWithText("Search").performClick()
        composeRule.onNodeWithText("Lookup failed").assertExists()
        composeRule.onNodeWithText("Language: English").performClick()
        composeRule.onNodeWithText("German").performClick()
        composeRule.onNodeWithText("Lookup failed").assertDoesNotExist()

        composeRule.onNodeWithText("Search").performClick()
        composeRule.onNodeWithText("Lookup failed").assertExists()
        composeRule.onNodeWithText("Mode: Rhyme").performClick()
        composeRule.onNodeWithText("Word prefix").performClick()
        composeRule.onNodeWithText("Lookup failed").assertDoesNotExist()
    }

    @Test
    fun supersededSearchCannotPublishResultsOrClearNewSearchLoadingState() {
        val pending = mutableMapOf<String, Continuation<DictionarySearchResult>>()
        composeRule.setContent {
            GhostwriterTheme {
                DictionaryScreen(
                    onBack = {},
                    onOpenDownloads = {},
                    installedLanguages = listOf("en"),
                    // Deliberately allow a canceled request to return, as blocking IO can do.
                    search = { word, _, _, _ -> suspendCoroutine { pending[word] = it } },
                )
            }
        }

        fun result(word: String) = DictionarySearchResult(
            null, listOf(DictionaryMatch(word, "/æt/", PronunciationSource.WIKTIONARY)),
        )

        try {
            composeRule.onNodeWithText("Word").performTextInput("cat")
            composeRule.onNodeWithText("Search").performClick()
            composeRule.onNodeWithText("Searching…").assertExists()
            composeRule.onNodeWithText("Word").performTextReplacement("hat")
            composeRule.onNodeWithText("Searching…").assertDoesNotExist()
            composeRule.onNodeWithText("Search").performClick()

            composeRule.runOnIdle { pending.remove("cat")!!.resume(result("stale result")) }
            composeRule.onNodeWithText("stale result").assertDoesNotExist()
            composeRule.onNodeWithText("Searching…").assertExists()
            composeRule.onNodeWithText("Search").assertIsNotEnabled()

            composeRule.runOnIdle { pending.remove("hat")!!.resume(result("current result")) }
            composeRule.onNodeWithText("current result").assertExists()
            composeRule.onNodeWithText("Searching…").assertDoesNotExist()
            composeRule.onNodeWithText("stale result").assertDoesNotExist()
        } finally {
            composeRule.runOnIdle {
                pending.values.toList().forEach { it.resume(DictionarySearchResult(null, emptyList())) }
                pending.clear()
            }
        }
    }

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
                    search = { word, language, mode, _ ->
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
    fun movesBetweenResultPages() {
        val requestedPages = mutableListOf<Int>()
        composeRule.setContent {
            GhostwriterTheme {
                DictionaryScreen(
                    onBack = {},
                    onOpenDownloads = {},
                    installedLanguages = listOf("en"),
                    search = { _, _, _, page ->
                        requestedPages += page
                        DictionarySearchResult(
                            null,
                            if (page == 0) (0 until 60).map {
                                DictionaryMatch("entry-$it", "/æt/", PronunciationSource.WIKTIONARY)
                            } else listOf(DictionaryMatch("entry-60", "/æt/", PronunciationSource.WIKTIONARY)),
                            hasNext = page == 0,
                        )
                    },
                )
            }
        }

        composeRule.onNodeWithText("Word").performTextInput("cat")
        composeRule.onNodeWithText("Search").performClick()
        composeRule.onNodeWithText("Page 1").assertExists()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Page 2").assertExists()
        composeRule.onNodeWithText("entry-60").assertExists()
        composeRule.onNodeWithText("Next").assertIsNotEnabled()
        composeRule.onNodeWithText("Previous").performClick()
        composeRule.onNodeWithText("Page 1").assertExists()
        composeRule.runOnIdle { assertEquals(listOf(0, 1, 0), requestedPages) }
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

        val screen = composeRule.onRoot().fetchSemanticsNode().boundsInRoot
        val title = composeRule.onNodeWithText("Rhyme Search").fetchSemanticsNode().boundsInRoot
        val headline = composeRule.onNodeWithText("No rhyme dictionaries are installed")
            .fetchSemanticsNode().boundsInRoot
        val description = composeRule.onNodeWithText(
            "Download at least one language to search for matching words.",
        ).fetchSemanticsNode().boundsInRoot
        val button = composeRule.onNodeWithText("Download dictionaries").fetchSemanticsNode().boundsInRoot
        val centerX = screen.center.x
        assertTrue(abs(headline.center.x - centerX) < 4f)
        assertTrue(abs(description.center.x - centerX) < 4f)
        assertTrue(abs(button.center.x - centerX) < 4f)
        val contentCenterY = (title.bottom + screen.bottom) / 2f
        val emptyStateCenterY = (headline.top + button.bottom) / 2f
        assertTrue(abs(emptyStateCenterY - contentCenterY) < screen.height * 0.06f)
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
