package com.prosincerity.ghostwriter.editor.ui

import com.prosincerity.ghostwriter.waveform.domain.shouldWarnBeforeWaveformExtraction
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WaveformWarningTest {

    @Test
    fun warningStartsAtFiveMinutes() {
        assertFalse(shouldWarnBeforeWaveformExtraction(null))
        assertFalse(shouldWarnBeforeWaveformExtraction(299_999L))
        assertTrue(shouldWarnBeforeWaveformExtraction(300_000L))
        assertTrue(shouldWarnBeforeWaveformExtraction(600_000L))
    }
}
