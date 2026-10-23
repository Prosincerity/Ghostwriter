package com.prosincerity.ghostwriter.editor.data

import com.prosincerity.ghostwriter.projects.data.ProjectMetadata
import com.prosincerity.ghostwriter.projects.data.ProjectStorage

import java.io.File
import java.io.IOException

internal data class ImportedProjectBeat(val file: File, val metadata: ProjectMetadata)

/**
 * Import before saving the shared folder, then return its latest metadata.
 * The caller owns background dispatch, serialization, and the document stream.
 */
internal fun importProjectBeat(
    projectDir: File,
    projectTitle: String,
    originalName: String?,
    waveformDirectory: File,
    persistLyrics: (File) -> Boolean,
    copyBeat: (File) -> Unit,
): ImportedProjectBeat {
    val name = originalName ?: "beat.mp3"
    val extension = name.substringAfterLast('.', "").lowercase()
    require(extension in ProjectStorage.SUPPORTED_AUDIO_EXTENSIONS) {
        "Choose an MP3, WAV, OGG, FLAC, M4A, or AAC file"
    }

    val assigned = ProjectStorage.assignBeatToProject(
        projectDir = projectDir,
        originalName = name,
        waveformDirectory = waveformDirectory,
        copyAction = copyBeat,
    )
    if (!persistLyrics(projectDir)) throw IOException("Couldn't update the lyric folder; local draft kept")
    return ImportedProjectBeat(assigned, ProjectStorage.loadMetadata(projectDir, projectTitle))
}
