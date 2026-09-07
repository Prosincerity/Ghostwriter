# Project handoff: Ghostwriter

## 1. What this project is

**Ghostwriter** is a free, open-source Android app for writing rap lyrics,
built solo. Its tagline is **Songwriting Environment**. The repository is MIT
licensed and public. It currently runs as a barebones notepad and is being
grown, feature by feature, into a full songwriting environment.

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
│       │       │   └── ProjectStorage.kt   ← file I/O, autosaves, metadata & beat storage
│       │       └── ui/
│       │           ├── screens/
│       │           │   ├── HomeScreen.kt
│       │           │   ├── EditorScreen.kt
│       │           │   └── SettingsScreen.kt
│       │           └── theme/
│       │               ├── Color.kt        ← dark "terminal" palette
│       │               ├── Theme.kt
│       │               └── Type.kt         ← monospace typography
│       ├── test/java/com/prosincerity/ghostwriter/  ← unit tests (ProjectStorageTest, SettingsFormatTest)
│       └── androidTest/java/com/prosincerity/ghostwriter/
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

Walking through what each file does:

- **`MainActivity.kt`** — Hosts a tiny hand-rolled navigation system: a
  private `sealed class Screen` with `Home`, `Editor(projectTitle)`, and
  `SettingsFrom(projectTitle)` variants, switched on with a `when` inside a
  `GhostwriterApp()` composable. This is deliberately NOT using the
  Navigation-Compose library — three screens didn't justify the dependency.
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
  - **Global instrumentals directory:** A browsable global library at a
    user-accessible path such as `Music/Ghostwriter/Instrumentals/` remains
    planned. Do not treat it as implemented yet.

  **Project Metadata (`project.json`):**
  - Stored inside each project directory using Android's built-in `org.json`
    (`JSONObject`, zero third-party dependencies).
  - Schema captures musical and organizational details:
    ```json
    {
      "version": 1,
      "title": "Song Title",
      "bpm": 92,
      "key": "C# Minor",
      "timeSignature": "4/4",
      "beatFile": "beat.mp3",
      "beatOriginalName": "dark_boombap_92bpm.mp3",
      "createdAt": 1756980000000,
      "updatedAt": 1756985000000,
      "notes": ""
    }
    ```

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
  feature to be useful). Each recent project has a delete action with an
  irreversible-data confirmation; deletion removes its complete directory.
  New-project names are sanitized before duplicate matching. A case-insensitive
  match opens the existing folder using its actual casing.

- **`ui/screens/EditorScreen.kt`** — The actual text editor. Full-screen
  `TextField`, monospace, dark theme. Text state uses `rememberSaveable` (not
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
    default for continuous verse writing), seek scrubber, and volume.
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

  **Known simplification:** if the user changes the autosave interval while
  a wait is already in progress, the in-progress wait finishes on the OLD
  interval; the new interval applies starting the following cycle. Not a
  bug, just not instant.

- **`ui/screens/SettingsScreen.kt`** — Two dropdowns (autosave interval,
  backup count) backed by `Settings.kt`. Deliberately implemented with a
  plain `Box` + `DropdownMenu`/`DropdownMenuItem` rather than Material 3's
  `ExposedDropdownMenuBox` — that API's shape (specifically `menuAnchor()`)
  has changed across library versions, while the basic `DropdownMenu` API
  has stayed stable. If you need more dropdowns later, keep using this same
  pattern rather than switching to `ExposedDropdownMenuBox`, to avoid
  reintroducing that version-drift risk.

- **`ui/theme/`** — Dark, "terminal/hacker notebook" palette (`Color.kt`):
  near-black background, a terminal-green accent color. `Type.kt` sets the
  editor's body text to `FontFamily.Monospace` deliberately — this matters
  for later features, because the syllable-counter gutter and bar-counter
  column (see roadmap) need predictable, fixed-width character alignment
  next to each line, which only works cleanly with a monospace font.

- **`AndroidManifest.xml`** — `MainActivity` has
  `android:configChanges="orientation|screenSize|screenLayout|keyboardHidden"`.
  This was added specifically so the Activity is NOT recreated on rotation,
  which means the in-memory `Screen` navigation state (which has no
  `Saveable` implementation) survives rotation without extra code. This is a
  deliberate trade-off, not an oversight — don't "fix" it by removing
  `configChanges` unless you're also adding a proper `Saver` for the
  navigation sealed class.

- **Test files** (`ProjectStorageTest.kt`, `ProjectStorageBeatTest.kt`,
  `ProjectMetadataTest.kt`, `BeatPlayerTest.kt`, `SettingsFormatTest.kt`) —
  Unit tests cover storage creation, deletion, autosave rotation, manual saves,
  project metadata, beat storage helpers, player state, and settings interval
  formatting. Run locally via JUnit.

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

### Maintenance review (2026-09-07)

- Extracted `ui/screens/ProjectInfoDialog.kt` from the editor for readability.
- Consolidated staged text writes and optional JSON-string parsing; removed
  unused imports and corrected the player state comment.
- Fixed unchanged-content backup pruning, manual-save recovery fallback,
  same-path beat reloads, failed-copy preservation, duplicate-name resolution,
  misleading save-success messages, player release ordering, and NaN volume.
- Kept the global instrumentals helpers, beat-removal helper, and Home Import
  placeholder for planned features.
- `./gradlew test lint` passed with 58 JVM tests. Emulator checks remain needed
  for the picker, playback, and screen lifecycle changes.

## 8. Full feature roadmap (from FEATURES.md — keep this file updated as you work)

**Guiding rules:** no AI functionality, no added bloat, works offline by
default.

**Phase 1 — Barebones notepad (MVP)** — ✅ done
- Single full-screen text editor
- Modern, dark-friendly Material 3 UI
- Create/open lyric documents
- Basic local persistence

**Phase 2 — Songwriting environment** — in progress
- [x] Autosave — continuous autosave, configurable interval, rolling backup
      ring (`autosave1.txt`..`autosaveN.txt`, N configurable)
- [x] **Offline beat / media player** — in-editor background audio player
      for instrumentals while songwriting. Built strictly using Android
      framework's built-in AOSP `MediaPlayer` (no heavy external libraries).
      Controls for play/pause, loop toggle, seek bar, and volume.
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
