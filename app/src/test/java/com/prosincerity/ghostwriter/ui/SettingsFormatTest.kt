package com.prosincerity.ghostwriter.ui

import com.prosincerity.ghostwriter.ui.components.formatPlaybackTime
import com.prosincerity.ghostwriter.ui.screens.formatInterval
import com.prosincerity.ghostwriter.ui.screens.formatTypographyNumber
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsFormatTest {

    @Test
    fun formatPlaybackTime_displaysMinutesAndPaddedSeconds() {
        assertEquals("0:00", formatPlaybackTime(-1))
        assertEquals("0:00", formatPlaybackTime(0))
        assertEquals("0:30", formatPlaybackTime(30_999))
        assertEquals("2:30", formatPlaybackTime(150_000))
        assertEquals("60000:00", formatPlaybackTime(3_600_000_000L))
    }

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
    fun typographyNumbers_keepFractionalSpacingWithoutUnnecessaryZeros() {
        assertEquals("0", formatTypographyNumber(0f))
        assertEquals("2", formatTypographyNumber(2f))
        assertEquals("-0.5", formatTypographyNumber(-0.5f))
        assertEquals("1.15", formatTypographyNumber(1.15f))
        assertEquals("0.25", formatTypographyNumber(0.25f))
        assertEquals("1.23", formatTypographyNumber(1.2345f))
        assertEquals("7.78", formatTypographyNumber(7.777f))
        assertEquals("10", formatTypographyNumber(10f))
    }
}
