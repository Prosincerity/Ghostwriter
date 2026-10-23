package com.prosincerity.ghostwriter.waveform.domain

import com.prosincerity.ghostwriter.waveform.domain.WaveformMarker

/** One tick per scrub movement, even when it skips several markers. Arrival counts; departure does not. */
internal fun crossesWaveformMarker(
    fromMs: Long,
    toMs: Long,
    markers: List<WaveformMarker>,
): Boolean = markers.any { marker ->
    val position = marker.positionMs
    if (toMs > fromMs) position > fromMs && position <= toMs
    else if (toMs < fromMs) position < fromMs && position >= toMs
    else false
}
