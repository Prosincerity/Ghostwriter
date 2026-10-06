package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LyricsFolderScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun folderSelectionAndSettingsRemainAvailableAfterAStorageFailure() {
        var selected = false
        var settings = false
        composeRule.setContent {
            GhostwriterTheme { LyricsFolderScreen(false, "Local draft kept", { selected = true }, { settings = true }) }
        }
        composeRule.onNodeWithText("Keep your lyrics").assertExists()
        composeRule.onNodeWithText("Local draft kept").assertExists()
        composeRule.onNodeWithText("Choose lyric folder").performScrollTo().performClick()
        composeRule.onNodeWithText("Settings").performScrollTo().performClick()
        composeRule.runOnIdle { assertTrue(selected); assertTrue(settings) }
    }

    @Test fun preparationWaitsBeforeAllowingFolderChanges() {
        composeRule.setContent { GhostwriterTheme { LyricsFolderScreen(true, null, {}, {}) } }
        composeRule.onNodeWithText("Preparing your projects…").assertExists()
        composeRule.onNodeWithText("Choose lyric folder").assertDoesNotExist()
    }
}
