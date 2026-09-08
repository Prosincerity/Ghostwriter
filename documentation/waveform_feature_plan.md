# Task: DAW-style waveform view for BeatPlayer (replaces slider)

Read `AGENTS.md`, `documentation/AGENT_CONTEXT.md`, and `documentation/FEATURES.md`
before writing any code. This document is the spec; those files are the
constraints. If anything here conflicts with them, the project documentation
wins — stop and ask.

## 0. Branch setup (do this first)

```
git checkout dev
git pull
git checkout -b waveform-feature
```

All work for this feature happens on `waveform-feature`. Do not merge to
`dev` or `main` yourself — the maintainer merges when it's stable, per
existing workflow.

## 1. What this feature is

Replace the current seek slider in `EditorScreen.kt`'s beat player controls
with a horizontally scrollable, pinch-zoomable waveform view — similar to the
timeline/playlist view in a DAW (e.g. FL Studio). It must do everything the
slider currently does (tap/drag to seek, reflect playback position) plus:

- Render the beat's actual amplitude waveform, not a plain bar.
- Pinch-to-zoom horizontally (zoom in for fine placement, zoom out to see
  the whole beat).
- Scroll/pan horizontally when zoomed in.
- Tap or drag to seek, same as the slider it replaces.
- Optionally place named markers on the waveform (e.g. "Hook", "Verse 1",
  "Bridge") that persist with the project and can be tapped to seek there,
  renamed, or deleted.

This is Phase 2 work, requested directly by users — treat it as high
priority, but do not skip the "ask before adding a new task" step for
anything not covered by this spec (see AGENTS.md).

## 2. Non-negotiables carried over from project rules

- **No new external dependencies.** This is built entirely with Compose
  Foundation (`Canvas`, `detectTransformGestures`, `detectTapGestures`,
  `detectDragGestures` — all already part of the Compose BOM already in
  `build.gradle.kts`) and AOSP's `android.media.MediaExtractor` +
  `android.media.MediaCodec` for decoding. Do not add
  ExoPlayer/Media3, Amplituda, compose-audiowaveform, or any other
  waveform/audio library. This was evaluated and rejected — see rationale
  below if you want to double check the reasoning before starting.
  - Rationale (for context, not for re-litigating): the available
    open-source waveform libraries for Compose either lack zoom/pan/marker
    support outright, are distributed only via JitPack, pull in ExoPlayer,
    or (Amplituda specifically) ship prebuilt FFmpeg `.so` binaries under
    LGPL with known unresolved issues (16 KB page size support, native
    crash reports). None of that is worth it when `MediaExtractor` +
    `MediaCodec` gives us the same raw PCM data for free, already inside
    AOSP, with a license we don't have to think about.
- **`org.json` only** for any persisted marker data — no kotlinx.serialization,
  no Gson, no Moshi.
- **`android.media.MediaPlayer`** (already wrapped by `BeatPlayer.kt`) remains
  the playback engine. The waveform view is a visualization + input surface
  that reads `BeatPlayer.currentPositionMs` / `durationMs` and calls
  `BeatPlayer.seekTo(...)` — it does not touch playback internals directly.
- **Testable logic first, UI second.** Amplitude extraction and any
  zoom/scroll math that can be expressed as pure functions must live in
  a `logic/` package with JVM unit tests, completely separate from the
  Composable that draws pixels. Do not write the Canvas composable before
  the extraction and viewport-math modules have passing tests.
- **Monospace/dark terminal aesthetic** — reuse `ui/theme/Color.kt` and
  `Type.kt` values; do not introduce new ad hoc colors.

## 3. Proposed module breakdown

```
app/src/main/java/com/prosincerity/ghostwriter/
├── logic/
│   ├── WaveformExtractor.kt      ← NEW: decode audio → amplitude samples
│   └── WaveformViewport.kt       ← NEW: pure zoom/pan/seek math (testable, no Android UI deps)
├── data/
│   ├── ProjectMetadata.kt        ← MODIFY: add markers field
│   └── ProjectStorage.kt         ← MODIFY: cache extracted waveform data per project
├── media/
│   └── BeatPlayer.kt             ← DO NOT MODIFY (public API is sufficient: currentPositionMs, durationMs, seekTo)
├── ui/screens/
│   └── EditorScreen.kt           ← MODIFY: replace Slider with WaveformView
└── ui/components/
    └── WaveformView.kt           ← NEW: Canvas composable, gestures, marker overlay
```

### 3.1 `logic/WaveformExtractor.kt` (new, pure logic module)

- Input: a beat file (`File`) from the project directory.
- Uses `MediaExtractor` to pull the audio track and `MediaCodec` to decode
  it to PCM, downsampling into a fixed-size `IntArray` of amplitude peaks
  (e.g. one value per ~20ms window, or a configurable sample count —
  pick a default and document it).
- Pure function signature roughly:
  `fun extractAmplitudes(file: File, targetSampleCount: Int): IntArray`
  (exact signature is up to you, but it must not take any Compose/UI types
  as input or output — this needs to run in a JVM unit test with a bundled
  test audio fixture, not an emulator).
- Runs on `Dispatchers.IO` when called from UI code (decoding a full song
  is not free) — but the function itself stays synchronous/pure so it's
  trivially testable.
- Unit tests: cover a short bundled test fixture (silence, a tone, or
  similar), verify output length matches `targetSampleCount`, verify it
  doesn't crash on edge cases (very short file, corrupt file → return
  empty array rather than throwing where reasonable).

### 3.2 Waveform data caching (`ProjectStorage.kt`)

- Extracting amplitudes from a full song is too slow to do on every editor
  open. Cache the extracted `IntArray` alongside the beat file, e.g.
  `waveform.dat` in the project directory (simple binary or comma-separated
  ints via plain file I/O — no new serialization dependency).
- Invalidate/regenerate the cache when the beat file changes (same event
  that currently triggers `beatFile`/`beatOriginalName` updates in
  `project.json` — hook in there).
- This follows the same "self-contained project directory" principle
  already documented for beat storage.

### 3.3 `logic/WaveformViewport.kt` (new, pure logic module)

Pure math for translating between:
- Playback position (ms) ↔ x-pixel position on screen, given current zoom
  level and horizontal scroll offset.
- Pinch gesture deltas ↔ new zoom level (with min/max zoom clamping — fully
  zoomed out should show the entire beat; pick and document a sensible max
  zoom, e.g. a few seconds across the visible width).
- Drag/pan deltas ↔ new scroll offset (clamped so you can't scroll past
  the start or end of the beat).
- Tap/drag-to-seek x-position ↔ target playback ms.

This is the part most worth unit testing thoroughly, since gesture-to-state
math is easy to get subtly wrong (off-by-one at zoom boundaries, scroll
clamping at the very start/end, etc.) and has nothing to do with Android UI.

### 3.4 `ui/components/WaveformView.kt` (new Composable)

- `Canvas` draws the amplitude bars for the currently visible window
  (using `WaveformViewport` to know which slice of the amplitude array is
  on-screen), a playhead line at the current position, and marker flags/
  labels above their respective positions.
- Gestures:
  - `detectTransformGestures` for pinch-zoom + pan in one recognizer
    (standard Compose approach — do not hand-roll multi-touch math).
  - `detectTapGestures(onTap = ...)` to seek on single tap.
  - Drag on the playhead itself (or anywhere on the waveform, whichever
    feels better — use your judgment, but keep it consistent with how the
    old slider behaved) to scrub, calling `BeatPlayer.seekTo` continuously
    or on drag-end (match the old slider's existing behavior/feel — check
    how `EditorScreen.kt` currently wires the `Slider`'s `onValueChange`
    vs `onValueChangeFinished` and preserve that distinction here).
  - Long-press (or a dedicated "add marker" button — your call, propose
    one and note it in the PR description) to add a marker at that
    position, prompting for a label via a small dialog (reuse
    `ProjectInfoDialog.kt`'s dialog patterns/styling for consistency).
- Visual style: monospace labels, dark terminal palette from `Color.kt`,
  greyed-out marker flags so they don't compete visually with the waveform
  itself.

### 3.5 Marker persistence (`ProjectMetadata.kt`)

Add a `markers` field to the existing metadata schema, e.g.:

```json
{
  "version": 2,
  "...": "...(existing fields unchanged)",
  "markers": [
    { "label": "Hook", "positionMs": 15200 },
    { "label": "Verse 1", "positionMs": 32000 }
  ]
}
```

- Bump `version` to 2 and make sure `fromJsonObject` defaults `markers` to
  an empty list when reading an older `project.json` that doesn't have the
  field — do not break existing projects.
- Add a `WaveformMarker` data class (`label: String`, `positionMs: Long`)
  parsed/serialized with `org.json`, following the exact same
  optional-field pattern already used for `bpm`/`key`/etc. in this file.
- Markers save through the same `ProjectStorage` metadata-save path already
  used for BPM/key/notes — no new save mechanism.

### 3.6 `EditorScreen.kt` changes

- Remove the existing `Slider` used for beat seeking.
- Replace it with `WaveformView`, wired to:
  - `beatPlayer.currentPositionMs` / `durationMs` for position/playhead.
  - `beatPlayer.seekTo(...)` for seeking.
  - The cached amplitude data for the current project's beat (load once
    per beat, not on every recomposition).
  - The project's `markers` list for overlay + add/rename/delete.
- Keep all other existing controls (play/pause, loop toggle, volume) as-is
  — this task only touches the seek mechanism and adds markers.

## 4. Testing expectations

- JVM unit tests for `WaveformExtractor` (amplitude extraction correctness,
  edge cases) and `WaveformViewport` (zoom/pan/seek math) — these are the
  two pure modules and must have real coverage before UI work is
  considered done.
- Manual/emulator verification for gestures (pinch, pan, tap-seek,
  marker add/rename/delete) — note in the PR description what was checked
  on-device, since Compose gesture behavior can't be fully covered by JVM
  tests alone.
- Run `./gradlew test lint` before considering the task complete, same as
  existing workflow.

## 5. Documentation updates (required, not optional)

Before this is considered done:

- **`documentation/FEATURES.md`** — update the roadmap: move "waveform
  seek view" from not-listed to explicitly tracked, mark sub-parts (zoom/
  scroll, markers) with their status. If markers ship as part of this task,
  mark them done; if deferred, add them as a follow-up bullet.
- **`documentation/AGENT_CONTEXT.md`** —
  - Add `WaveformExtractor.kt`, `WaveformViewport.kt`, and
    `WaveformView.kt` to the repository structure tree (§4) and the
    file-by-file walkthrough (§5).
  - Add a new "Deliberate architectural decisions" entry (§6) recording:
    Canvas + Compose Foundation gestures instead of a third-party waveform
    library, and why (short version of §2's rationale above).
  - Update `project.json` schema documentation to include `markers` and
    the version bump to 2.
  - Add `waveform.dat` (or whatever the cache file ends up named) to the
    per-project directory listing.
  - Update the "known technical debt" section (§7) with anything you
    deliberately deferred (e.g. no waveform re-extraction progress
    indicator, no undo for marker deletion, etc. — whatever is actually
    true once you're done).
- **`AGENTS.md`** at repo root only needs updating if a new non-negotiable
  constraint was introduced — it shouldn't be, since this task adds zero
  new dependencies.

Do not leave documentation updates for a follow-up commit — they land in
the same PR/branch as the code.

## 6. Suggested order of work

1. `git checkout -b waveform-feature` from `dev`.
2. `WaveformExtractor.kt` + unit tests.
3. `WaveformViewport.kt` + unit tests.
4. Wire extraction + caching into `ProjectStorage.kt`.
5. Add `markers` to `ProjectMetadata.kt` (with backward-compat parsing).
6. Build `WaveformView.kt` against the now-tested logic modules.
7. Wire into `EditorScreen.kt`, remove the old `Slider`.
8. `./gradlew test lint`, manual emulator pass on gestures.
9. Update `FEATURES.md` and `AGENT_CONTEXT.md`.
10. Report back with a summary of what was built, what was deferred, and
    what needs the maintainer's manual verification on-device.

If at any point a step requires a new dependency, a change to `BeatPlayer.kt`'s
public API, or a deliberate architectural decision not listed here — stop
and ask before proceeding, per `AGENTS.md`.
