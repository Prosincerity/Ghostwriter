# Architecture and documentation

This guide maps the source tree, storage contracts, and playback lifecycle.

## Guides

| Guide | Purpose |
| --- | --- |
| [Build setup](SETUP_NOTES.md) | Toolchain requirements and debug builds |
| [Features](FEATURES.md) | Implemented features and planned work |
| [Dictionaries](DICTIONARIES.md) | Installation, lookup, and search rules |
| [Native pronunciation engine](ESPEAK_NATIVE.md) | JNI integration and data generation |
| [Testing](TESTING.md) | Verification scope, commands, and coverage |
| [Releasing](RELEASING.md) | Binary checks and matching source distribution |
| [Contributor instructions](../AGENTS.md) | Constraints and development workflow |

## Source layout

The app uses Kotlin, Jetpack Compose, and Material 3 in one `app` module.
Paths below are relative to
[`com/prosincerity/ghostwriter/`](../app/src/main/java/com/prosincerity/ghostwriter/).

| Path | Responsibility |
| --- | --- |
| `MainActivity.kt` | Hand-rolled screen navigation and shared-folder connection |
| `projects/` | Project files, shared-folder recovery, Home, and project information |
| `editor/` | Lyric editing, editor sessions, beat import, and editor dialogs |
| `dictionary/` | Dictionary installation, pronunciation matching, downloads, and search UI |
| `waveform/` | Waveform decoding, cache, markers, loop geometry, and timeline UI |
| `player/` | Beat playback, PCM audio, audio focus, media controls, and player UI |
| `settings/` | SharedPreferences and the settings screen |
| `typography/` | Lyric text settings, device fonts, and Compose text styles |
| `about/` | About screen and attribution |
| `core/` | Shared file replacement, UI controls, and theme |

Packages follow feature ownership. Larger features use internal `data`, `domain`,
`ui`, `audio`, or `playback` subpackages. JVM and Android tests mirror their
subject's production package. Keep this layout for future changes.

Feature dependencies must remain acyclic. `waveform` owns `WaveformMarker` and
`MarkerLoopRole` independently of projects and playback. It may depend on `core`,
but must not import `player`, `projects`, or `editor`. `player` may depend on
`waveform` and `core`, but must not import `projects` or `editor`. The root
`MainActivity` composes features and remains the playback notification destination.

`Settings.kt` stores preferences in `SharedPreferences`. Metadata uses `org.json`.

## Project storage

`ProjectStorage.kt` manages UUID project directories under the app-specific
external-files directory. Renaming changes the display title in `project.json`.
Legacy title directories migrate while retaining their files and snapshot ages.

| File or location | Contents |
| --- | --- |
| `lyrics.txt` | Manual lyric snapshot |
| `autosave1.txt` through `autosave10.txt` | Autosave slots; older slots remain readable |
| `project.json` | Title, musical details, beat assignment, markers, and timestamps |
| `beat.<extension>` | Copied project audio |
| Android cache directory, `waveforms/<project UUID>/waveform.dat` | Regenerable binary peaks, excluded from backups |

`ProjectLyricsStorage.kt` selects the newest readable manual or autosave snapshot.
It also reads legacy title-based lyric files. An empty saved lyric file counts
as an intentional draft. If all existing snapshots fail to read, the editor
reports the load failure and does not save over those files.

`EditorScreen.kt` owns persistence; `LyricsNotepad.kt` owns editing.
`EditorLyricsSession` serializes writes within an editor visit and rejects
queued saves after the final exit save. `StagedFileWriter` syncs a complete
temporary file before renaming it over the destination.
Stopping the activity requests a save of the latest lyrics without closing the
editor session, so switching apps does not have to wait for the autosave timer.

### Shared lyric folder

Home requires a connected folder before enabling project actions.
`PersistentLyricsStorage` uses Android's Storage Access Framework. Selecting
an existing `Ghostwriter` folder uses it directly; another folder receives
a `Ghostwriter` subfolder.

`DocumentProjectArchive` writes immutable, checksummed JSON snapshots of lyrics
and metadata. It syncs and reads back a new save before pruning older saves.
The shared folder survives uninstall; reconnect it after reinstall to restore
project UUIDs and titles. Beats remain app-specific and require reassignment.

Reconnection preserves local drafts that differ from their last verified shared
save. Missing local lyrics recover from readable shared snapshots, retaining
local metadata edits. `ProjectRestoreJournal` records original and intended
file states so interrupted recovery can resume without overwriting later edits.

A failed shared save retains the local working copy and reports the failure.
Reconnect successfully before uninstalling to preserve that draft outside app
storage. Folder permission must be granted again after reinstall.

## Playback and waveforms

`BeatPlaybackService` owns `BeatPlayer` and a framework `MediaSession`.
The editor draws before binding to the service. Unbinding leaves started
playback running; a foreground service and wake lock support background audio.
Audio focus loss and headphone disconnection can pause playback.

Opening another project, changing its beat, or renaming or deleting the playing
project releases playback. Dismissing the app from Recents stops the service
and removes media controls. Process death does not restart audio.
Volume, loop mode, and playback position are session state.

| Mode | Behavior |
| --- | --- |
| Off | Play to the physical end without repeating |
| Whole beat | Default; loop with `MediaPlayer`, ignoring markers |
| Markers | Seek to the loop start while preserving play/pause state |

Markers store frame indices, sample rate, and loop roles in `project.json`.
`MarkerLoop.kt` resolves missing boundaries to zero or beat duration.
The editor rejects empty or reversed ranges and transfers duplicate roles.

Valid markers prepare PCM off the UI thread through `MediaExtractor` and
`MediaCodec`. `PcmSource.kt` selects memory storage up to 8 MiB or a disk-backed
`PcmRingBuffer` with worker prefetch. Preparation survives mode changes and
marker edits; replacing the beat cancels it.

`SmoothLoopPlayback` feeds `AudioTrack` through `PcmLoopRenderer`.
The renderer uses preallocated buffers without file I/O or decoding.
An overlapping crossfade reduces clicks and shortens each repeat by up to
3 ms. Marker edits affect upcoming buffers; explicit seeks flush queued audio.

During preparation, `MediaPlayer` remains audible and controllable.
PCM buffers silently before a short volume blend transfers playback at the
live position. Paused audio stays paused. Preparation failure retains normal
playback. The outputs have separate clocks; handoff is not sample-exact.

`WaveformExtractor.kt` decodes peaks off the UI thread. `WaveformCache.kt`
validates cached peaks; `WaveformViewport.kt` handles zoom and seek calculations.
On API 35 and later, the AOSP C2 MP3 decoder can batch packets and PCM transfers.
Extraction checks the decoder's packet clock before batching and preserves
individual packet rounding when placing peaks. Other decoders and unsupported
MP3 layouts use single-packet decoding; a failed batch retries with fresh media
objects. Codec callbacks enqueue events while the calling worker processes
PCM and checks cancellation.
The panel shows transport controls only after the player and waveform are ready.
New imports can start audio while the long-file warning is open.

## Typography

`SystemFontCatalog.kt` discovers device fonts on Android 10 and later, filters
for the device language and glyph coverage, and groups font families.
Older devices or discovery failure offer System default. Unavailable saved
font files fall back to monospace.

Justified text uses zero letter spacing;
the saved spacing remains available for other alignments.

## Known limitations

- Navigation resets after process death; rotation uses `configChanges`.
- Initial lyric reads and the final editor save can block the main thread.
- Beat replacement and metadata updates are separate writes; a metadata failure
  can leave copied audio paired with old metadata.
- Waveform extraction has no percentage progress.
