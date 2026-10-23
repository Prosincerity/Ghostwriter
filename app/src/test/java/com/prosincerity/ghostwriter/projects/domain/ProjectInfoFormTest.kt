package com.prosincerity.ghostwriter.projects.domain

import com.prosincerity.ghostwriter.waveform.domain.MarkerLoopRole
import com.prosincerity.ghostwriter.projects.data.ProjectMetadata
import com.prosincerity.ghostwriter.waveform.domain.WaveformMarker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProjectInfoFormTest {
    private val original = ProjectMetadata(
        title = "Track", bpm = 80, key = "C Minor", timeSignature = "4/4", notes = "Draft",
        beatFile = "beat.wav", beatOriginalName = "Instrumental.wav",
        markers = listOf(WaveformMarker("Start", 1000, MarkerLoopRole.START, 44100, 44100)),
        createdAt = 10, updatedAt = 20, version = 3,
    )

    @Test fun fromMetadata_populatesEditableFields() {
        assertEquals(ProjectInfoForm("80", "C Minor", "4/4", "Draft"), ProjectInfoForm.fromMetadata(original))
    }

    @Test fun fromMetadata_displaysAbsentOptionalValuesAsEmptyText() {
        val metadata = ProjectMetadata(title = "Track", createdAt = 10, updatedAt = 20)
        assertEquals(ProjectInfoForm(), ProjectInfoForm.fromMetadata(metadata))
    }

    @Test fun bpmChange_filtersNonDigitsWithoutChangingOtherFields() {
        val form = ProjectInfoForm.fromMetadata(original)
        assertEquals(form.copy(bpmInput = "905"), form.edit(ProjectInfoEdit.BpmChanged(" +9a0.5 bpm ")))
        assertEquals("80", form.bpmInput)
    }

    @Test fun keyChange_keepsRawTextUntilSaving() {
        val form = ProjectInfoForm.fromMetadata(original)
        assertEquals(form.copy(keyInput = " D Minor "), form.edit(ProjectInfoEdit.KeyChanged(" D Minor ")))
    }

    @Test fun timeSignatureChange_keepsRawTextUntilSaving() {
        val form = ProjectInfoForm.fromMetadata(original)
        assertEquals(form.copy(timeSignatureInput = " 3/4 "),
            form.edit(ProjectInfoEdit.TimeSignatureChanged(" 3/4 ")))
    }

    @Test fun notesChange_keepsMultilineDraftUntilSaving() {
        val form = ProjectInfoForm.fromMetadata(original)
        assertEquals(form.copy(notesInput = " Verse\n\nChorus "),
            form.edit(ProjectInfoEdit.NotesChanged(" Verse\n\nChorus ")))
    }

    @Test fun save_normalizesFieldsAndPreservesNonEditableMetadata() {
        val form = ProjectInfoForm("95", " D Minor ", " 3/4 ", " Verse\n\nChorus ")
        assertEquals(original.copy(bpm = 95, key = "D Minor", timeSignature = "3/4", notes = "Verse\n\nChorus"),
            form.applyTo(original))
        assertEquals("Draft", original.notes)
    }

    @Test fun save_usesCurrentNonEditableMetadata() {
        val form = ProjectInfoForm.fromMetadata(original).edit(ProjectInfoEdit.KeyChanged("D Minor"))
        val current = original.copy(title = "Renamed track", beatFile = "replacement.wav", markers = emptyList(), updatedAt = 30)
        assertEquals(current.copy(key = "D Minor"), form.applyTo(current))
    }

    @Test fun save_clearsEmptyAndWhitespaceOnlyOptionalFields() {
        val form = ProjectInfoForm(bpmInput = " \t", keyInput = "", timeSignatureInput = " \t", notesInput = "\n ")
        assertFalse(form.isBpmInvalid)
        assertEquals(original.copy(bpm = null, key = null, timeSignature = null, notes = null), form.applyTo(original))
    }

    @Test fun save_acceptsPositiveBpmBoundaries() {
        for (bpm in listOf("1", Int.MAX_VALUE.toString())) {
            val form = ProjectInfoForm(bpmInput = bpm)
            assertFalse(form.isBpmInvalid)
            assertEquals(bpm.toInt(), form.applyTo(original)?.bpm)
        }
    }

    @Test fun save_rejectsInvalidBpmWithoutClearingExistingValue() {
        for (bpm in listOf("0", "-1", "2147483648", "99999999999999999999", "90x", " 90 ")) {
            val form = ProjectInfoForm(bpmInput = bpm)
            assertTrue(form.isBpmInvalid)
            assertNull(form.applyTo(original))
            assertEquals(80, original.bpm)
        }
    }

    @Test fun correctingInvalidBpm_allowsSavingAgain() {
        val form = ProjectInfoForm(bpmInput = "0").edit(ProjectInfoEdit.BpmChanged("90"))
        assertFalse(form.isBpmInvalid)
        assertEquals(90, form.applyTo(original)?.bpm)
    }

    @Test fun clearingInvalidBpm_allowsRemovingTheSavedBpm() {
        val form = ProjectInfoForm(bpmInput = "0").edit(ProjectInfoEdit.BpmChanged(""))
        assertFalse(form.isBpmInvalid)
        assertNull(checkNotNull(form.applyTo(original)).bpm)
    }
}
