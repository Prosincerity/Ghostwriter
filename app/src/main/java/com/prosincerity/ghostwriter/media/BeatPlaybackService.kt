package com.prosincerity.ghostwriter.media

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
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
import android.os.Handler
import android.os.IBinder
import android.os.Looper
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
    private val focus = PlaybackFocusState()
    private var foreground = false
    private var advertised = false
    private var destroying = false
    private var pausingForFocus = false
    private var project: String? = null
    private var beatTitle = ""
    private val notificationHandler = Handler(Looper.getMainLooper())
    private val notificationUpdates = PlaybackNotificationUpdater(
        schedule = { task, delay -> notificationHandler.postDelayed(task, delay); Unit },
        remove = { notificationHandler.removeCallbacks(it) },
        publish = ::postNotification,
    )
    private val artwork by lazy {
        val icon = applicationInfo.loadIcon(packageManager)
        Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888).also { bitmap ->
            icon.setBounds(0, 0, bitmap.width, bitmap.height)
            val canvas = Canvas(bitmap)
            canvas.scale(0.75f, 0.75f, bitmap.width / 2f, bitmap.height / 2f)
            icon.draw(canvas)
        }
    }

    private val playbackTitle get() = beatTitle.ifBlank { getString(R.string.app_name) }
    private val playbackSubtitle get() = listOfNotNull(
        getString(R.string.beat_playback_source), project?.takeIf { it.isNotBlank() },
    ).joinToString(" · ")
    private val loopLabel get() = getString(if (player.isLooping) R.string.beat_disable_loop else R.string.beat_enable_loop)
    private val loopIcon get() = if (player.isLooping) R.drawable.ic_beat_loop_on else R.drawable.ic_beat_loop

    private val focusListener = AudioManager.OnAudioFocusChangeListener(::onAudioFocusChange)

    internal fun onAudioFocusChange(change: Int) {
        if (destroying) return
        when (change) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (focus.onGain()) player.play()
            }
            AudioManager.AUDIOFOCUS_LOSS -> pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                focus.onTransientLoss(player.isPlaying)
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
            onPauseRequested = { if (!pausingForFocus) focus.onPauseRequested() })
        session.setCallback(object : MediaSession.Callback() {
            override fun onPlay() { player.play() }
            override fun onPause() { pause() }
            override fun onStop() { stopPlayback() }
            override fun onSeekTo(pos: Long) { player.seekTo(pos.coerceIn(0, Int.MAX_VALUE.toLong()).toInt()) }
            override fun onSkipToPrevious() { restartPlayback() }
            override fun onCustomAction(action: String, extras: Bundle?) {
                when (action) {
                    ACTION_RESTART -> restartPlayback()
                    ACTION_LOOP -> player.toggleLoop()
                }
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
        focus.onPauseRequested()
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
            ACTION_RESTART -> restartPlayback()
            ACTION_LOOP -> player.toggleLoop()
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
        focus.onPlaybackRequested()
        // Foreground status must precede audio-focus requests on Android 15+.
        startService(Intent(this, BeatPlaybackService::class.java))
        advertised = true
        session.isActive = true
        notificationUpdates.cancel()
        startForeground(NOTIFICATION_ID, notification())
        foreground = true
        val granted = if (focus.hasFocus) true else if (Build.VERSION.SDK_INT >= 26) {
            audioManager.requestAudioFocus(focusRequest!!) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            (audioManager.requestAudioFocus(focusListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        }
        focus.onRequestResult(granted)
        if (!granted) publishState()
        return granted
    }

    private fun pause() {
        focus.onPauseRequested()
        player.pause()
    }

    private fun stopPlayback() {
        // Remove the foreground notification before pause() can detach it. DETACH can
        // post a deferred notification, which would race the subsequent cancellation.
        clearNotification()
        pause()
        player.seekTo(0)
        session.isActive = false
        stopSelf()
    }

    private fun restartPlayback() {
        player.seekTo(0)
        player.play()
    }

    private fun abandonFocus() {
        focus.onAbandoned()
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
            .putString(MediaMetadata.METADATA_KEY_TITLE, playbackTitle)
            .putString(MediaMetadata.METADATA_KEY_ARTIST, playbackSubtitle)
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, playbackTitle)
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, playbackSubtitle)
            .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, artwork)
            .putBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON, artwork)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, player.durationMs.toLong()).build())
        session.setPlaybackState(PlaybackState.Builder()
            .setActions(PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PAUSE or
                PlaybackState.ACTION_PLAY_PAUSE or
                PlaybackState.ACTION_SEEK_TO)
            .addCustomAction(ACTION_RESTART, getString(R.string.beat_restart), R.drawable.ic_beat_restart)
            .addCustomAction(ACTION_LOOP, loopLabel, loopIcon)
            .setState(if (playing) PlaybackState.STATE_PLAYING else if (player.isReady)
                PlaybackState.STATE_PAUSED else PlaybackState.STATE_STOPPED,
                player.currentPositionMs.toLong(), if (playing) 1f else 0f).build())
        if (!playing && !focus.resumeOnFocusGain) abandonFocus()
        if (!player.isReady) {
            focus.onPauseRequested()
            abandonFocus()
            clearNotification()
            session.isActive = false
            stopSelf()
        } else if (advertised) {
            if (!focus.shouldKeepForeground(playing) && foreground) {
                stopForeground(STOP_FOREGROUND_DETACH)
                foreground = false
            }
            // Seek completion and transport actions can arrive in bursts. Media notifications
            // are exempt from notification permission, but still subject to update rate limits.
            notificationUpdates.request()
        }
    }

    @SuppressLint("NotificationPermission")
    private fun postNotification() {
        if (advertised && !destroying) {
            getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification())
        }
    }

    private fun notification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(this, CHANNEL)
            else @Suppress("DEPRECATION") Notification.Builder(this)
        val playing = player.isPlaying
        return builder.setSmallIcon(R.drawable.ic_music_note)
            .setContentTitle(playbackTitle).setContentText(playbackSubtitle)
            .setLargeIcon(artwork).setContentIntent(contentIntent())
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true).setOngoing(playing)
            .setDeleteIntent(actionIntent(ACTION_STOP))
            .addAction(Notification.Action.Builder(
                Icon.createWithResource(this, if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play),
                getString(if (playing) R.string.beat_pause else R.string.beat_play),
                actionIntent(if (playing) ACTION_PAUSE else ACTION_PLAY)).build())
            .addAction(Notification.Action.Builder(Icon.createWithResource(this, R.drawable.ic_beat_restart),
                getString(R.string.beat_restart), actionIntent(ACTION_RESTART)).build())
            .addAction(Notification.Action.Builder(Icon.createWithResource(this, loopIcon),
                loopLabel, actionIntent(ACTION_LOOP)).build())
            .setStyle(Notification.MediaStyle().setMediaSession(session.sessionToken).setShowActionsInCompactView(0, 1, 2))
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
        notificationUpdates.cancel()
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
        private const val ACTION_RESTART = "com.prosincerity.ghostwriter.RESTART_BEAT"
        private const val ACTION_LOOP = "com.prosincerity.ghostwriter.LOOP_BEAT"
        private const val ACTION_STOP = "com.prosincerity.ghostwriter.STOP_BEAT"
        private const val ACTION_FORGET = "com.prosincerity.ghostwriter.FORGET_PROJECT"
        private const val EXTRA_PROJECT = "project"

        fun forgetProject(context: Context, title: String) {
            context.startService(Intent(context, BeatPlaybackService::class.java)
                .setAction(ACTION_FORGET).putExtra(EXTRA_PROJECT, title))
        }
    }
}
