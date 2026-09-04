package com.ghostwriter.exe.ui

import com.ghostwriter.exe.data.Settings
import com.ghostwriter.exe.ui.screens.formatInterval
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsFormatTest {

    @Test
    fun formatInterval_secondsUnderOneMinute() {
        assertEquals("10 seconds", formatInterval(10))
        assertEquals("30 seconds", formatInterval(30))
    }

    @Test
    fun formatInterval_exactMinutes() {
        assertEquals("1 minute", formatInterval(60))
        assertEquals("2 minutes", formatInterval(120))
        assertEquals("5 minutes", formatInterval(300))
    }

    @Test
    fun settingsConstants_areValid() {
        assertTrue(Settings.INTERVAL_OPTIONS_SECONDS.contains(Settings.DEFAULT_INTERVAL_SECONDS))
        assertTrue(Settings.COUNT_OPTIONS.contains(Settings.DEFAULT_AUTOSAVE_COUNT))
    }
}
