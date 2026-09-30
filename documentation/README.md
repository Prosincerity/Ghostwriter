# Ghostwriter documentation

- [FEATURES.md](FEATURES.md): implemented features and roadmap.
- [TESTING.md](TESTING.md): test, lint, and coverage commands.
- [SETUP_NOTES.md](SETUP_NOTES.md): build requirements.
- [RELEASING.md](RELEASING.md): release checks and matching source distribution.
- [RHYME_DETECTION_PLAN.md](RHYME_DETECTION_PLAN.md): dictionary and rhyme work in progress.
- [RHYME_BRANCH_REVIEW.md](RHYME_BRANCH_REVIEW.md): staged branch cleanup and validation.

## Architecture

Ghostwriter is a Kotlin, Jetpack Compose, Material 3 Android app. Its namespace
is `com.prosincerity.ghostwriter`. It works offline by default, has no AI
features or Google Play Services dependency, and favors Android framework APIs
over new libraries. Build versions and SDK levels live in the Gradle files.

- `MainActivity.kt` hosts a small sealed-class screen navigator. Keep the
  hand-rolled navigation unless a feature requires a change. Its
  `configChanges` setting preserves this in-memory navigation on rotation;
  process death still resets it.
- `data/Settings.kt` uses `SharedPreferences` for autosave interval and backup
  count. Keep that store for new settings.
- `data/ProjectStorage.kt` coordinates project files under the app-specific
  external-files directory: lyric snapshots, `project.json`, an assigned beat,
  and `waveform.dat`. `ProjectLyricsStorage.kt` handles manual saves and
  rotating autosaves; `StagedFileWriter.kt` replaces files through a temporary
  file; `WaveformCache.kt` validates cached peaks. Storage mutations are
  synchronized. Loading chooses the newest readable manual or autosave
  snapshot. Import and export should use the Storage Access Framework so users
  can choose a visible destination; autosaves stay in app storage.
- Project metadata uses Android's `org.json`. Assigned beats are copied into
  their project. Changing a beat invalidates its waveform cache and markers.
  The global instrumentals library is planned, not implemented.
- `EditorScreen.kt` owns lyric persistence; `LyricsNotepad.kt`
  owns the editing surface. Autosave runs periodically and on editor exit.
  `BeatPlaybackService.kt` owns `BeatPlayer.kt` (AOSP `MediaPlayer`) and a
  framework `MediaSession`. The editor binds to the service; navigating away
  unbinds without interrupting started playback. A media-playback foreground
  service and playback wake lock keep beats running in other apps and with the
  screen locked. Android media controls show the app icon as artwork and a
  `[Ghostwriter]` project label, with play, pause, seek, restart, and loop.
  Pausing keeps the system player available for resuming. The loop setting is
  shared with the editor. Opening another project,
  replacing/removing its beat, or deleting/
  renaming the playing project releases playback. Audio focus and headphone
  disconnection are respected. Process death does not automatically restart audio.
  `WaveformExtractor.kt` uses
  `MediaExtractor` and `MediaCodec` off the UI thread;
  `WaveformViewport.kt` handles zoom and seek math. Compose Canvas and gestures
  render and control the waveform. Extraction supports cancellation and cache
  reuse.
- Settings links to a dedicated rhyme dictionary download screen. Each language
  offers independent Wiktionary Kaikki and eSpeak NG generated downloads, with
  installed sources marked on device. The pinned release manifest identifies
  read-only SQLite pronunciation indexes stored in app-private internal storage.
  Normal editing and installed-dictionary lookup remain offline. An eSpeak NG
  1.52.0 JNI fallback generates IPA locally for database misses, with only
  English, German, and Turkish language data packaged. Its native build and
  data-generation steps are in [ESPEAK_NATIVE.md](ESPEAK_NATIVE.md). The editor
  opens a Dictionary Screen with bounded rhyme, assonance, word-prefix, and
  word-suffix searches over installed indexes. Its IPA tokenizer follows the
  producer's phoneme rules. Syllable coloring remains planned; see the
  [rhyme plan](RHYME_DETECTION_PLAN.md).

## Current limits

- Navigation survives rotation but not process death.
- Initial project reads and the final editor save can run on the main thread;
  large storage operations can delay other saves.
- Beat and metadata replacement are separate writes, so a metadata failure can
  leave the new beat with old metadata.
- Player volume, loop setting, and position last only for the current session.
- First-time waveform extraction can be slow; it has cancel, retry, and a
  duration warning, but no percentage progress.

See [FEATURES.md](FEATURES.md) for planned work. Agent workflow rules live in
the repository's [AGENTS.md](../AGENTS.md).
