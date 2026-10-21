package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AboutScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun attributionLinks_openTheirUrlsAndUseTheCurrentHandler() {
        var openedUrl: String? = null
        var onOpenLink by mutableStateOf<(String) -> Unit>({ openedUrl = it })
        composeRule.setContent {
            GhostwriterTheme {
                AboutScreen(
                    versionName = "1.2.3",
                    onBack = {},
                    onOpenLink = onOpenLink,
                )
            }
        }

        composeRule.onNodeWithText("Version 1.2.3").assertExists()
        composeRule.onNodeWithText("GNU GPL v3.0 or later").assertExists()
        composeRule.onNodeWithText("Ghostwriter comes with no warranty", substring = true).assertExists()
        composeRule.onNodeWithText("You may redistribute and modify it", substring = true).assertExists()
        composeRule.onNodeWithText("Dictionary attribution").assertExists()
        composeRule.onNodeWithText("CC BY-SA 4.0").assertExists()
        composeRule.onNodeWithText("Third-party licenses").assertExists()
        composeRule.onNodeWithText("Dictionary data license and attribution").assertExists()
        val links = listOf(
            "Source code" to GHOSTWRITER_REPOSITORY_URL,
            "GNU GPL v3.0 or later" to GHOSTWRITER_LICENSE_URL,
            "Third-party licenses" to THIRD_PARTY_LICENSES_URL,
            "Dictionary source files" to DICTIONARY_REPOSITORY_URL,
            "Dictionary data license and attribution" to DICTIONARY_DATA_LICENSE_URL,
            "Kaikki.org data source" to KAIKKI_URL,
            "Wiktionary copyright and licensing" to WIKTIONARY_COPYRIGHT_URL,
            "CC BY-SA 4.0" to CC_BY_SA_URL,
            "GNU Free Documentation License" to GFDL_URL,
            "eSpeak NG 1.52.0 source" to ESPEAK_SOURCE_URL,
            "eSpeak NG license" to ESPEAK_LICENSE_URL,
        )
        for ((label, url) in links) {
            composeRule.onNodeWithText(label).performScrollTo().performClick()
            composeRule.runOnIdle { assertEquals(url, openedUrl) }
        }
        composeRule.runOnIdle { onOpenLink = { openedUrl = "updated:$it" } }
        composeRule.onNodeWithText("Source code").performScrollTo().performClick()
        composeRule.runOnIdle { assertEquals("updated:$GHOSTWRITER_REPOSITORY_URL", openedUrl) }
    }

}
