package com.prosincerity.ghostwriter.ui

import com.prosincerity.ghostwriter.ui.screens.parsePositiveBpm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProjectInfoBpmTest {

    @Test
    fun bpmParsing_acceptsPositiveValuesAndRejectsInvalidOnes() {
        assertEquals(90, parsePositiveBpm("90"))
        assertNull(parsePositiveBpm(""))
        assertNull(parsePositiveBpm("0"))
        assertNull(parsePositiveBpm("99999999999999999999"))
    }
}
