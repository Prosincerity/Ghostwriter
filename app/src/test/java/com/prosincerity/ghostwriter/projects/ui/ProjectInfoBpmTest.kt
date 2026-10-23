package com.prosincerity.ghostwriter.projects.ui

import com.prosincerity.ghostwriter.projects.domain.parsePositiveBpm
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
