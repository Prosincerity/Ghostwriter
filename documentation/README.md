# Ghostwriter documentation

| Guide | Contents |
| --- | --- |
| [Build setup](SETUP_NOTES.md) | Requirements, checkout, and local builds |
| [Features and roadmap](FEATURES.md) | Implemented features and planned work |
| [Dictionaries](DICTIONARIES.md) | Downloads, pronunciation lookup, and search rules |
| [Native pronunciation engine](ESPEAK_NATIVE.md) | eSpeak integration and language-data generation |
| [Testing](TESTING.md) | Verification, coverage, and test guidance |
| [Releasing](RELEASING.md) | Release checks and matching source distribution |

## Architecture

Ghostwriter is a Kotlin Android app using Jetpack Compose and Material 3.
It works offline by default, has no AI features or Google Play Services,
and favors Android framework APIs and small implementations. SDK levels,
toolchain versions, and dependencies are defined in the Gradle configuration.

UI typography uses bundled Roboto regular, medium, and bold faces, including
waveform marker labels. The lyric editor keeps its independently selected font.
The studio theme stays dark in either device appearance mode, with neutral
charcoal surfaces, orange primary actions, and purple selection and loop states.
Shared corner shapes and slim sliders keep screens and dialogs consistent.

Application sources are under
[`app/src/main/java/com/prosincerity/ghostwriter/`](../app/src/main/java/com/prosincerity/ghostwriter/).

| Area | Responsibility |
| --- | --- |
| `MainActivity.kt` | Hosts the hand-rolled screen navigator |
| `ui/screens/` | Project, editor, dictionary, settings, and About screens |
| `ui/components/` | Editing surface and reusable controls |
| `data/` | Settings, project files, dictionary installation, and lookup |
| `logic/` | Phonetic matching, input normalization, and waveform calculations |
| `media/` | Beat playback, audio focus, and Android media controls |

### Projects and settings

`Settings.kt` stores preferences in `SharedPreferences`. `ProjectStorage.kt`
coordinates project files in app-specific external storage. Each project
uses a UUID directory and contains lyric snapshots, `project.json` metadata,
and an optional copied beat. The display title lives in metadata; renaming
updates metadata without moving the directory or lyric files. Legacy title
directories migrate automatically, preserving snapshots and their ages.
Regenerable waveform peaks use a versioned binary format with two bytes per
peak in Android's cache directory, excluded from backups. Metadata uses Android's `org.json`.

Global lyric-pad typography is stored with those preferences. `LyricTextSettings`
keeps defaults and bounds; the editor and Settings preview share a Compose text
style mapping. On Android 10 and later, `SystemFonts.getAvailableFonts()` discovers
device font files off the UI thread. The picker reads OpenType typographic family
names (name ID 16, falling back to ID 1), groups weights/styles and UI variants,
and offers a regular upright face per family. Android font language tags and ICU
writing-script samples filter choices to the device's primary language; direct
OpenType character coverage excludes unrelated fallback and symbol fonts.
Only System default is pinned above the alphabetized installed families.
Font selections preserve collection face indices, variation settings, and family
names in SharedPreferences. Older Android versions or discovery failure offer
System default. Existing generic-family and file selections remain readable;
unavailable saved files fall back to monospace. Sizes and letter spacing use scaled pixels, and line height is relative
to the scaled font size.
Justified text uses zero letter spacing in the editor and Settings preview to avoid
Android measuring and drawing justified words at different widths. The saved
letter spacing is retained for other alignments.

`EditorScreen.kt` owns persistence; `LyricsNotepad.kt` owns the editing surface.
The editor draws its toolbar, beat panel, and editable lyrics before binding
the playback service. Only the beat panel waits for the connection; editing
and saving are available immediately. The Activity window uses the same black
background. `EditorLyricsSession` orders
lyric writes within each editor visit; its final exit save rejects queued writes
from that visit so older snapshots cannot overwrite lyrics after navigation.
The home screen reads project summaries off the UI thread and orders projects
by their latest lyric snapshot or metadata edit. Beat and waveform-cache writes
do not count as edits. Rows show the edit time, BPM, and musical key when available;
missing or malformed metadata does not prevent opening a project.

Lyrics are saved manually, periodically, and on editor exit. Loading selects
the newest readable manual or autosave snapshot. Storage mutations are
synchronized, and staged writes sync the complete temporary file before
renaming it over the previous copy. Manual snapshots use `lyrics.txt`.
Legacy title-based snapshots remain readable, including after UUID migration
and later title changes. Failed older backup copies do not prevent
the newest autosave, and count reductions prune backups only after that save
succeeds.
Replacing a beat invalidates its waveform cache and markers.

Future import and export should use the Storage Access Framework to let users
choose a destination. Autosaves remain in app storage.

### Playback and waveforms

`BeatPlaybackService.kt` owns an AOSP `MediaPlayer` through `BeatPlayer.kt`
and exposes a framework `MediaSession`. The editor binds to the service;
started playback continues after it unbinds. A foreground service and wake
lock support playback while the app is in the background or the screen is
locked. Audio focus and headphone disconnection can pause playback.
Dismissing the app from Recents releases playback and loop preparation, removes
media controls, and stops the service. Switching apps or locking the screen
continues playback.

Opening another project, changing its beat, or deleting or renaming the
playing project releases playback. Process death does not restart audio.
Volume, loop mode, and playback position are session state. The loop button
cycles Off, Whole beat, and Markers, using distinct icons and three position
dots, a visible Off / Beat / Marker label, and an accessibility label in all
layouts. The compact Reassign button scales to the available width. Playback
receives the space between compact, balanced side areas, keeping Play centered
in the full row. Loop labels scale to fit beneath the icon on small screens;
playback actions and volume controls remain evenly spaced on one row.
Notification and lock-screen actions and subtitles show the same mode. Whole
beat is the default and uses `MediaPlayer`, ignoring loop boundaries.
Valid loop markers prepare PCM in the background in any mode, retaining the
decoded audio and paused output for the loaded beat. Selecting Markers seeks
to the loop start (or zero when no start is set), preserving whether playback
is playing or paused. Leaving marker
mode pauses PCM output and resumes `MediaPlayer` at the current position;
preparation and cached PCM survive mode changes. Off plays to the physical end
without repeating.

Marker loop roles and exact frame indices (with their sample rate) are persisted
in project metadata. Old millisecond markers migrate when the beat is opened;
milliseconds remain a UI adapter. `MarkerLoop.kt` resolves the start/end range. A missing
start defaults to zero; a missing end defaults to beat duration. The editor
rejects empty or reversed ranges and transfers duplicate roles to the newly
selected marker. With valid loop markers, `BeatPlayer` prepares a temporary,
disk-backed PCM copy using `MediaExtractor`/`MediaCodec` off the UI thread.
Short beats up to 8 MiB of PCM are loaded into a `ShortArray`. Longer beats use
`PcmRingBuffer`: a separate worker fills a bounded circular page buffer,
prefetches loop head/tail pages, and retains loop regions up to 8 MiB in memory.
`SmoothLoopPlayback` feeds one AOSP `AudioTrack`. Its `PcmLoopRenderer` uses
preallocated scratch arrays and atomic boundary snapshots, with no allocation,
monitor locks, file I/O or decoding in render calls. Marker edits apply to upcoming
buffers without pausing/flushing playback. Explicit seeks still flush queued audio.
Seeking past the loop end plays the remaining beat, then wraps to the start
marker and resumes marker looping. A missing start marker wraps to zero.
`PlaybackFrameLedger` maps consumed output frames to the beat, including live edits.
Loop joins use an overlapping 3 ms crossfade, capped for short regions. The
overlapped head frames are skipped on wrap, shortening a repeat by up to 3 ms.
Playback, seeking, focus interruptions, media controls, and wake
locks remain owned by the service. Preparation is cancelled on replacement;
PCM files are deleted after loading into memory or on release. Marker edits
update cached boundaries and prefetch without decoding the beat again. Beats
without loop markers use `MediaPlayer`; an already prepared PCM output can
also play the whole beat in Markers mode after removing the markers. While PCM
is prepared, `MediaPlayer` keeps
playing with working pause, seek, loop, and volume controls. PCM is buffered
silently for a future position on the same beat. The normal player stays audible
until PCM output advances, then a 30 ms volume blend transfers playback without
restarting. Delayed buffering retries from the updated live position. If playback
has already passed the marker end, it finishes the physical tail before entering
the marker loop; explicit seeks and later marker edits still apply immediately.
Paused playback stays paused. Decoder or output preparation failure leaves the
normal player available. The two Android outputs expose separate playback clocks;
the handoff aligns their millisecond positions, not individual hardware samples.

`WaveformExtractor.kt` decodes audio with `MediaExtractor` and `MediaCodec`
off the UI thread. `WaveformCache.kt` validates cached peaks, and
`WaveformViewport.kt` handles zoom and seek calculations. Compose Canvas and
gestures provide the timeline controls. Extraction supports cancellation,
retry, and cache reuse. Playback is available during waveform processing,
cancellation, and failure; retry does not restart a paused beat. Newly imported
beats start playing as soon as they are loaded, including while the long-file
waveform warning is open.
Canceling an import removes its beat and stops playback.

### Dictionaries

Dictionary indexes are separate from project storage and are opened read-only
with Android SQLite APIs. Downloads are user-initiated; installed searches
and bundled native pronunciation generation work offline. See
[Dictionaries](DICTIONARIES.md) for the data contract and matching behavior.

## Known limitations

- Navigation survives rotation through the activity's `configChanges`
  configuration, but resets after process death.
- Initial project reads and the final editor save can run on the main thread.
  Large storage operations can delay other saves.
- Beat replacement and metadata updates are separate writes. A metadata
  failure can leave a new beat paired with old metadata.
- Initial waveform extraction can be slow and has no percentage progress.

Contributor workflow and project constraints are in [AGENTS.md](../AGENTS.md).
