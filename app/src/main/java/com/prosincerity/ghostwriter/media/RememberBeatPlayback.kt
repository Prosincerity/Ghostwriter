package com.prosincerity.ghostwriter.media

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.awaitCancellation

/** Unbinding a screen leaves started playback running in the service. */
@Composable
internal fun rememberBeatPlayback(projectId: String, projectTitle: String = projectId): BeatPlaybackService? {
    val context = LocalContext.current.applicationContext
    var service by remember(projectId) { mutableStateOf<BeatPlaybackService?>(null) }
    LaunchedEffect(context, projectId, projectTitle) {
        // Frame callbacks run before drawing. Wait for the following frame as
        // well so the editor can draw once before service creation starts.
        withFrameNanos { }
        withFrameNanos { }
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                service = (binder as BeatPlaybackService.LocalBinder).service.also {
                    it.selectProject(projectId, projectTitle)
                }
            }
            override fun onServiceDisconnected(name: ComponentName?) { service = null }
        }
        val bound = context.bindService(
            Intent(context, BeatPlaybackService::class.java), connection, Context.BIND_AUTO_CREATE,
        )
        try {
            awaitCancellation()
        } finally {
            if (bound) context.unbindService(connection)
        }
    }
    return service
}
