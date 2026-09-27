package com.prosincerity.ghostwriter.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.prosincerity.ghostwriter.MainActivity
import com.prosincerity.ghostwriter.R

/** Owns local playback beyond any Activity or Compose screen's lifetime. */
class BeatPlaybackService : Service() {
    inner class LocalBinder : Binder() {
        val service: BeatPlaybackService get() = this@BeatPlaybackService
    }

    lateinit var player: BeatPlayer
        private set
    private lateinit var session: MediaSession
    private lateinit var audioManager: AudioManager
    private var focusRequest: AudioFocusRequest? = null
    private var hasFocus = false
    private var foreground = false
    private var advertised = false
    private var destroying = false
    private var resumeOnFocusGain = false
    private var pausingForFocus = false
    private var project: String? = null
    private var beatTitle = ""

    private val focusListener = AudioManager.OnAudioFocusChangeListener { change ->
        when (change) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                hasFocus = true
                if (resumeOnFocusGain) {
                    resumeOnFocusGain = false
                    player.play()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                hasFocus = false
                resumeOnFocusGain = resumeOnFocusGain || player.isPlaying
                pausingForFocus = true
                try { player.pause() } finally { pausingForFocus = false }
            }
        }
    }
    private val noisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) pause()
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AudioManager::class.java)
        session = MediaSession(this, "Ghostwriter beat")
        @Suppress("DEPRECATION")
        session.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
        player = BeatPlayer(this, ::preparePlayback, ::publishState,
            onPauseRequested = { if (!pausingForFocus) resumeOnFocusGain = false })
        session.setCallback(object : MediaSession.Callback() {
            override fun onPlay() { player.play() }
            override fun onPause() { pause() }
            override fun onStop() { stopPlayback() }
            override fun onSeekTo(pos: Long) { player.seekTo(pos.coerceIn(0, Int.MAX_VALUE.toLong()).toInt()) }
            override fun onSkipToPrevious() { player.seekTo(0) }
            override fun onCustomAction(action: String, extras: Bundle?) {
                if (action == ACTION_STOP) stopPlayback()
            }
        })
        session.setSessionActivity(contentIntent())
        if (Build.VERSION.SDK_INT >= 26) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(CHANNEL, getString(R.string.beat_playback_channel), NotificationManager.IMPORTANCE_LOW),
            )
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                .setOnAudioFocusChangeListener(focusListener)
                .build()
        }
        ContextCompat.registerReceiver(this, noisyReceiver,
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY), ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onBind(intent: Intent?): IBinder = LocalBinder()

    fun selectProject(title: String) {
        if (project == title) return
        resumeOnFocusGain = false
        player.release()
        project = title
        beatTitle = ""
    }

    fun setBeatTitle(title: String) {
        beatTitle = title
        publishState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> player.play()
            ACTION_PAUSE -> pause()
            ACTION_STOP -> stopPlayback()
            ACTION_FORGET -> if (project == intent.getStringExtra(EXTRA_PROJECT)) {
                player.release()
                project = null
            }
        }
        if (!advertised) stopSelf(startId)
        // Never unexpectedly restart a beat after process death.
        return START_NOT_STICKY
    }

    private fun preparePlayback(): Boolean {
        resumeOnFocusGain = false
        // Foreground status must precede audio-focus requests on Android 15+.
        startService(Intent(this, BeatPlaybackService::class.java))
        advertised = true
        session.isActive = true
        startForeground(NOTIFICATION_ID, notification())
        foreground = true
        val granted = if (hasFocus) true else if (Build.VERSION.SDK_INT >= 26) {
            audioManager.requestAudioFocus(focusRequest!!) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            (audioManager.requestAudioFocus(focusListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        }
        hasFocus = granted
        if (!granted) publishState()
        return granted
    }

    private fun pause() {
        resumeOnFocusGain = false
        player.pause()
    }

    private fun stopPlayback() {
        pause()
        player.seekTo(0)
        clearNotification()
        session.isActive = false
        stopSelf()
    }

    private fun abandonFocus() {
        hasFocus = false
        if (Build.VERSION.SDK_INT >= 26) audioManager.abandonAudioFocusRequest(focusRequest!!)
        else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(focusListener)
        }
    }

    private fun publishState() {
        if (destroying) return
        val playing = player.isPlaying
        session.setMetadata(MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, beatTitle)
            .putString(MediaMetadata.METADATA_KEY_ARTIST, project.orEmpty())
            .putLong(MediaMetadata.METADATA_KEY_DURATION, player.durationMs.toLong()).build())
        session.setPlaybackState(PlaybackState.Builder()
            .setActions(PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PAUSE or
                PlaybackState.ACTION_PLAY_PAUSE or PlaybackState.ACTION_STOP or
                PlaybackState.ACTION_SEEK_TO or PlaybackState.ACTION_SKIP_TO_PREVIOUS)
            .addCustomAction(ACTION_STOP, getString(R.string.beat_stop), android.R.drawable.ic_media_pause)
            .setState(if (playing) PlaybackState.STATE_PLAYING else if (player.isReady)
                PlaybackState.STATE_PAUSED else PlaybackState.STATE_STOPPED,
                player.currentPositionMs.toLong(), if (playing) 1f else 0f).build())
        if (!playing && !resumeOnFocusGain) abandonFocus()
        if (!player.isReady) {
            resumeOnFocusGain = false
            abandonFocus()
            clearNotification()
            session.isActive = false
            stopSelf()
        } else if (advertised) {
            if (!playing && foreground) {
                stopForeground(STOP_FOREGROUND_DETACH)
                foreground = false
            }
            getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification())
        }
    }

    private fun notification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(this, CHANNEL)
            else @Suppress("DEPRECATION") Notification.Builder(this)
        val playing = player.isPlaying
        return builder.setSmallIcon(R.drawable.ic_music_note)
            .setContentTitle(beatTitle.ifBlank { getString(R.string.app_name) })
            .setContentText(project).setContentIntent(contentIntent())
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true).setOngoing(playing)
            .setDeleteIntent(actionIntent(ACTION_STOP))
            .addAction(Notification.Action.Builder(
                Icon.createWithResource(this, if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play),
                getString(if (playing) R.string.beat_pause else R.string.beat_play),
                actionIntent(if (playing) ACTION_PAUSE else ACTION_PLAY)).build())
            .addAction(Notification.Action.Builder(Icon.createWithResource(this, android.R.drawable.ic_menu_close_clear_cancel),
                getString(R.string.beat_stop), actionIntent(ACTION_STOP)).build())
            .setStyle(Notification.MediaStyle().setMediaSession(session.sessionToken).setShowActionsInCompactView(0, 1))
            .build()
    }

    private fun contentIntent() = PendingIntent.getActivity(this, 0,
        Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    private fun actionIntent(action: String) = PendingIntent.getService(this, 0,
        Intent(this, BeatPlaybackService::class.java).setAction(action),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    private fun clearNotification() {
        advertised = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        foreground = false
        getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
    }

    override fun onDestroy() {
        destroying = true
        player.release()
        abandonFocus()
        clearNotification()
        session.release()
        unregisterReceiver(noisyReceiver)
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL = "beat_playback"
        private const val NOTIFICATION_ID = 1001
        private const val ACTION_PLAY = "com.prosincerity.ghostwriter.PLAY_BEAT"
        private const val ACTION_PAUSE = "com.prosincerity.ghostwriter.PAUSE_BEAT"
        private const val ACTION_STOP = "com.prosincerity.ghostwriter.STOP_BEAT"
        private const val ACTION_FORGET = "com.prosincerity.ghostwriter.FORGET_PROJECT"
        private const val EXTRA_PROJECT = "project"

        fun forgetProject(context: Context, title: String) {
            context.startService(Intent(context, BeatPlaybackService::class.java)
                .setAction(ACTION_FORGET).putExtra(EXTRA_PROJECT, title))
        }
    }
}
