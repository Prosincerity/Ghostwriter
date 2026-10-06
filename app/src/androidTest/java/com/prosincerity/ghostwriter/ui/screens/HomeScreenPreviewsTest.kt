package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composer
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenPreviewsTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun recentProjectsPreview_showsSampleProjectsAndMusicalDetails() {
        composeRule.setContent { renderPreview("RecentProjectsPreview") }
        composeRule.onNodeWithText("Night shift").assertExists()
        composeRule.onNodeWithText("Loose lines").assertExists()
        composeRule.onNodeWithText("Side B").assertExists()
        composeRule.onNodeWithText("92 BPM", substring = true).assertExists()
    }

    @Test fun emptyPreview_showsFirstProjectPrompt() {
        composeRule.setContent { renderPreview("EmptyProjectsPreview") }
        composeRule.onNodeWithText("Start your next track").assertExists()
        composeRule.onNodeWithText("New project").assertExists()
    }

    // Preview entry points are deliberately private. Invoke the actual preview
    // so sample data/layout regressions are caught without widening the app API.
    @OptIn(InternalComposeApi::class)
    @Composable
    private fun renderPreview(name: String) {
        val method = Class.forName("com.prosincerity.ghostwriter.ui.screens.HomeScreenPreviewsKt")
            .getDeclaredMethod(name, Composer::class.java, Int::class.javaPrimitiveType)
            .apply { isAccessible = true }
        method.invoke(null, currentComposer, 0)
    }
}
