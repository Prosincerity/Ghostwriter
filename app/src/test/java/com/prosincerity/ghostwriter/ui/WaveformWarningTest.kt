package com.prosincerity.ghostwriter.ui

import com.prosincerity.ghostwriter.ui.screens.shouldWarnBeforeWaveformExtraction
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
