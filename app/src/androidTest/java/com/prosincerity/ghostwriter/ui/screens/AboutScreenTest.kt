package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import com.prosincerity.ghostwriter.ui.theme.GhostColorScheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AboutScreenTest {

    private companion object {
        var openedByStaticHandler: String? = null
    }

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun changingLinkTarget_keepsLabelAndOpensCurrentUrl() {
        var url by mutableStateOf(GHOSTWRITER_REPOSITORY_URL)
        var opened: String? = null
        val callback: (String) -> Unit = { opened = it }
        // The screen's links are fixed. Exercise its private link boundary
        // with a changing target to check the remembered click captures.
        val link = Class.forName("com.prosincerity.ghostwriter.ui.screens.AboutScreenKt")
            .getDeclaredMethod("AboutLink", String::class.java, String::class.java,
                Function1::class.java, androidx.compose.runtime.Composer::class.java,
                Int::class.javaPrimitiveType).apply { isAccessible = true }
        composeRule.setContent {
            GhostwriterTheme {
                link.invoke(null, "Project link", url, callback, currentComposer, 0)
            }
        }
        composeRule.onNodeWithText("Project link").performClick()
        composeRule.runOnIdle { assertEquals(GHOSTWRITER_REPOSITORY_URL, opened); url = GHOSTWRITER_LICENSE_URL }
        composeRule.onNodeWithText("Project link").performClick()
        composeRule.runOnIdle { assertEquals(GHOSTWRITER_LICENSE_URL, opened) }
    }

    @Test fun staticLinkHandler_survivesThemeChanges() {
        var color by mutableStateOf(GhostColorScheme.surfaceContainer)
        openedByStaticHandler = null
        composeRule.setContent {
            GhostwriterTheme {
                MaterialTheme(colorScheme = GhostColorScheme.copy(surfaceContainer = color)) {
                    AboutScreen("test", {}, { openedByStaticHandler = it })
                }
            }
        }
        composeRule.onNodeWithText("Source code").performScrollTo().performClick()
        composeRule.runOnIdle {
            assertEquals(GHOSTWRITER_REPOSITORY_URL, openedByStaticHandler)
            color = Color.DarkGray
        }
        composeRule.onNodeWithText("GNU GPL v3.0 or later").performScrollTo().performClick()
        composeRule.runOnIdle { assertEquals(GHOSTWRITER_LICENSE_URL, openedByStaticHandler) }
    }

    @Test fun changingLinkHandler_usesLatestCallbackAfterRecomposition() {
        var opened = ""
        var color by mutableStateOf(GhostColorScheme.surfaceContainer)
        var callback by mutableStateOf<(String) -> Unit>({ opened = "old:$it" })
        composeRule.setContent {
            GhostwriterTheme {
                MaterialTheme(colorScheme = GhostColorScheme.copy(surfaceContainer = color)) {
                    AboutScreen("test", {}, callback)
                }
            }
        }
        composeRule.onNodeWithText("Source code").performScrollTo().performClick()
        composeRule.runOnIdle {
            assertEquals("old:$GHOSTWRITER_REPOSITORY_URL", opened)
            callback = { opened = "new:$it" }
        }
        composeRule.onNodeWithText("Source code").performScrollTo().performClick()
        composeRule.runOnIdle { assertEquals("new:$GHOSTWRITER_REPOSITORY_URL", opened); color = Color.DarkGray }
        composeRule.onNodeWithText("Source code").performScrollTo().performClick()
        composeRule.runOnIdle { assertEquals("new:$GHOSTWRITER_REPOSITORY_URL", opened) }
    }

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
    }

}
