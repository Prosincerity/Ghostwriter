package com.prosincerity.ghostwriter.data

import java.io.DataOutputStream
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class WaveformCacheTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test fun fullResolutionCacheUsesTwoBytesPerPeakAndPreservesMaximumAmplitude() {
        val peaks = IntArray(100_000) { it % 32_769 }
        assertTrue(WaveformCache.save(temporaryFolder.root, peaks.size, peaks))
        assertEquals(200_008L, WaveformCache.file(temporaryFolder.root).length())
        assertArrayEquals(peaks, WaveformCache.load(temporaryFolder.root, peaks.size))
    }

    @Test fun corruptAndLegacyCachesAreRejectedForRegeneration() {
        val file = WaveformCache.file(temporaryFolder.root)
        for (bytes in listOf(
            "2\n0,32768".toByteArray(),
            byteArrayOf(0, 1),
        )) {
            file.writeBytes(bytes)
            assertNull(WaveformCache.load(temporaryFolder.root, 2))
        }
        assertTrue(WaveformCache.save(temporaryFolder.root, 2, intArrayOf(0, 32_768)))
        val valid = file.readBytes()
        for (bytes in listOf(valid.dropLast(1).toByteArray(), valid + byteArrayOf(0))) {
            file.writeBytes(bytes)
            assertNull(WaveformCache.load(temporaryFolder.root, 2))
        }
        DataOutputStream(file.outputStream()).use {
            it.writeInt(0x47574631); it.writeInt(2); it.writeShort(0); it.writeShort(65_535)
        }
        assertNull(WaveformCache.load(temporaryFolder.root, 2))
    }
}
