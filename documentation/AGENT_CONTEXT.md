# Project handoff: Ghostwriter

## 1. What this project is

**Ghostwriter** is a free, open-source Android app for writing rap lyrics,
built solo. Its tagline is **Songwriting Environment**. The repository is
MIT licensed and public at `github.com/Prosincerity/Ghostwriter`. Its core
songwriting editor, project persistence, offline beat player, and interactive
waveform timeline are implemented; it continues to grow feature by feature.

## 2. Non-negotiable philosophy — read this before suggesting anything

These rules came directly from the project owner and override any instinct
you have to "modernize" or "improve" things in ways that conflict with them:

- **No AI functionality, anywhere, ever.** No AI-assisted writing, no smart
  suggestions, no LLM calls, nothing. This is explicitly an anti-AI-bloat
  writing tool. Do not propose AI features even as an aside.
- **No added bloat.** Every dependency and every feature must earn its place
  for someone writing rap lyrics. Prefer the Android framework's built-in
  tools (e.g. `SharedPreferences`) over pulling in a library (e.g. DataStore)
  when the built-in tool is sufic.
- **AOSP-only, no Google Play Services.** The owner tests on an AOSP-based
  Android Virtual Device with no Google services layer. Never introduce a
  dependency on Google Play Services, Firebase, Google Maps, Google Sign-In,
  or anything else that requires GMS to function. (Using Google's *Maven
  repository* — `google()` in Gradle — to download AndroidX/Jetpack library
  artifacts is fine and unrelated; that's just an artifact host, not a
  runtime dependency on Play Services.)
- **Kotlin + Jetpack Compose + Material 3.** This is the modern, declarative
  Android UI toolkit and is the only UI approach used in this project — no
  XML layouts, no legacy Views system.
- **Solo dev, Git branching.** `main` = stable/working. `dev` = active
  feature work. The owner works inside Android Studio and uses assisted coding
  with Google Antigravity and OpenAI Codex.

## 3. Tech stack (as of last verified state)

- Kotlin (2.2.10 at last check)
- Jetpack Compose + Material 3 (Compose BOM 2026.02.01 at last check)
- Android Gradle Plugin 9.4.0, Gradle 9.6 (wizard-generated, don't hand-edit
  version numbers without reason — they drift fast and the wizard/Android
  Studio keeps them in sync correctly)
- `compileSdk`/`targetSdk` 37, `minSdk` 24
- Audio playback: `android.media.MediaPlayer` (built-in AOSP core framework,
  offline-only, zero external libraries)
- Project metadata: `org.json` (built-in Android SDK `JSONObject`/`JSONArray`,
  zero external serialization libraries)
- No navigation library (hand-rolled sealed-class navigation, see below)
- No DI framework (project is small enough not to need one yet)
- No local database yet (plain text files & JSON on disk — see storage section)

## 4. Repository structure (current)

```
Ghostwriter/
├── README.md
├── LICENSE                  ← MIT
├── .gitignore
├── documentation/           ← all project documentation lives here
│   ├── README.md            ← documentation index & guide
│   ├── FEATURES.md          ← the full phased feature roadmap, keep updated
│   ├── SETUP_NOTES.md       ← project creation and Android Studio setup
│   └── AGENT_CONTEXT.md     ← agent handoff, architecture guide & decisions
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   └── java/com/prosincerity/ghostwriter/
│       │       ├── MainActivity.kt         ← navigation host (see §6)
│       │       ├── data/
│       │       │   ├── Settings.kt         ← SharedPreferences wrapper
│       │       │   ├── ProjectMetadata.kt  ← metadata schema and waveform markers
│       │       │   └── ProjectStorage.kt   ← file I/O, autosaves, metadata, beat & waveform cache
│       │       ├── logic/
│       │       │   ├── WaveformExtractor.kt ← AOSP audio decoding and peak extraction
│       │       │   └── WaveformViewport.kt  ← pure zoom, pan, and seek math
│       │       ├── media/
│       │       │   └── BeatPlayer.kt        ← AOSP MediaPlayer wrapper
│       │       └── ui/
│       │           ├── components/
│       │           │   ├── BeatDialogs.kt
│       │           │   ├── BeatPlayerPanel.kt
│       │           │   ├── LyricsNotepad.kt  ← state-hoisted lyrics editing surface
│       │           │   ├── PlaybackTime.kt
│       │           │   └── WaveformView.kt  ← Canvas timeline and gestures
│       │           ├── screens/
│       │           │   ├── AboutScreen.kt
│       │           │   ├── HomeScreen.kt
│       │           │   ├── EditorScreen.kt
│       │           │   ├── ProjectInfoDialog.kt
│       │           │   └── SettingsScreen.kt
│       │           └── theme/
│       │               ├── Color.kt        ← dark "terminal" palette
│       │               ├── Theme.kt
│       │               └── Type.kt         ← monospace typography
│       ├── test/java/com/prosincerity/ghostwriter/  ← JVM logic, storage, metadata & player tests
│       └── androidTest/java/com/prosincerity/ghostwriter/ ← Compose/device tests
└── gradle/libs.versions.toml
```

The Android namespace and application ID are both
`com.prosincerity.ghostwriter`. The visible app name is **Ghostwriter** and
the tagline is **Songwriting Environment**. If a `com.ghostwriter.exe`
reference remains, it is a stale identifier that must be migrated.

## 5. What's actually built right now

**Phase 1 (barebones notepad)** — done.
**Phase 2 features landed so far:**
- Autosave + rolling backups + settings — done.
- Project metadata (`project.json`) + in-editor Project Info dialog — done.
- Offline beat player + project-local beat import — done.
- Interactive waveform, zoom/pan, markers, caching, and processing safety — done.
- About screen with open-source and dictionary-data attribution — done.

Walking through what each file does:

- **`MainActivity.kt`** — Hosts a tiny hand-rolled navigation system: a
  private `sealed class Screen` with `Home`, `Editor(projectTitle)`, `Settings`,
  and `About` variants, switched on with a `when` inside a
  `GhostwriterApp()` composable. This is deliberately NOT using the
  Navigation-Compose library — the current screen count doesn't justify the dependency.
  If the screen count grows significantly, that's the natural point to
  reconsider.

- **`data/Settings.kt`** — Wraps `SharedPreferences` (not DataStore, by
  design — see philosophy). Stores two settings: autosave interval in
  seconds (options: 10/30/60/120/300) and autosave backup count (options:
  3/4/5). Simple get/set functions, no observers/flows — screens re-read on
  their own recomposition.

- **`data/ProjectStorage.kt`** — Owns the on-disk layout. Each "project"
  (song) lives at:
  ```
  <context.getExternalFilesDir(null)>/ghostwriter/<sanitized title>/
      autosave1.txt        ← always the MOST RECENT snapshot
      autosave2.txt
      ...
      autosave{N}.txt      ← oldest kept
      <title>.txt          ← manual save snapshot
      project.json         ← project metadata (BPM, key, beat file, timestamps)
      beat.<ext>           ← assigned instrumental audio file for this project
      waveform.dat         ← validated 1,000-peak cache for the assigned beat
  ```
  This is the app's own external-files directory — no runtime storage
  permission needed, private to the app, wiped on uninstall, works
  identically on AOSP (it's a core Android API, not Play-Services-gated).
  `rotateAndSave()` shifts every file up one index (dropping anything past
  the configured keep-count) then writes the current text into
  `autosave1.txt`; unchanged content does not rotate the ring, but reducing the
  backup count still prunes old snapshots. `loadLatest()` prefers an up-to-date
  manual save, otherwise reads the newest readable autosave, with the manual
  save as a last recovery fallback. Text and metadata writes stage a temporary
  file before replacement. Storage mutations are synchronized to prevent
  overlapping operations from interleaving; manual and metadata saves return
  success flags used by the editor's feedback.

  **Beat & Instrumental Storage Architecture:**
  - **Current project beat import:** When a project has no assigned beat, the
    editor opens Android's Storage Access Framework picker for supported audio
    files (`.mp3`, `.wav`, `.ogg`, `.flac`, `.m4a`, `.aac`). The selected file
    is copied into the project directory as `beat.<ext>`, and its original name
    is stored in `project.json`. This makes the project self-contained and
    portable even if the source file is moved or deleted.
    Replacing or removing a beat invalidates `waveform.dat` and clears markers
    because both belong to the previous beat's timeline.
  - **Global instrumentals directory:** A browsable global library at a
    user-accessible path such as `Music/Ghostwriter/Instrumentals/` remains
    planned. Do not treat it as implemented yet.

  **Project Metadata (`project.json`):**
  - Stored inside each project directory using Android's built-in `org.json`
    (`JSONObject`, zero third-party dependencies).
  - Schema captures musical and organizational details:
    ```json
    {
      "version": 2,
      "title": "Song Title",
      "bpm": 92,
      "key": "C# Minor",
      "timeSignature": "4/4",
      "beatFile": "beat.mp3",
      "beatOriginalName": "dark_boombap_92bpm.mp3",
      "markers": [
        { "label": "Hook", "positionMs": 15200 }
      ],
      "createdAt": 1756980000000,
      "updatedAt": 1756985000000,
      "notes": ""
    }
    ```
  - Version 1 projects remain readable and default to an empty marker list.

  **Known limitation, intentional for now:** this directory isn't easily
  user-browsable via a stock file manager because of Android's scoped
  storage rules. That's fine for autosave/backup, but real Import/Export
  (still unbuilt — see roadmap) should use the Storage Access Framework
  (`ACTION_OPEN_DOCUMENT_TREE` + `DocumentFile`) instead, so the user picks
  a real folder they can see. Don't retrofit SAF into the autosave path;
  keep autosave on private storage and add SAF only for import/export.

- **`ui/screens/HomeScreen.kt`** — Entry screen. "New file" button opens an
  `AlertDialog` prompting for a title, which calls `ProjectStorage.projectDir()`
  to create the folder immediately (even before any text is typed) and
  navigates to the editor. A disabled "Import (coming soon)" button is a
  placeholder for later. Below that, a "Recent" list shows existing project
  folders (added proactively — without it, autosaved work could never be
  reopened; this wasn't explicitly requested but was necessary for the
  feature to be useful). Each recent project has an overflow menu with rename
  and delete actions. Rename preserves the project contents, renames the
  title-based manual save, and updates metadata; it rejects a destination name
  that already exists. Deletion has an irreversible-data confirmation and
  removes the complete directory. New-project names are sanitized before
  duplicate matching. A case-insensitive match opens the existing folder using
  its actual casing.

- **`ui/screens/EditorScreen.kt`** — The actual text editor. Full-screen
  editor with a monospace, dark-theme `LyricsNotepad`. The notepad UI is kept
  in `ui/components/LyricsNotepad.kt` so optional left/right gutters can be
  added without mixing their layout into screen-level persistence and beat
  player behavior. Text state uses `rememberSaveable` (not
  plain `remember`) so it survives recomposition without a disk round-trip.
  A `LaunchedEffect` loop calls `delay(intervalSeconds * 1000L)` then
  re-reads settings and calls `rotateAndSave`. A `DisposableEffect`'s
  `onDispose` does one more save when the screen is left (back to Home, or
  into Settings), so nothing is lost between ticks. Top bar includes manual
  save (`<title>.txt`) and quick access to Settings.

  **Offline Beat Player (in Editor):**
  - Integrated directly into the songwriting environment. Songwriters can loop
    and listen to their beats while actively typing lyrics.
  - Powered by the native AOSP `android.media.MediaPlayer` API (no heavy
    libraries like ExoPlayer).
  - Controls: Play/Pause/Resume, Play from start, Loop toggle (enabled by
    default for continuous verse writing), interactive waveform, and volume.
  - When no beat is assigned, an Import beat button opens Android's Storage
    Access Framework picker for supported audio files, then copies the selected
    file into the project directory and begins playback.
  - Displays the selected beat's original filename without its extension.
  - Imports stage the stream copy before replacing audio, so a failed copy
    preserves the previous beat. Imports explicitly reload the player even
    when the destination filename is unchanged. Import is disabled while a
    copy is in progress, and load failures are reported.
  - Lifecycle: Audio playback runs in background while writing and is cleanly
    released on screen disposal.
  - Waveform preparation runs on `Dispatchers.IO`, leaving the UI responsive.
    The player remains unavailable until preparation finishes; navigation and
    save actions are guarded during processing to avoid stale editor work or
    lifecycle crashes. Users can cancel or retry preparation.
  - Audio files at least five minutes long show an explicit warning that
    waveform processing can take a long time before decoding begins.

- **`logic/WaveformExtractor.kt`** — Uses AOSP `MediaExtractor` and
  `MediaCodec` to decode PCM and perform a single-pass O(N) peak-envelope
  reduction into 1,000 evenly distributed amplitude buckets. It supports
  cooperative cancellation and returns an empty result for invalid or
  unsupported audio. Extraction does not hold the storage lock.

- **`logic/WaveformViewport.kt`** — Pure, unit-tested timeline math for
  position/pixel conversion, viewport resize, zoom clamping from 1x to 64x,
  focal-point-preserving zoom, and bounded horizontal panning.

- **`ui/components/WaveformView.kt` and `BeatPlayerPanel.kt`** — A Compose
  `Canvas` draws a vertically centered amplitude waveform, a high-contrast
  playhead, and secondary-accent markers. The timeline supports tap/drag seek,
  pinch zoom, panning, and dedicated zoom controls in the beat-player header.
  Long-press adds a marker; markers can be moved, renamed, deleted, or tapped
  to seek. Dialog and loading/error states live in focused components rather
  than the editor screen.

  **Known simplification:** if the user changes the autosave interval while
  a wait is already in progress, the in-progress wait finishes on the OLD
  interval; the new interval applies starting the following cycle. Not a
  bug, just not instant.

- **`ui/screens/SettingsScreen.kt`** — Two dropdowns (autosave interval,
  backup count) backed by `Settings.kt`, plus an entry to the About screen.
  Deliberately implemented with a
  plain `Box` + `DropdownMenu`/`DropdownMenuItem` rather than Material 3's
  `ExposedDropdownMenuBox` — that API's shape (specifically `menuAnchor()`)
  has changed across library versions, while the basic `DropdownMenu` API
  has stayed stable. If you need more dropdowns later, keep using this same
  pattern rather than switching to `ExposedDropdownMenuBox`, to avoid
  reintroducing that version-drift risk.

- **`ui/screens/AboutScreen.kt`** — Offline project information, installed app
  version, MIT source/license links, and attribution for dictionary datasets
  prepared from Kaikki.org/Wiktionary data. External links are opened only on
  user action and fail gracefully when no browser is installed.

- **`ui/theme/`** — OLED-black palette (`Color.kt`): `#000000` background,
  `#1E1E1E` elevated surfaces, `#F2F2F2` text, `#FF4500` primary controls,
  `#8B5CF6` secondary actions, and `#2A2A2A` outlines/dividers. `Type.kt`
  sets the editor's body text to `FontFamily.Monospace` deliberately — this
  matters for later features, because the syllable-counter gutter and
  bar-counter column (see roadmap) need predictable, fixed-width character
  alignment next to each line, which only works cleanly with a monospace font.

- **`AndroidManifest.xml`** — `MainActivity` has
  `android:configChanges="orientation|screenSize|screenLayout|keyboardHidden"`.
  This was added specifically so the Activity is NOT recreated on rotation,
  which means the in-memory `Screen` navigation state (which has no
  `Saveable` implementation) survives rotation without extra code. This is a
  deliberate trade-off, not an oversight — don't "fix" it by removing
  `configChanges` unless you're also adding a proper `Saver` for the
  navigation sealed class.

- **Tests** — 102 JVM tests cover storage, atomic replacement, waveform cache
  validation/cancellation, extraction math, viewport math, metadata and marker
  compatibility, player state, warning thresholds, and formatting. Twenty-one
  Android instrumented tests cover key Compose screens, dialogs, player states,
  waveform decoding, the lyrics editing surface, Home and Editor behavior,
  persistence, and activity-level navigation.
  Run `./gradlew test lint` locally; build the device suite with
  `./gradlew assembleDebugAndroidTest`.
  Debug local and instrumented tests have JaCoCo coverage enabled. Run
  `./gradlew :app:createCoverageReport` with an emulator or device connected to
  generate the unified HTML report. See [`TESTING.md`](TESTING.md) for all
  coverage tasks, output paths, and the Android Studio workflow.

  **Active manual verification:** The maintainer tests from Android Studio on
  an emulator and performs physical-device testing on an AOSP-based Pixel 8.
  Testing APKs are also distributed to test users for broader real-device
  feedback. Compose gestures, Android document-picker behavior, playback,
  lifecycle transitions, and long-file performance still require these manual
  checks in addition to automated tests.

## 6. Deliberate architectural decisions — please don't silently reverse these

If you think one of these should change, say so and explain why, but don't
just "fix" it without flagging it first:

1. Hand-rolled navigation instead of Navigation-Compose.
2. `SharedPreferences` instead of DataStore for settings.
3. Plain `DropdownMenu` instead of `ExposedDropdownMenuBox`.
4. App-private external storage instead of SAF, for autosave specifically.
5. `configChanges` on the Activity instead of a custom `Saver` for nav state.
6. No Navigation library, no DI framework, no local database — kept minimal
   on purpose while the app is small.
7. `android.media.MediaPlayer` (built into AOSP framework) instead of ExoPlayer/Media3
   for audio playback, maintaining zero added library bloat and native offline AOSP compatibility.
8. `org.json` (built into Android framework) instead of external serialization libraries
   (Gson, Moshi, kotlinx.serialization) for `project.json` metadata.
9. Self-contained project storage (assigned beat files copied into the project directory
   and metadata saved in `project.json`), paired with a global beats directory
   (`Music/Ghostwriter/Instrumentals/`) for browsing and selecting beats.
10. AOSP `MediaExtractor` + `MediaCodec` and Compose `Canvas`/Foundation
    gestures instead of a third-party waveform or media library. This keeps
    waveform generation offline, avoids native binary/licensing and 16 KB page
    compatibility risks, and adds no dependency bloat.

## 7. Known technical debt (not yet addressed, tracked, but not urgent)

- Storage methods run on the calling thread. Periodic autosaves, manual saves,
  metadata saves, beat copies, and deletion use `Dispatchers.IO`; initial reads
  and the editor's final disposal save still run on the main thread. Storage
  mutations share a lock, so a large import can delay another save.
- Player preparation remains synchronous on the main thread. JVM player tests
  cover wrapper state, not real decoding or device lifecycle behavior.
- Beat-file replacement and metadata replacement are separate operations, not
  a single transaction. A metadata-write failure is reported, but may leave the
  new audio paired with old metadata.
- Navigation state (`Screen`) does not survive process death (Android
  killing the app in the background under memory pressure) — only survives
  simple rotation, thanks to `configChanges`. A real fix needs a custom
  `Saver` for the sealed class or a switch to Navigation-Compose (which
  handles this for free).
- Player volume, loop preference, and position remain session-only.
- First-time waveform extraction remains proportional to decoded audio length
  and can take tens of seconds on some devices. It has cancellation, retry,
  caching, and a five-minute warning, but no percentage progress indicator.
- Marker deletion has no undo history. Reassigning a beat does have a warning
  because it deletes the beat file, waveform cache, and all markers.

### Waveform feature review (2026-09-09)

- Replaced the seek slider with a DAW-style waveform, 1x–64x zoom, pan,
  tap/drag seeking, dedicated zoom controls, and persistent editable markers.
- Added project-local waveform caching, five-minute long-audio warnings,
  cancel/retry paths, staged beat replacement, and responsive processing that
  does not hold the storage lock.
- Guarded navigation and saving during preparation and added safe cancellation
  across editor disposal, import, retry, and beat removal.
- Adopted a pure-black OLED background, secondary-accent markers, and a
  distinct high-contrast playhead.
- Refactored player/dialog/storage responsibilities and removed obsolete code.
- `./gradlew test lint assembleDebugAndroidTest` passes: 99 JVM tests execute
  locally and seven Android instrumented tests compile for emulator/device use.

### Maintenance review (2026-09-07, historical checkpoint)

- Extracted `ui/screens/ProjectInfoDialog.kt` from the editor for readability.
- Consolidated staged text writes and optional JSON-string parsing; removed
  unused imports and corrected the player state comment.
- Fixed unchanged-content backup pruning, manual-save recovery fallback,
  same-path beat reloads, failed-copy preservation, duplicate-name resolution,
  misleading save-success messages, player release ordering, and NaN volume.
- Kept the global instrumentals helpers, beat-removal helper, and Home Import
  placeholder for planned features.
- At this checkpoint, `./gradlew test lint` passed with 58 JVM tests. The later
  waveform review above supersedes this count and records emulator/device
  verification requirements.

## 8. Full feature roadmap (from FEATURES.md — keep this file updated as you work)

**Guiding rules:** no AI functionality, no added bloat, works offline by
default.

**Phase 1 — Barebones notepad (MVP)** — ✅ done
- Single full-screen text editor
- Modern, dark-friendly Material 3 UI
- Create/open lyric documents
- Rename lyric documents
- Basic local persistence

**Phase 2 — Songwriting environment** — in progress
- [x] Autosave — continuous autosave, configurable interval, rolling backup
      ring (`autosave1.txt`..`autosaveN.txt`, N configurable)
- [x] **Offline beat / media player** — in-editor background audio player
      for instrumentals while songwriting. Built strictly using Android
      framework's built-in AOSP `MediaPlayer` (no heavy external libraries).
      Controls for play/pause, loop toggle, interactive waveform, and volume.
- [x] **Interactive waveform timeline** — decoded amplitude display with
      tap/drag seek, zoom/pan, a live playhead, persistent named markers,
      project-local caching, cancellation/retry, and long-audio warnings.
- [ ] **Instrumentals library & project beat management** — direct import into
      a project is complete, but the browsable global folder (e.g.
      `Music/Ghostwriter/Instrumentals/` or another user-accessible directory)
      remains to be built. Assigned beats are copied into the project directory
      so projects stay self-contained.
- [x] **Project metadata (`project.json`)** — in-editor Project Info dialog and
      JSON file in each project directory storing musical key, BPM, time signature,
      notes, assigned beat references, and timestamps, using Android's built-in
      `org.json` (no third-party JSON libraries).
- [ ] Cloud sync (optional, user-provided backend/account — must stay
      AOSP-friendly: WebDAV/Nextcloud/S3-compatible, explicitly NOT Firebase)
- [ ] **Syllable counter column** — left-hand gutter, one number per line,
      IDE-style (this is the natural next feature — see §9)
- [ ] **Bar counter** — right-hand side, bars separated by a greyed-out `|`
      or `/` (user-configurable character)
- [ ] **Hyphenation** — auto-hyphenate every word, customizable separator
      character (e.g. `-` or space), toggle on/off
- [ ] **Rhyme detection** — syllable-by-syllable analysis within a bar;
      syllables that rhyme with another syllable get a unique, consistent
      color per rhyme group
- [ ] **Import/export** — plain text at minimum, using the Storage Access
      Framework (see §5's note on `ProjectStorage`); consider `.docx`/`.pdf`
      export later
- [ ] **Dictionary** — word lookup while writing
- [ ] **Rhyme dictionary** — look up rhymes on demand when stuck
- [ ] **Testable code blocks** — core text-processing logic (syllable
      counting, rhyme detection, hyphenation) built as pure, unit-testable
      modules using current Android testing tools (JUnit, Compose UI
      testing)

**Non-goals** (do not implement, even if asked to "improve" the app):
- AI-generated or AI-assisted lyric writing, in any form
- Social features, accounts, or analytics beyond what optional cloud sync
  strictly needs
- Ads, telemetry, or any monetization that compromises "free and open
  source, forever"

## 9. Suggested next task, if you want a starting point

The syllable counter gutter is next in line and is a clean, self-contained
task: given the current line's text, compute an approximate syllable count
(a rule-based English heuristic — vowel-group counting with common
exceptions — is the standard non-AI approach here; do not reach for an LLM
call or an online API for this) and render it as a number in a fixed-width
column to the left of each line in the editor, using the same monospace
font already set up in `Type.kt`. This logic should live in a pure,
testable function (e.g. `SyllableCounter.kt` under a new `logic/` package)
completely separate from the Compose UI, so it can get real unit tests per
the Phase 2 roadmap item on testable code blocks.

That said — confirm with the project owner before starting a specific
feature; they may want a different one first.

## 10. Before you start making changes

1. Read the documentation files in `documentation/`:
   `documentation/README.md`, `documentation/FEATURES.md`,
   `documentation/SETUP_NOTES.md`, and `documentation/AGENT_CONTEXT.md`.
2. Read every file listed in §4 — the project is small enough to read in
   full before editing anything.
3. Check which branch you're on (`dev` for active work; don't commit
   directly to `main`).
4. If you want to add any new dependency, however small, ask first — see
   the "no bloat" rule in §2.
