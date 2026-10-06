package com.prosincerity.ghostwriter.data

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File

/** Versioned binary peaks; a missing or obsolete cache is decoded again. */
internal object WaveformCache {
    const val MAX_SAMPLES = 100_000
    private const val MAGIC = 0x47574631 // GWF1
    private const val HEADER_BYTES = 8L
    private const val FILE_NAME = "waveform.dat"

    fun file(cacheDirectory: File): File = File(cacheDirectory, FILE_NAME)

    fun load(cacheDirectory: File, targetSampleCount: Int): IntArray? {
        if (targetSampleCount !in 1..MAX_SAMPLES) return null
        val cacheFile = file(cacheDirectory)
        // Check before allocating: rejects truncated, trailing, legacy text,
        // and oversized payloads without loading them into memory.
        if (!cacheFile.isFile || cacheFile.length() != HEADER_BYTES + targetSampleCount * 2L) return null
        return runCatching {
            DataInputStream(cacheFile.inputStream().buffered()).use { input ->
                if (input.readInt() != MAGIC || input.readInt() != targetSampleCount) return null
                val samples = IntArray(targetSampleCount) { input.readUnsignedShort() }
                if (samples.any { it > 32_768 } || input.read() != -1) null else samples
            }
        }.getOrNull()
    }

    fun save(cacheDirectory: File, targetSampleCount: Int, amplitudes: IntArray): Boolean {
        if (
            targetSampleCount !in 1..MAX_SAMPLES ||
            amplitudes.size != targetSampleCount ||
            amplitudes.any { it !in 0..32_768 }
        ) return false
        return runCatching {
            check(cacheDirectory.isDirectory || cacheDirectory.mkdirs())
            StagedFileWriter.replace(file(cacheDirectory), "waveform-", "Couldn't replace waveform cache") { staged ->
                DataOutputStream(staged.outputStream().buffered()).use { output ->
                    output.writeInt(MAGIC)
                    output.writeInt(targetSampleCount)
                    amplitudes.forEach { output.writeShort(it) }
                }
            }
        }.isSuccess
    }

    fun invalidate(cacheDirectory: File): Boolean {
        val cacheFile = file(cacheDirectory)
        return !cacheFile.exists() || cacheFile.delete()
    }
}
