package com.prosincerity.ghostwriter.media

/** Preallocated output-to-beat mapping for queued audio, including live edits and wraps. */
internal class PlaybackFrameLedger(capacity: Int, private val totalFrames: Long) : FramePositionEvents {
    private val output = LongArray(capacity)
    private val beat = LongArray(capacity)
    private var first = 0
    private var count = 0
    private var baseOutput = 0L
    private var baseBeat = 0L

    fun reset(position: Long) { first = 0; count = 0; baseOutput = 0; baseBeat = position }

    override fun record(outputFrame: Long, beatFrame: Long) {
        if (count > 0) {
            val last = (first + count - 1) % output.size
            if (output[last] == outputFrame) { beat[last] = beatFrame; return }
        }
        check(count < output.size) { "Playback position ledger overflow" }
        val index = (first + count) % output.size
        output[index] = outputFrame
        beat[index] = beatFrame
        count++
    }

    fun positionAt(consumed: Long): Long {
        while (count > 0 && output[first] <= consumed) {
            baseOutput = output[first]
            baseBeat = beat[first]
            first = (first + 1) % output.size
            count--
        }
        return (baseBeat + consumed - baseOutput).coerceIn(0, totalFrames)
    }
}
