package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.ProjectMetadata

internal data class ProjectInfoForm(
    val bpmInput: String = "",
    val keyInput: String = "",
    val timeSignatureInput: String = "",
    val notesInput: String = "",
) {
    val isBpmInvalid: Boolean
        get() = bpmInput.isNotBlank() && parsePositiveBpm(bpmInput) == null

    fun edit(event: ProjectInfoEdit): ProjectInfoForm = when (event) {
        is ProjectInfoEdit.BpmChanged -> copy(bpmInput = event.input.filter { it.isDigit() })
        is ProjectInfoEdit.KeyChanged -> copy(keyInput = event.input)
        is ProjectInfoEdit.TimeSignatureChanged -> copy(timeSignatureInput = event.input)
        is ProjectInfoEdit.NotesChanged -> copy(notesInput = event.input)
    }

    /** Invalid BPM input must not silently clear the project's saved tempo. */
    fun applyTo(metadata: ProjectMetadata): ProjectMetadata? =
        if (isBpmInvalid) null else metadata.copy(
            bpm = parsePositiveBpm(bpmInput),
            key = keyInput.trimmedOrNull(),
            timeSignature = timeSignatureInput.trimmedOrNull(),
            notes = notesInput.trimmedOrNull(),
        )

    companion object {
        fun fromMetadata(metadata: ProjectMetadata): ProjectInfoForm = ProjectInfoForm(
            bpmInput = metadata.bpm?.toString().orEmpty(),
            keyInput = metadata.key.orEmpty(),
            timeSignatureInput = metadata.timeSignature.orEmpty(),
            notesInput = metadata.notes.orEmpty(),
        )
    }
}

internal sealed interface ProjectInfoEdit {
    data class BpmChanged(val input: String) : ProjectInfoEdit
    data class KeyChanged(val input: String) : ProjectInfoEdit
    data class TimeSignatureChanged(val input: String) : ProjectInfoEdit
    data class NotesChanged(val input: String) : ProjectInfoEdit
}

internal fun parsePositiveBpm(input: String): Int? = input.toIntOrNull()?.takeIf { it > 0 }

private fun String.trimmedOrNull(): String? = trim().ifBlank { null }
