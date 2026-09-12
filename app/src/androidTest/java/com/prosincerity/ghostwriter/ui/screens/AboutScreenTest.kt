package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AboutScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun aboutScreen_showsProjectAndDictionaryAttribution() {
        var openedUrl: String? = null
        composeRule.setContent {
            GhostwriterTheme {
                AboutScreen(
                    versionName = "1.2.3",
                    onBack = {},
                    onOpenLink = { openedUrl = it },
                )
            }
        }

        composeRule.onNodeWithText("Version 1.2.3").assertExists()
        composeRule.onNodeWithText("MIT License").assertExists()
        composeRule.onNodeWithText("Dictionary attribution").assertExists()
        composeRule.onNodeWithText("CC BY-SA 4.0").assertExists()
        composeRule.onNodeWithText("Dictionary source files").performClick()

        composeRule.runOnIdle {
            assertEquals(DICTIONARY_REPOSITORY_URL, openedUrl)
        }
    }

    @Test
    fun aboutScreen_backButtonInvokesNavigation() {
        var wentBack = false
        composeRule.setContent {
            GhostwriterTheme {
                AboutScreen(
                    versionName = "1.2.3",
                    onBack = { wentBack = true },
                    onOpenLink = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.runOnIdle { assertTrue(wentBack) }
    }

    @Test
    fun settingsScreen_aboutButtonInvokesNavigation() {
        var openedAbout = false
        composeRule.setContent {
            GhostwriterTheme {
                SettingsScreen(
                    onBack = {},
                    onOpenAbout = { openedAbout = true },
                )
            }
        }

        composeRule.onNodeWithText("About Ghostwriter").performClick()
        composeRule.runOnIdle { assertTrue(openedAbout) }
    }
}
