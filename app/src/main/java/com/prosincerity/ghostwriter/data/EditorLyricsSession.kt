package com.prosincerity.ghostwriter.data

import java.io.File

/** Serializes lyric writes from one visit to the editor. */
internal class EditorLyricsSession(
    private val projectDir: File,
    private val projectTitle: String,
) {
    private var finished = false

    @Synchronized
    fun saveManual(lyrics: String, keepCount: Int): Boolean =
        !finished && ProjectStorage.saveManual(projectDir, projectTitle, lyrics, keepCount)

    @Synchronized
    fun autosave(lyrics: String, keepCount: Int) {
        if (finished) return
        ProjectStorage.rotateAndSave(projectDir, lyrics, keepCount)
    }

    @Synchronized
    fun finish(lyrics: String, keepCount: Int) {
        if (finished) return
        // A coroutine already dispatched to IO may resume after screen disposal.
        // Finish under the same monitor as ongoing writes and reject later ones.
        finished = true
        ProjectStorage.rotateAndSave(projectDir, lyrics, keepCount)
    }
}
