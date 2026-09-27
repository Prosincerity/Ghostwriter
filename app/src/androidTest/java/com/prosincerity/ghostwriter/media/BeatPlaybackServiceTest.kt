package com.prosincerity.ghostwriter.media

import android.app.Notification
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.IBinder
import android.os.SystemClock
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
    fun unboundPlayback_survivesBackground_andMediaControlsWorkWithoutActivity() {
        withService { service, unbind ->
            val player = service.player
            onMain { player.play() }
            waitUntil { player.isPlaying }
            val notification = context.getSystemService(NotificationManager::class.java)
                .activeNotifications.single().notification
            @Suppress("DEPRECATION")
            val token = notification.extras.getParcelable<MediaSession.Token>(Notification.EXTRA_MEDIA_SESSION)!!
            val controller = MediaController(context, token)
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
    private fun waitUntil(condition: () -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 5_000
        while (!onMainValue(condition) && SystemClock.uptimeMillis() < deadline) SystemClock.sleep(20)
        assertTrue("Playback condition timed out", onMainValue(condition))
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
