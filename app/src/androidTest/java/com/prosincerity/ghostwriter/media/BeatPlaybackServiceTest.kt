package com.prosincerity.ghostwriter.media

import android.app.Notification
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.IBinder
import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prosincerity.ghostwriter.MainActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

@RunWith(AndroidJUnit4::class)
class BeatPlaybackServiceTest {
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @Test
    fun mediaPresentation_includesArtworkBrandingAndPlaybackActions() {
        withService { service, _ ->
            onMain { service.player.play() }
            waitUntil { service.player.isPlaying }
            val notification = waitForMediaNotification("Pause", "Restart", "Disable loop")
            val controller = mediaController(notification)
            val metadata = controller.metadata!!

            assertEquals("Test instrumental", metadata.getString(MediaMetadata.METADATA_KEY_TITLE))
            assertEquals("[Ghostwriter] · Service test", metadata.getString(MediaMetadata.METADATA_KEY_ARTIST))
            assertEquals("[Ghostwriter] · Service test", notification.extras.getCharSequence(Notification.EXTRA_TEXT).toString())
            assertNotNull(notification.getLargeIcon())
            assertNotNull(metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART))
            assertNotNull(metadata.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON))
            assertEquals(listOf("Restart", "Disable loop"),
                controller.playbackState!!.customActions.map { it.name.toString() })
            assertEquals(0L, controller.playbackState!!.actions and PlaybackState.ACTION_STOP)
            assertEquals(listOf("Pause", "Restart", "Disable loop"),
                notification.actions.map { it.title.toString() })
        }
    }

    @Test
    fun mediaSessionActions_restartFromPausedAndKeepLoopStateInSync() {
        withService { service, _ ->
            val player = service.player
            onMain { player.play() }
            waitUntil { player.isPlaying }
            val controller = mediaController(waitForMediaNotification("Pause", "Restart", "Disable loop"))
            val actions = controller.playbackState!!.customActions
            val restart = actions.first { it.name.toString() == "Restart" }.action
            val loop = actions.first { it.name.toString() == "Disable loop" }.action

            onMain { player.seekTo(5_000); player.pause() }
            waitUntil { !player.isPlaying && player.currentPositionMs >= 5_000 }
            controller.transportControls.sendCustomAction(restart, null)
            waitUntil { player.isPlaying && player.currentPositionMs < 1_000 }

            controller.transportControls.sendCustomAction(loop, null)
            waitUntil { !player.isLooping }
            waitUntil { controller.playbackState!!.customActions.any { it.name.toString() == "Enable loop" } }
            assertEquals("Enable loop",
                waitForMediaNotification("Pause", "Restart", "Enable loop").actions[2].title.toString())
            onMain { player.toggleLoop() }
            waitUntil { controller.playbackState!!.customActions.any { it.name.toString() == "Disable loop" } }
            assertEquals("Disable loop",
                waitForMediaNotification("Pause", "Restart", "Disable loop").actions[2].title.toString())

            controller.transportControls.pause()
            waitUntil { !player.isPlaying }
            assertTrue(onMainValue { player.isReady })
            assertEquals("Play",
                waitForMediaNotification("Play", "Restart", "Disable loop").actions[0].title.toString())
        }
    }

    @Test
    fun notificationActions_restartToggleLoopAndKeepPausedPlayerAvailable() {
        withService { service, _ ->
            val player = service.player
            onMain { player.play() }
            waitUntil { player.isPlaying }
            onMain { player.seekTo(5_000); player.pause() }
            waitUntil { !player.isPlaying && player.currentPositionMs >= 5_000 }

            waitForMediaNotification("Play", "Restart", "Disable loop").actions[2].actionIntent.send()
            waitUntil { !player.isLooping }
            assertFalse(onMainValue { player.isPlaying })
            waitForMediaNotification("Play", "Restart", "Enable loop").actions[2].actionIntent.send()
            waitUntil { player.isLooping }
            waitForMediaNotification("Play", "Restart", "Disable loop").actions[1].actionIntent.send()
            waitUntil { player.isPlaying && player.currentPositionMs < 1_000 }
            waitForMediaNotification("Pause", "Restart", "Disable loop").actions[0].actionIntent.send()
            waitUntil { !player.isPlaying }
            assertTrue(onMainValue { player.isReady })
            assertEquals(listOf("Play", "Restart", "Disable loop"),
                waitForMediaNotification("Play", "Restart", "Disable loop").actions.map { it.title.toString() })
        }
    }

    @Test
    fun unboundPlayback_survivesBackground_andMediaControlsWorkWithoutActivity() {
        withService { service, unbind ->
            val player = service.player
            onMain { player.play() }
            waitUntil { player.isPlaying }
            val controller = mediaController(waitForMediaNotification("Pause", "Restart", "Disable loop"))
            unbind()
            composeRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
            val position = onMainValue { player.currentPositionMs }
            waitUntil { player.currentPositionMs > position + 250 }
            assertEquals(PlaybackState.STATE_PLAYING, controller.playbackState!!.state)

            controller.transportControls.pause()
            waitUntil { !player.isPlaying }
            controller.transportControls.play()
            waitUntil { player.isPlaying }
            controller.transportControls.stop()
            waitUntil { !player.isReady }
            waitUntil { context.getSystemService(NotificationManager::class.java).activeNotifications.isEmpty() }
            composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        }
    }

    @Test
    fun stoppingImmediatelyAfterResume_removesNotificationWhileBound_andAllowsPlayingAgain() {
        withService { service, _ ->
            val player = service.player
            onMain { player.play() }
            val controller = mediaController(waitForMediaNotification("Pause", "Restart", "Disable loop"))

            controller.transportControls.pause()
            waitUntil { !player.isPlaying }
            controller.transportControls.play()
            waitUntil { player.isPlaying }
            // Stop before the resumed foreground notification update has been published.
            controller.transportControls.stop()
            waitUntil { !player.isPlaying && player.currentPositionMs == 0 }
            assertTrue(onMainValue { player.isReady })
            waitUntil({ "Stopped playback notification was not removed" }) {
                context.getSystemService(NotificationManager::class.java).activeNotifications.isEmpty()
            }

            onMain { player.play() }
            waitUntil { player.isPlaying }
            waitForMediaNotification("Pause", "Restart", "Disable loop")
        }
    }

    @Test
    fun selectingAnotherProject_releasesBeat_andRemovesMediaControls() {
        withService { service, _ ->
            onMain { service.player.play() }
            waitUntil { service.player.isPlaying }
            onMain { service.selectProject("Second project") }
            assertFalse(onMainValue { service.player.isReady })
            waitUntil { context.getSystemService(NotificationManager::class.java).activeNotifications.isEmpty() }
        }
    }

    @Test
    fun selectingSameProject_doesNotInterruptPlayback() {
        withService { service, _ ->
            onMain { service.player.play() }
            waitUntil { service.player.currentPositionMs > 250 }
            onMain { service.selectProject("Service test") }
            assertTrue(onMainValue { service.player.isPlaying })
            assertTrue(onMainValue { service.player.currentPositionMs } >= 250)
        }
    }

    @Test
    fun transientFocusLoss_keepsUnboundServiceForeground_andResumesInBackground() {
        withService { service, unbind ->
            onMain { service.player.play() }
            waitForMediaNotification("Pause", "Restart", "Disable loop")
            unbind()
            composeRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
            try {
                // Deliver callbacks deterministically; another app taking real audio focus
                // is outside this test's control. Verify the actual service/notification state.
                for (loss in listOf(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)) {
                    onMain { service.onAudioFocusChange(loss) }
                    waitUntil { !service.player.isPlaying }
                    val notification = waitForMediaNotification("Play", "Restart", "Disable loop")
                    assertTrue("An interrupted beat must keep its foreground service",
                        notification.flags and Notification.FLAG_FOREGROUND_SERVICE != 0)

                    onMain { service.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN) }
                    waitUntil { service.player.isPlaying }
                    waitForMediaNotification("Pause", "Restart", "Disable loop")
                }
            } finally {
                composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
            }
        }
    }

    @Test
    fun editorPauseDuringFocusLoss_cancelsResume_andLeavesForeground() {
        withService { service, _ ->
            onMain { service.player.play() }
            waitForMediaNotification("Pause", "Restart", "Disable loop")
            onMain {
                service.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
                // The editor calls BeatPlayer directly, including when already paused.
                service.player.pause()
                service.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN)
            }

            assertFalse(onMainValue { service.player.isPlaying })
            val notification = waitForMediaNotification("Play", "Restart", "Disable loop")
            assertEquals(0, notification.flags and Notification.FLAG_FOREGROUND_SERVICE)
        }
    }

    @Test
    fun replacingProjectDuringFocusLoss_doesNotResumeOrRestoreNotification() {
        withService { service, _ ->
            onMain { service.player.play() }
            waitForMediaNotification("Pause", "Restart", "Disable loop")
            onMain {
                service.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
                service.selectProject("Replacement project")
                service.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN)
            }

            assertFalse(onMainValue { service.player.isReady })
            assertFalse(onMainValue { service.player.isPlaying })
            waitUntil { context.getSystemService(NotificationManager::class.java).activeNotifications.isEmpty() }
        }
    }

    /** NotificationManager can still return the previous buttons after player/session state changes. */
    private fun waitForMediaNotification(vararg expectedActions: String): Notification {
        val manager = context.getSystemService(NotificationManager::class.java)
        var notification: Notification? = null
        waitUntil({
            "Expected notification actions ${expectedActions.toList()}, " +
                "last observed ${notification?.actions?.map { it.title.toString() }}"
        }) {
            notification = manager.activeNotifications.singleOrNull()?.notification
            notification?.actions?.map { it.title.toString() } == expectedActions.toList()
        }
        return checkNotNull(notification)
    }

    private fun mediaController(notification: Notification): MediaController {
        @Suppress("DEPRECATION")
        val token = notification.extras.getParcelable<MediaSession.Token>(Notification.EXTRA_MEDIA_SESSION)!!
        return MediaController(context, token)
    }

    private fun withService(block: (BeatPlaybackService, () -> Unit) -> Unit) {
        var service: BeatPlaybackService? = null
        var bound = false
        val file = File(context.cacheDir, "service-playback-test.wav")
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                service = (binder as BeatPlaybackService.LocalBinder).service
            }
            override fun onServiceDisconnected(name: ComponentName?) { service = null }
        }
        val unbind = {
            onMain {
                if (bound) context.unbindService(connection)
                bound = false
            }
        }
        try {
            writeWav(file)
            onMain {
                bound = context.bindService(Intent(context, BeatPlaybackService::class.java),
                    connection, Context.BIND_AUTO_CREATE)
            }
            waitUntil { service != null }
            onMain {
                service!!.selectProject("Service test")
                service!!.setBeatTitle("Test instrumental")
                assertTrue(service!!.player.load(file))
            }
            block(service!!, unbind)
        } finally {
            onMain { service?.player?.release() }
            unbind()
            context.stopService(Intent(context, BeatPlaybackService::class.java))
            file.delete()
        }
    }

    private fun onMain(block: () -> Unit) = instrumentation.runOnMainSync(block)
    private fun <T> onMainValue(block: () -> T): T {
        var result: T? = null
        onMain { result = block() }
        return result!!
    }
    private fun waitUntil(message: () -> String = { "Playback condition timed out" }, condition: () -> Boolean) {
        try {
            composeRule.waitUntil(timeoutMillis = 5_000) { onMainValue(condition) }
        } catch (timeout: ComposeTimeoutException) {
            throw AssertionError(message(), timeout)
        }
    }

    private fun writeWav(file: File) {
        val dataSize = 8_000 * 2 * 30
        val bytes = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN)
        bytes.put("RIFF".toByteArray()).putInt(36 + dataSize).put("WAVEfmt ".toByteArray())
        bytes.putInt(16).putShort(1).putShort(1).putInt(8_000).putInt(16_000)
        bytes.putShort(2).putShort(16).put("data".toByteArray()).putInt(dataSize)
        repeat(dataSize / 2) { bytes.putShort(if (it % 40 < 20) 1_000 else -1_000) }
        file.writeBytes(bytes.array())
    }
}
