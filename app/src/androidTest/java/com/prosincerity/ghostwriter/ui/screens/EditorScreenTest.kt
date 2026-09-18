package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prosincerity.ghostwriter.data.ProjectStorage
import com.prosincerity.ghostwriter.logic.WaveformExtractor
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

@RunWith(AndroidJUnit4::class)
class EditorScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun emptyEditor_showsCoreControlsAndInvokesNavigation() {
        val projectTitle = uniqueProjectTitle("Editor controls")
        val showEditor = mutableStateOf(true)
        var openedSettings = false

        try {
            composeRule.setContent {
                if (showEditor.value) {
                    GhostwriterTheme {
                        EditorScreen(
                            projectTitle = projectTitle,
                            onBack = { showEditor.value = false },
                            onOpenSettings = { openedSettings = true },
                        )
                    }
                }
            }

            composeRule.onNodeWithText(projectTitle).assertExists()
            composeRule.onNodeWithText("Import beat").assertExists()
            composeRule.onNodeWithText("Start writing...").assertExists()
            composeRule.onNodeWithContentDescription("Project Info").assertExists()
            composeRule.onNodeWithContentDescription("Save").assertExists()

            composeRule.onNodeWithContentDescription("Settings").performClick()
            composeRule.runOnIdle { assertTrue(openedSettings) }

            composeRule.onNodeWithContentDescription("Back").performClick()
            composeRule.onNodeWithText(projectTitle).assertDoesNotExist()
            composeRule.runOnIdle { assertFalse(showEditor.value) }
        } finally {
            disposeEditorAndDeleteProject(showEditor, projectTitle)
        }
    }

    @Test
    fun editingLyricsAndProjectInfo_persistsBothWhenLeaving() {
        val projectTitle = uniqueProjectTitle("Editor persistence")
        val projectDir = ProjectStorage.projectDir(context, projectTitle)
        val showEditor = mutableStateOf(true)
        val lyrics = "Opening line\nSecond line"

        try {
            composeRule.setContent {
                if (showEditor.value) {
                    GhostwriterTheme {
                        EditorScreen(
                            projectTitle = projectTitle,
                            onBack = { showEditor.value = false },
                            onOpenSettings = {},
                        )
                    }
                }
            }

            composeRule.onNodeWithContentDescription("Project Info").performClick()
            composeRule.onNodeWithText("Project Information").assertExists()
            composeRule.onNodeWithText("Title: $projectTitle").assertExists()
            composeRule.onNodeWithText("BPM (optional)").performTextInput("96")
            composeRule.onNodeWithText("Musical Key (optional)").performTextInput("C Minor")
            composeRule.onNodeWithText("Save").performClick()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                val metadata = ProjectStorage.loadMetadata(projectDir, projectTitle)
                metadata.bpm == 96 && metadata.key == "C Minor"
            }

            composeRule.onNode(hasSetTextAction()).performTextInput(lyrics)
            composeRule.onNodeWithContentDescription("Back").performClick()
            composeRule.waitForIdle()

            assertEquals(lyrics, ProjectStorage.loadLatest(projectDir))
        } finally {
            disposeEditorAndDeleteProject(showEditor, projectTitle)
        }
    }

    @Test
    fun manualSave_persistsCurrentLyrics() {
        val projectTitle = uniqueProjectTitle("Editor manual save")
        val projectDir = ProjectStorage.projectDir(context, projectTitle)
        val showEditor = mutableStateOf(true)
        val lyrics = "Saved from the toolbar"

        try {
            setEditorContent(projectTitle, showEditor)

            composeRule.onNode(hasSetTextAction()).performTextInput(lyrics)
            composeRule.onNodeWithContentDescription("Save").performClick()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                ProjectStorage.loadLatest(projectDir) == lyrics
            }
        } finally {
            disposeEditorAndDeleteProject(showEditor, projectTitle)
        }
    }

    @Test
    fun cachedBeat_markerLifecyclePersistsAddMoveRenameAndDelete() {
        val projectTitle = uniqueProjectTitle("Editor markers")
        val showEditor = mutableStateOf(true)
        val projectDir = createAssignedBeat(projectTitle, durationMs = 5_000, cacheWaveform = true)

        try {
            setEditorContent(projectTitle, showEditor)
            waitUntilTextExists("editor-fixture")
            waitUntilTextExists("0:00/0:05")

            composeRule.onNodeWithTag("Waveform").performTouchInput { longClick(center) }
            composeRule.onNodeWithText("Add marker").assertExists()
            composeRule.onNodeWithText("Marker name").performTextInput("Hook")
            composeRule.onNodeWithText("Add").performClick()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                ProjectStorage.loadMetadata(projectDir, projectTitle).markers.singleOrNull()?.label == "Hook"
            }
            val originalPosition = ProjectStorage.loadMetadata(projectDir, projectTitle)
                .markers.single().positionMs

            composeRule.onNodeWithTag("Waveform marker Hook").performSemanticsAction(
                SemanticsActions.SetProgress,
            ) { setProgress ->
                setProgress((originalPosition + 1_000L).toFloat())
            }
            composeRule.waitUntil(timeoutMillis = 5_000) {
                ProjectStorage.loadMetadata(projectDir, projectTitle)
                    .markers.singleOrNull()?.positionMs?.let { it != originalPosition } == true
            }

            composeRule.onNodeWithTag("Waveform marker Hook").performSemanticsAction(
                SemanticsActions.OnClick,
            ) { click ->
                assertTrue(click())
            }
            waitUntilTextExists("Edit marker")
            composeRule.onNodeWithText("Marker name").performTextReplacement("Chorus")
            composeRule.onNodeWithText("Rename").performClick()
            composeRule.waitUntil(timeoutMillis = 5_000) {
                ProjectStorage.loadMetadata(projectDir, projectTitle).markers.singleOrNull()?.label == "Chorus"
            }

            composeRule.onNodeWithTag("Waveform marker Chorus").performSemanticsAction(
                SemanticsActions.OnClick,
            ) { click ->
                assertTrue(click())
            }
            waitUntilTextExists("Edit marker")
            composeRule.onNodeWithText("Delete marker").performClick()
            composeRule.waitUntil(timeoutMillis = 5_000) {
                ProjectStorage.loadMetadata(projectDir, projectTitle).markers.isEmpty()
            }
        } finally {
            disposeEditorAndDeleteProject(showEditor, projectTitle)
        }
    }

    @Test
    fun corruptBeat_retryThenRemove_clearsBeatAndMetadata() {
        val projectTitle = uniqueProjectTitle("Editor corrupt beat")
        val showEditor = mutableStateOf(true)
        val projectDir = ProjectStorage.projectDir(context, projectTitle)
        ProjectStorage.assignBeatToProject(projectDir, "corrupt.wav") { destination ->
            destination.writeText("not a WAV file")
        }

        try {
            setEditorContent(projectTitle, showEditor)
            waitUntilTextExists("Couldn't create waveform")

            composeRule.onNodeWithText("Retry").performClick()
            waitUntilTextExists("Couldn't create waveform")
            composeRule.onNodeWithText("Remove beat").performClick()
            composeRule.onNodeWithText("Reassign beat?").assertExists()
            clickLastNodeWithText("Reassign")

            waitUntilTextExists("No beat selected")
            composeRule.waitUntil(timeoutMillis = 5_000) {
                val metadata = ProjectStorage.loadMetadata(projectDir, projectTitle)
                ProjectStorage.getProjectBeatFile(projectDir, metadata) == null &&
                    metadata.beatFile == null && metadata.beatOriginalName == null
            }
        } finally {
            disposeEditorAndDeleteProject(showEditor, projectTitle)
        }
    }

    @Test
    fun longBeatWarning_cancelThenApprove_usesCachedWaveform() {
        val projectTitle = uniqueProjectTitle("Editor long beat")
        val showEditor = mutableStateOf(true)
        val projectDir = createAssignedBeat(
            projectTitle = projectTitle,
            durationMs = LONG_TEST_BEAT_DURATION_MS,
            cacheWaveform = false,
        )

        try {
            setEditorContent(projectTitle, showEditor)
            waitUntilTextExists("Long audio file")

            composeRule.onNodeWithText("Cancel preparation").performClick()
            waitUntilTextExists("Waveform preparation canceled")
            composeRule.onNodeWithText("Retry").performClick()
            waitUntilTextExists("Long audio file")

            assertTrue(
                ProjectStorage.saveCachedWaveform(
                    projectDir,
                    WaveformExtractor.DEFAULT_TARGET_SAMPLE_COUNT,
                    cachedWaveform(),
                ),
            )
            composeRule.onNodeWithText("Process anyway").performClick()

            waitUntilTextExists("editor-fixture")
            waitUntilTextExists("0:00/5:00")
        } finally {
            disposeEditorAndDeleteProject(showEditor, projectTitle)
        }
    }

    private fun setEditorContent(
        projectTitle: String,
        showEditor: MutableState<Boolean>,
    ) {
        composeRule.setContent {
            if (showEditor.value) {
                GhostwriterTheme {
                    EditorScreen(
                        projectTitle = projectTitle,
                        onBack = { showEditor.value = false },
                        onOpenSettings = {},
                    )
                }
            }
        }
    }

    private fun createAssignedBeat(
        projectTitle: String,
        durationMs: Int,
        cacheWaveform: Boolean,
    ): File {
        val projectDir = ProjectStorage.projectDir(context, projectTitle)
        ProjectStorage.assignBeatToProject(projectDir, "editor-fixture.wav") { destination ->
            writePcm16Wav(destination, durationMs)
        }
        if (cacheWaveform) {
            assertTrue(
                ProjectStorage.saveCachedWaveform(
                    projectDir,
                    WaveformExtractor.DEFAULT_TARGET_SAMPLE_COUNT,
                    cachedWaveform(),
                ),
            )
        }
        return projectDir
    }

    private fun cachedWaveform(): IntArray =
        IntArray(WaveformExtractor.DEFAULT_TARGET_SAMPLE_COUNT) { sampleIndex ->
            if (sampleIndex % 2 == 0) 8_000 else 16_000
        }

    private fun writePcm16Wav(file: File, durationMs: Int) {
        val sampleRate = 8_000
        val channelCount = 1
        val bytesPerSample = 2
        val sampleCount = (sampleRate.toLong() * durationMs / 1_000L).toInt()
        val dataSize = (sampleCount.toLong() * channelCount * bytesPerSample).toInt()
        val wav = ByteBuffer.allocate(WAV_HEADER_SIZE + dataSize).order(ByteOrder.LITTLE_ENDIAN)

        wav.put("RIFF".toByteArray(Charsets.US_ASCII))
        wav.putInt(36 + dataSize)
        wav.put("WAVE".toByteArray(Charsets.US_ASCII))
        wav.put("fmt ".toByteArray(Charsets.US_ASCII))
        wav.putInt(16)
        wav.putShort(1.toShort())
        wav.putShort(channelCount.toShort())
        wav.putInt(sampleRate)
        wav.putInt(sampleRate * channelCount * bytesPerSample)
        wav.putShort((channelCount * bytesPerSample).toShort())
        wav.putShort(16.toShort())
        wav.put("data".toByteArray(Charsets.US_ASCII))
        wav.putInt(dataSize)

        repeat(sampleCount) { sampleIndex ->
            val sample = if ((sampleIndex / 20) % 2 == 0) 12_000 else -12_000
            wav.putShort(sample.toShort())
        }
        file.writeBytes(wav.array())
    }

    private fun waitUntilTextExists(text: String) {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun clickLastNodeWithText(text: String) {
        val matchingNodes = composeRule.onAllNodesWithText(text)
        matchingNodes[matchingNodes.fetchSemanticsNodes().lastIndex].performClick()
    }

    private fun disposeEditorAndDeleteProject(
        showEditor: MutableState<Boolean>,
        projectTitle: String,
    ) {
        composeRule.runOnIdle { showEditor.value = false }
        composeRule.waitForIdle()
        ProjectStorage.deleteProject(context, projectTitle)
    }

    private fun uniqueProjectTitle(prefix: String): String =
        "$prefix ${System.nanoTime()}"

    private companion object {
        private const val WAV_HEADER_SIZE = 44
        private const val LONG_TEST_BEAT_DURATION_MS = 5 * 60 * 1_000
    }
}
