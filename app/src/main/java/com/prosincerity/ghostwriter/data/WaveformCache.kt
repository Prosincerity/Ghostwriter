package com.prosincerity.ghostwriter.data

import java.io.File

/** File format and validation for the project-local waveform cache. */
internal object WaveformCache {
    const val MAX_SAMPLES = 100_000
    private const val FILE_NAME = "waveform.dat"

    fun file(projectDir: File): File = File(projectDir, FILE_NAME)

    fun load(projectDir: File, targetSampleCount: Int): IntArray? {
        if (targetSampleCount !in 1..MAX_SAMPLES) return null
        val cacheFile = file(projectDir)
        if (!cacheFile.isFile) return null

        return runCatching {
            val encoded = cacheFile.readText()
            val headerEnd = encoded.indexOf('\n')
            if (headerEnd < 0) return null

            val storedTargetCount = encoded.substring(0, headerEnd).toInt()
            if (storedTargetCount != targetSampleCount) return null

            val payload = encoded.substring(headerEnd + 1).trim()
            if (payload.isEmpty()) return null

            val samples = payload.split(',').map { sample ->
                sample.toInt().takeIf { it in 0..32_768 }
                    ?: throw IllegalArgumentException("Invalid cached waveform sample")
            }
            if (samples.size != targetSampleCount) return null
            samples.toIntArray()
        }.getOrNull()
    }

    fun save(projectDir: File, targetSampleCount: Int, amplitudes: IntArray): Boolean {
        if (
            targetSampleCount !in 1..MAX_SAMPLES ||
            amplitudes.size != targetSampleCount ||
            amplitudes.any { it !in 0..32_768 }
        ) return false

        return runCatching {
            val encoded = buildString {
                append(targetSampleCount)
                append('\n')
                amplitudes.joinTo(this, separator = ",")
            }
            StagedFileWriter.writeText(file(projectDir), encoded)
        }.isSuccess
    }

    fun invalidate(projectDir: File): Boolean {
        val cacheFile = file(projectDir)
        return !cacheFile.exists() || cacheFile.delete()
    }
}
