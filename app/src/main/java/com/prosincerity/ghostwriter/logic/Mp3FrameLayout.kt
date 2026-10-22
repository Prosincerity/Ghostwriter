package com.prosincerity.ghostwriter.logic

/** MPEG Layer III packet boundaries used to retain single-packet PCM timing. */
internal object Mp3FrameLayout {
    private val mpeg1Bitrates = intArrayOf(0, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320)
    private val mpeg2Bitrates = intArrayOf(0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160)
    private val sampleRates = intArrayOf(44_100, 48_000, 32_000)

    fun samplesPerPacket(header: Int, byteCount: Int, sampleRate: Int): Int? {
        val version = header ushr 19 and 3
        val layer = header ushr 17 and 3
        val bitrateIndex = header ushr 12 and 15
        val rateIndex = header ushr 10 and 3
        if (header ushr 21 != 0x7ff || version == 1 || layer != 1 ||
            bitrateIndex == 0 || bitrateIndex == 15 || rateIndex == 3) return null
        val rate = sampleRates[rateIndex] / when (version) { 3 -> 1; 2 -> 2; else -> 4 }
        if (rate != sampleRate) return null
        val bitrate = if (version == 3) mpeg1Bitrates[bitrateIndex] else mpeg2Bitrates[bitrateIndex]
        val padding = header ushr 9 and 1
        val packetSize = (if (version == 3) 144_000 else 72_000) * bitrate / rate + padding
        if (byteCount != packetSize) return null
        return if (version == 3) 1152 else 576
    }
}
