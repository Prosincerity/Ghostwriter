package com.prosincerity.ghostwriter.ui.screens

import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.content.Context
import android.content.ContextWrapper
import android.content.ServiceConnection
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import kotlinx.coroutines.test.StandardTestDispatcher
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prosincerity.ghostwriter.data.ProjectStorage
import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.data.WaveformMarker
import com.prosincerity.ghostwriter.logic.WaveformExtractor
import com.prosincerity.ghostwriter.media.BeatPlaybackService
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import com.prosincerity.ghostwriter.ui.theme.GhostColorScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class EditorScreenTest {

    @get:Rule
    // Queue resumptions after IO instead of running recomposition on the IO worker.
    val composeRule = createComposeRule(StandardTestDispatcher())

    private val feedback = mutableListOf<HapticFeedbackType>()
    private val haptics = object : HapticFeedback {
        override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
            feedback += hapticFeedbackType
        }
    }

    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun awaitingPlaybackConnection_onEveryEntryDrawsDarkBackground() {
        // Hold service connections pending, so this exercises the otherwise brief
        // first composition rather than waiting for the fully loaded editor.
        val connections = mutableSetOf<ServiceConnection>()
        val pendingContext = object : ContextWrapper(context) {
            override fun getApplicationContext(): Context = this

            override fun bindService(intent: Intent, connection: ServiceConnection, flags: Int): Boolean {
                connections += connection
                return true
            }

            override fun unbindService(connection: ServiceConnection) {
                assertTrue(connections.remove(connection))
            }
        }
        val visible = mutableStateOf(true)
        composeRule.setContent {
            CompositionLocalProvider(LocalContext provides pendingContext) {
                GhostwriterTheme {
                    if (visible.value) {
                        EditorScreen("Pending playback", onBack = {}, onOpenSettings = {})
                    }
                }
            }
        }

        repeat(3) {
            composeRule.runOnIdle { assertEquals(1, connections.size) }
            val pixels = composeRule.onRoot().captureToImage().toPixelMap()
            assertTrue("Waiting editor must fill the screen", pixels.width > 1 && pixels.height > 1)
            for ((x, y) in listOf(0 to 0, (pixels.width - 1) to (pixels.height - 1))) {
                assertEquals(GhostColorScheme.background, pixels[x, y])
            }
            composeRule.runOnIdle { visible.value = false }
            composeRule.runOnIdle { assertTrue(connections.isEmpty()) }
            if (it < 2) composeRule.runOnIdle { visible.value = true }
        }
    }

    @Test
    fun leavingAndReturningToEditor_keepsBeatPlaying_andSystemControlsStopIt() {
        val title = uniqueProjectTitle("Background beat")
        val visible = mutableStateOf(true)
        createAssignedBeat(title, durationMs = 30_000, cacheWaveform = true)
        try {
            setEditorContent(title, visible)
            waitUntilTextExists("editor-fixture")
            composeRule.onNodeWithContentDescription("Play").performClick()
            composeRule.waitUntil(5_000) {
                context.getSystemService(NotificationManager::class.java).activeNotifications.isNotEmpty()
            }
            val notification = context.getSystemService(NotificationManager::class.java)
                .activeNotifications.single().notification
            @Suppress("DEPRECATION")
            val token = notification.extras.getParcelable<MediaSession.Token>(Notification.EXTRA_MEDIA_SESSION)!!
            val controller = MediaController(context, token)
            controller.transportControls.seekTo(5_000)
            composeRule.waitUntil(5_000) { (controller.playbackState?.position ?: 0) >= 4_750 }

            composeRule.runOnIdle { visible.value = false }
            composeRule.waitForIdle()
            assertEquals(PlaybackState.STATE_PLAYING, controller.playbackState!!.state)
            composeRule.runOnIdle { visible.value = true }
            waitUntilTextExists("editor-fixture")
            assertEquals(PlaybackState.STATE_PLAYING, controller.playbackState!!.state)
            assertTrue(controller.playbackState!!.position >= 4_750)

            controller.transportControls.pause()
            composeRule.waitUntil(5_000) { controller.playbackState?.state == PlaybackState.STATE_PAUSED }
            controller.transportControls.play()
            composeRule.waitUntil(5_000) { controller.playbackState?.state == PlaybackState.STATE_PLAYING }
            controller.transportControls.stop()
            composeRule.waitUntil(5_000) {
                context.getSystemService(NotificationManager::class.java).activeNotifications.isEmpty()
            }
            assertEquals(PlaybackState.STATE_PAUSED, controller.playbackState!!.state)
        } finally {
            disposeEditorAndDeleteProject(visible, title)
        }
    }

    @Test
    fun emptyEditor_showsCoreControlsAndInvokesNavigation() {
        val projectTitle = uniqueProjectTitle("Editor controls")
        val showEditor = mutableStateOf(true)
        var openedSettings = false
        var openedDictionary = false

        try {
            composeRule.setContent {
                if (showEditor.value) {
                    GhostwriterTheme {
                        EditorScreen(
                            projectTitle = projectTitle,
                            onBack = { showEditor.value = false },
                            onOpenSettings = { openedSettings = true },
                            onOpenDictionary = { openedDictionary = true },
                        )
                    }
                }
            }

            waitUntilTextExists(projectTitle)
            composeRule.onNodeWithText(projectTitle).assertExists()
            composeRule.onNodeWithText("Import beat").assertExists()
            composeRule.onNodeWithText("Start writing...").assertExists()
            composeRule.onNodeWithContentDescription("Project Info").assertExists()
            composeRule.onNodeWithContentDescription("Save").assertExists()
            composeRule.onNodeWithContentDescription("Dictionary").performClick()
            composeRule.runOnIdle { assertTrue(openedDictionary) }

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

            waitUntilTextExists(projectTitle)
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

            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule.onAllNodes(hasSetTextAction()).fetchSemanticsNodes().size == 1
            }
            composeRule.onNode(hasSetTextAction()).performTextInput(lyrics)
            composeRule.onNodeWithContentDescription("Save").performClick()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                ProjectStorage.loadLatest(projectDir) == lyrics
            }
            composeRule.runOnIdle { assertTrue(feedback.isEmpty()) }
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
            waitUntilTextExists("0:00 / 0:05")

            composeRule.onNodeWithTag("Waveform").performTouchInput { longClick(center) }
            composeRule.onNodeWithText("Add marker").assertExists()
            composeRule.onNodeWithText("Marker name").performTextInput("Hook")
            composeRule.onNodeWithText("Add").performClick()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                ProjectStorage.loadMetadata(projectDir, projectTitle).markers.singleOrNull()?.label == "Hook"
            }
            val originalPosition = ProjectStorage.loadMetadata(projectDir, projectTitle)
                .markers.single().positionMs
            composeRule.waitUntil(5_000) { feedback.size == 2 }
            composeRule.runOnIdle {
                assertEquals(List(2) { HapticFeedbackType.SegmentTick }, feedback)
            }
            val storedMarker = ProjectStorage.loadMetadata(projectDir, projectTitle).markers.single()
            assertEquals(8000, storedMarker.sampleRate)
            assertEquals(originalPosition * 8, storedMarker.frameIndex)

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
            composeRule.onNodeWithText("Save").performClick()
            composeRule.waitUntil(timeoutMillis = 5_000) {
                ProjectStorage.loadMetadata(projectDir, projectTitle).markers.singleOrNull()?.label == "Chorus"
            }
            composeRule.runOnIdle { assertEquals(2, feedback.size) }

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
            composeRule.waitUntil(5_000) { feedback.size == 3 }
            composeRule.runOnIdle { assertEquals(HapticFeedbackType.LongPress, feedback.last()) }
        } finally {
            disposeEditorAndDeleteProject(showEditor, projectTitle)
        }
    }

    @Test
    fun openingLegacyLoop_migratesFramesRejectsInvalidMovesAndAcceptsRecovery() {
        val title = uniqueProjectTitle("Legacy loop")
        val visible = mutableStateOf(true)
        val directory = createAssignedBeat(title, durationMs = 5000, cacheWaveform = true)
        val markers = listOf(
            WaveformMarker("Start", 1000, MarkerLoopRole.START),
            WaveformMarker("End", 4000, MarkerLoopRole.END),
        )
        assertTrue(ProjectStorage.saveMetadata(directory, ProjectStorage.loadMetadata(directory, title).copy(markers = markers)))
        try {
            setEditorContent(title, visible)
            waitUntilTextExists("editor-fixture")
            waitUntilTextExists("0:00 / 0:05")
            composeRule.waitUntil(10000) {
                val saved = ProjectStorage.loadMetadata(directory, title).markers
                saved.size == 2 && saved.all { it.sampleRate == 8000 }
            }
            val migrated = ProjectStorage.loadMetadata(directory, title).markers
            assertEquals(listOf(8000L, 32000L), migrated.map { it.frameIndex })
            for ((label, invalidPosition) in listOf("Start" to 4000f, "Start" to 4500f, "End" to 500f)) {
                composeRule.onNodeWithTag("Waveform marker $label").performSemanticsAction(SemanticsActions.SetProgress) {
                    assertTrue(it(invalidPosition))
                }
                composeRule.onNodeWithTag("Waveform marker $label").assert(hasProgressBarRangeInfo(
                    ProgressBarRangeInfo(migrated.first { it.label == label }.positionMs.toFloat(), 0f..5000f),
                ))
                composeRule.runOnIdle { assertEquals(migrated, ProjectStorage.loadMetadata(directory, title).markers) }
            }
            composeRule.onNodeWithTag("Waveform marker Start").performSemanticsAction(SemanticsActions.SetProgress) {
                assertTrue(it(2000f))
            }
            composeRule.waitUntil(5000) {
                ProjectStorage.loadMetadata(directory, title).markers.first().frameIndex == 16000L
            }
            assertEquals(migrated[1], ProjectStorage.loadMetadata(directory, title).markers[1])
        } finally {
            disposeEditorAndDeleteProject(visible, title)
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
            val reassignButtons = composeRule.onAllNodesWithText("Reassign")
            reassignButtons[reassignButtons.fetchSemanticsNodes().lastIndex].performClick()

            waitUntilTextExists("No beat selected")
            composeRule.waitUntil(timeoutMillis = 5_000) {
                val metadata = ProjectStorage.loadMetadata(projectDir, projectTitle)
                ProjectStorage.getProjectBeatFile(projectDir, metadata) == null &&
                    metadata.beatFile == null && metadata.beatOriginalName == null
            }
            composeRule.waitUntil(5_000) { feedback.isNotEmpty() }
            composeRule.runOnIdle { assertEquals(listOf(HapticFeedbackType.LongPress), feedback) }
        } finally {
            disposeEditorAndDeleteProject(showEditor, projectTitle)
        }
    }

    @Test
    fun processingBeat_disablesDictionaryUntilWaveformIsReady() {
        val projectTitle = uniqueProjectTitle("Editor dictionary processing")
        val showEditor = mutableStateOf(true)
        var openedDictionary = false
        val storageLocked = CountDownLatch(1)
        val releaseStorage = CountDownLatch(1)
        var storageThread: Thread? = null
        val projectDir = createAssignedBeat(
            projectTitle, LONG_TEST_BEAT_DURATION_MS, cacheWaveform = false,
        )

        try {
            setEditorContent(projectTitle, showEditor) { openedDictionary = true }
            waitUntilTextExists("Long audio file")

            // Hold the storage monitor used at extraction startup so the loading
            // state can be checked without racing the decoder on faster devices.
            storageThread = Thread {
                synchronized(ProjectStorage) {
                    storageLocked.countDown()
                    if (releaseStorage.await(30, TimeUnit.SECONDS)) {
                        // Supply the cache before extraction resumes to keep this
                        // navigation test independent of decoder speed.
                        ProjectStorage.saveCachedWaveform(
                            projectDir,
                            WaveformExtractor.DEFAULT_TARGET_SAMPLE_COUNT,
                            cachedWaveform(),
                        )
                    }
                }
            }.apply { start() }
            assertTrue(storageLocked.await(5, TimeUnit.SECONDS))
            composeRule.onNodeWithText("Process anyway").performClick()
            composeRule.onNodeWithContentDescription("Dictionary").assertIsNotEnabled()
                .performClick()
            composeRule.runOnIdle { assertFalse(openedDictionary) }

            releaseStorage.countDown()
            waitUntilTextExists("editor-fixture")
            composeRule.onNodeWithContentDescription("Dictionary").assertIsEnabled()
                .performClick()
            composeRule.runOnIdle { assertTrue(openedDictionary) }
        } finally {
            releaseStorage.countDown()
            storageThread?.join(5_000)
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
            composeRule.onNodeWithContentDescription("Play").assertIsEnabled().performClick()
            composeRule.onNodeWithContentDescription("Pause").performClick()
            composeRule.onNodeWithContentDescription("Play").assertExists()
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
            composeRule.onNodeWithContentDescription("Play").assertExists()
            composeRule.onNodeWithContentDescription("Pause").assertDoesNotExist()
        } finally {
            disposeEditorAndDeleteProject(showEditor, projectTitle)
        }
    }

    private fun setEditorContent(
        projectTitle: String,
        showEditor: MutableState<Boolean>,
        onOpenDictionary: () -> Unit = {},
    ) {
        composeRule.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides haptics) {
                if (showEditor.value) {
                    GhostwriterTheme {
                        EditorScreen(
                            projectTitle = projectTitle,
                            onBack = { showEditor.value = false },
                            onOpenSettings = {},
                            onOpenDictionary = onOpenDictionary,
                        )
                    }
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

    private fun disposeEditorAndDeleteProject(
        showEditor: MutableState<Boolean>,
        projectTitle: String,
    ) {
        composeRule.runOnIdle { showEditor.value = false }
        composeRule.waitForIdle()
        context.stopService(Intent(context, BeatPlaybackService::class.java))
        ProjectStorage.deleteProject(context, projectTitle)
    }

    private fun uniqueProjectTitle(prefix: String): String =
        "$prefix ${System.nanoTime()}"

    private companion object {
        private const val WAV_HEADER_SIZE = 44
        private const val LONG_TEST_BEAT_DURATION_MS = 5 * 60 * 1_000
    }
}
