package com.prosincerity.ghostwriter.data

import java.io.DataOutputStream
import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class WaveformCacheTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test fun invalidSampleCountsAndAmplitudesLeaveTheGoodCacheIntact() {
        val peaks = intArrayOf(0, 32_768)
        assertTrue(WaveformCache.save(temporaryFolder.root, 2, peaks))
        for (count in listOf(-1, 0, WaveformCache.MAX_SAMPLES + 1)) {
            assertNull(WaveformCache.load(temporaryFolder.root, count))
            assertFalse(WaveformCache.save(temporaryFolder.root, count, peaks))
        }
        for (invalid in listOf(intArrayOf(1), intArrayOf(-1, 0), intArrayOf(0, 32_769))) {
            assertFalse(WaveformCache.save(temporaryFolder.root, 2, invalid))
        }
        assertArrayEquals(peaks, WaveformCache.load(temporaryFolder.root, 2))
    }

    @Test fun mismatchedBinaryHeadersAreRejectedEvenWithTheRightFileLength() {
        for ((magic, count) in listOf(0 to 2, 0x47574631 to 1)) {
            DataOutputStream(WaveformCache.file(temporaryFolder.root).outputStream()).use {
                it.writeInt(magic); it.writeInt(count); it.writeShort(0); it.writeShort(1)
            }
            assertNull(WaveformCache.load(temporaryFolder.root, 2))
        }
    }

    @Test fun cacheDirectoryCreationAndFilesystemFailuresAreReported() {
        val directory = File(temporaryFolder.root, "new/cache")
        assertNull(WaveformCache.load(directory, 1))
        assertTrue(WaveformCache.save(directory, 1, intArrayOf(1)))
        assertTrue(WaveformCache.invalidate(directory))
        assertTrue(WaveformCache.invalidate(directory))
        val blocker = temporaryFolder.newFile("blocker")
        assertFalse(WaveformCache.save(blocker, 1, intArrayOf(1)))
        val cache = WaveformCache.file(directory)
        assertTrue(cache.mkdir())
        File(cache, "keep").writeText("keep")
        assertFalse(WaveformCache.invalidate(directory))
        assertFalse(WaveformCache.save(directory, 1, intArrayOf(1)))
        assertEquals("keep", File(cache, "keep").readText())
    }

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
