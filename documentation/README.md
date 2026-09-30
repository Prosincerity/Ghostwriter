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
contains lyric snapshots, `project.json` metadata, an optional copied beat,
and its waveform cache. Metadata uses Android's `org.json`.

Global lyric-pad typography is stored with those preferences. `LyricTextSettings`
keeps defaults and bounds; the editor and Settings preview share a Compose text
style mapping. On Android 10 and later, `SystemFonts.getAvailableFonts()` discovers
device font files off the UI thread. Font selections preserve collection face
indices and variation settings in SharedPreferences. Generic platform families
remain available on older Android versions; unavailable saved files fall back to
monospace. Sizes and letter spacing use scaled pixels, and line height is relative
to the scaled font size.

`EditorScreen.kt` owns persistence; `LyricsNotepad.kt` owns the editing surface.
Lyrics are saved manually, periodically, and on editor exit. Loading selects
the newest readable manual or autosave snapshot. Storage mutations are
synchronized, and staged writes replace files through a temporary file.
Replacing a beat invalidates its waveform cache and markers.

Future import and export should use the Storage Access Framework to let users
choose a destination. Autosaves remain in app storage.

### Playback and waveforms

`BeatPlaybackService.kt` owns an AOSP `MediaPlayer` through `BeatPlayer.kt`
and exposes a framework `MediaSession`. The editor binds to the service;
started playback continues after it unbinds. A foreground service and wake
lock support playback while the app is in the background or the screen is
locked. Audio focus and headphone disconnection can pause playback.

Opening another project, changing its beat, or deleting or renaming the
playing project releases playback. Process death does not restart audio.
Volume, loop, and playback position are session state.

`WaveformExtractor.kt` decodes audio with `MediaExtractor` and `MediaCodec`
off the UI thread. `WaveformCache.kt` validates cached peaks, and
`WaveformViewport.kt` handles zoom and seek calculations. Compose Canvas and
gestures provide the timeline controls. Extraction supports cancellation,
retry, and cache reuse.

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
