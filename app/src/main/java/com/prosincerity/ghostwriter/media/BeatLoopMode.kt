package com.prosincerity.ghostwriter.media

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.prosincerity.ghostwriter.R

/** Session state shared by the editor, notification and media session. */
enum class BeatLoopMode(
    @get:StringRes val labelRes: Int,
    @get:StringRes val shortLabelRes: Int,
    @get:DrawableRes val iconRes: Int,
) {
    OFF(R.string.beat_loop_off, R.string.beat_loop_off_short, R.drawable.ic_beat_loop_off),
    WHOLE_BEAT(R.string.beat_loop_whole, R.string.beat_loop_whole_short, R.drawable.ic_beat_loop),
    MARKERS(R.string.beat_loop_markers, R.string.beat_loop_markers_short, R.drawable.ic_beat_loop_on);

    fun next(): BeatLoopMode = when (this) {
        OFF -> WHOLE_BEAT
        WHOLE_BEAT -> MARKERS
        MARKERS -> OFF
    }
}
