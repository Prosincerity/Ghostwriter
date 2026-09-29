package com.prosincerity.ghostwriter.media

/** Coalesces playback callbacks to limit notification update bursts. */
internal class PlaybackNotificationUpdater(
    private val schedule: (Runnable, Long) -> Unit,
    private val remove: (Runnable) -> Unit,
    private val publish: () -> Unit,
) {
    private var pending = false
    private val update = Runnable {
        pending = false
        // Read the current player state when publishing, rather than queueing stale buttons.
        publish()
    }

    fun request() {
        if (pending) return
        pending = true
        schedule(update, 500L)
    }

    fun cancel() {
        remove(update)
        pending = false
    }
}
