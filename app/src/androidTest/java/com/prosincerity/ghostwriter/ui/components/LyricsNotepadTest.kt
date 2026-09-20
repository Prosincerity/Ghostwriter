package com.prosincerity.ghostwriter.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LyricsNotepadTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyNotepad_showsExistingPlaceholder() {
        composeRule.setContent {
            GhostwriterTheme {
                LyricsNotepad(lyrics = "", onLyricsChange = {})
            }
        }

        composeRule.onNodeWithText("Start writing...").assertExists()
    }

    @Test
    fun notepad_displaysAndUpdatesHoistedLyrics() {
        var latestLyrics = "Opening line"
        composeRule.setContent {
            var lyrics by remember { mutableStateOf(latestLyrics) }
            GhostwriterTheme {
                LyricsNotepad(
                    lyrics = lyrics,
                    onLyricsChange = {
                        lyrics = it
                        latestLyrics = it
                    },
                )
            }
        }

        composeRule.onNodeWithText("Opening line").performTextReplacement("Updated line")

        composeRule.onNodeWithText("Opening line").assertDoesNotExist()
        composeRule.onNodeWithText("Updated line").assertExists()
        composeRule.runOnIdle { assertEquals("Updated line", latestLyrics) }
    }
}
