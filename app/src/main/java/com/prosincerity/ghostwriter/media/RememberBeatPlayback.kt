package com.prosincerity.ghostwriter.media

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/** Unbinding a screen leaves started playback running in the service. */
@Composable
internal fun rememberBeatPlayback(projectTitle: String): BeatPlaybackService? {
    val context = LocalContext.current.applicationContext
    var service by remember(projectTitle) { mutableStateOf<BeatPlaybackService?>(null) }
    DisposableEffect(context, projectTitle) {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                service = (binder as BeatPlaybackService.LocalBinder).service.also {
                    it.selectProject(projectTitle)
                }
            }
            override fun onServiceDisconnected(name: ComponentName?) { service = null }
        }
        val bound = context.bindService(
            Intent(context, BeatPlaybackService::class.java), connection, Context.BIND_AUTO_CREATE,
        )
        onDispose { if (bound) context.unbindService(connection) }
    }
    return service
}
