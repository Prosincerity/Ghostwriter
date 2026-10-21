package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.data.WaveformMarker

internal sealed interface MarkerEdit {
    data class Add(val positionMs: Long, val label: String, val role: MarkerLoopRole) : MarkerEdit
    data class Update(val selected: WaveformMarker, val label: String, val role: MarkerLoopRole) : MarkerEdit
    data class Delete(val selected: WaveformMarker) : MarkerEdit
}

/** Apply dialog events to the current markers; stale or ambiguous selections produce no change. */
internal fun editMarkers(
    markers: List<WaveformMarker>,
    edit: MarkerEdit,
    sampleRate: Int,
): List<WaveformMarker>? = when (edit) {
    is MarkerEdit.Add -> {
        val added = markers + WaveformMarker(edit.label, edit.positionMs, edit.role).withSampleRate(sampleRate)
        replaceLoopMarker(added, added.lastIndex, added.last())
    }
    is MarkerEdit.Update -> {
        val index = indexOfSelectedMarker(markers, edit.selected)
        if (index < 0) null
        else replaceLoopMarker(markers, index, markers[index].copy(label = edit.label, loopRole = edit.role))
    }
    is MarkerEdit.Delete -> {
        val index = indexOfSelectedMarker(markers, edit.selected)
        if (index < 0) null else markers.filterIndexed { currentIndex, _ -> currentIndex != index }
    }
}

/** Preserve the existing drag no-op rule and normalize moved positions to the beat's sample rate. */
internal fun moveMarker(
    markers: List<WaveformMarker>,
    selected: WaveformMarker,
    positionMs: Long,
    sampleRate: Int,
): List<WaveformMarker>? {
    val index = indexOfSelectedMarker(markers, selected)
    if (index < 0 || selected.positionMs == positionMs) return null
    return markers.mapIndexed { currentIndex, marker ->
        if (currentIndex == index) selected.atPositionMs(positionMs, sampleRate) else marker
    }
}
