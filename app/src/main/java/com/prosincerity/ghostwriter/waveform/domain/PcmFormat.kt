package com.prosincerity.ghostwriter.waveform.domain

internal enum class PcmEncoding(val bytesPerSample: Int) {
    PCM_8BIT(1), PCM_16BIT(2), PCM_FLOAT(4),
}

/** Interleaved sample layout shared by PCM conversion and waveform accumulation. */
internal data class PcmFormat(
    val channelCount: Int,
    val sampleRate: Int,
    val encoding: PcmEncoding,
)

