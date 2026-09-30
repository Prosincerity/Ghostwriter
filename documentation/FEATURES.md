# Feature roadmap

This is the authoritative feature status. Planned items are not commitments
to a release date or implementation order. Checked items are implemented;
unchecked items are planned. Phases group capabilities rather than set a
release schedule.

## Guiding rules

- No AI functionality.
- Work offline by default.
- Keep dependencies small; every feature must earn its place for someone
  writing rap lyrics.
- Use Android/AOSP APIs where adequate; do not require Google Play Services.

## Phase 1 — Barebones notepad (MVP)

- [x] **Full-screen lyric editor** — write and edit lyrics in a focused text editor.
- [x] **Modern, dark-friendly UI** — Material 3 with dark-theme support.
- [x] **Lyric projects** — create, rename, and delete projects.
- [x] **Local persistence** — manual saves and loading saved lyrics after an
  app restart.

## Phase 2 — Songwriting environment

### Lyric editor

- [x] **Autosave** — configurable periodic saves with a configurable number
  of rotating backups; load the newest readable manual or autosave snapshot.
- [x] **Lyric-pad typography** — choose a device font discovered through Android's
  system font API (Android 10+), or a generic platform family, plus font size,
  relative line height, letter spacing, and alignment in Settings, with a live
  preview. Line height (0.5–4×) and letter spacing (−2–10 sp) use sliders.
  Choices apply across projects and follow Android's font scaling.
- [x] **Project information** — musical key, tempo, time signature, notes,
  beat references, and timestamps stored with each project.
- [ ] **Syllable counter column** — left-hand gutter with one count per lyric
  line, independently toggleable from bar numbers.
- [ ] **Bar counter** — right-hand gutter, grouping pairs of lyric lines into
  bars, with an independent toggle and a configurable greyed-out `|` or `/`
  separator.
- [ ] **Hyphenation** — optional automatic word splitting with a configurable
  separator, such as `-` or a space, and an on/off toggle.
- [ ] **Rhyme detection and coloring** — syllable-by-syllable analysis within
  a bar, with a unique, consistent color for each rhyme group.
- [ ] **Text-processing tests** — independently JVM-testable syllable counting,
  bar grouping, rhyme detection, and hyphenation, plus Compose coverage for
  gutter visibility and alignment as these features are implemented.

### Beats and playback

- [x] **Offline beat playback** — import a beat into a project; play, pause,
  restart, loop, and adjust volume from the editor.
- [x] **Background playback** — continue across screens and other apps, with
  Android notification and lock-screen controls. Loop state stays in sync
  with the editor, and audio interruptions pause playback.
- [x] **Waveform timeline and loop markers** — seek, zoom, pan, and manage named markers.
  Marker dialogs offer **Looping: None / Start / End**, with one start and one
  end per beat. Setting a role transfers it from the previous marker. The Loop
  button enables marker looping: a missing start uses 0:00, and a missing end
  uses the end of the beat. Start markers use a brighter shade of purple and
  end markers a darker shade. Loop audio is prepared locally and streamed
  with memory caching, worker prefetch for long beats, live loop boundaries,
  and a 3 ms overlapping crossfade to reduce clicks. Crossfades overlap samples,
  so each repeat is up to 3 ms shorter. Marker positions are saved as audio
  frames; old projects migrate when opened.
  Waveforms are cached; processing supports cancel and retry, with a warning
  for long audio files.
- [ ] **Instrumentals library** — browse and reuse beats across projects;
  assigned beats remain copied into each project.
- [ ] **Android Auto** — browse project instrumentals using offline playback
  and the existing media session, without requiring Google Play Services.

### Dictionaries

- [x] **Rhyme dictionary** — download pronunciation sources independently and
  search for rhymes, assonance, written prefixes, and phonetic suffixes.
  English, German, and Turkish are supported. Installed searches work offline,
  and missing-word pronunciations can be generated locally. See
  [Dictionaries](DICTIONARIES.md) for supported languages and search behavior.
- [ ] **General dictionary** — word definitions alongside pronunciation search
  for word lookup while writing.

### Import, export, and optional sync

- [ ] **Import and export** — plain text first, using Android's Storage Access
  Framework to choose files and destinations; consider `.docx` and `.pdf`
  export later. Autosaves remain in app storage.
- [ ] **Optional cloud sync** — a user-provided backend/account without Google
  Play Services; local writing and playback remain usable offline.

### About and attribution

- [x] **About and attribution** — project information, source links, licenses,
  and dictionary-data notices.

## Implementation guidance

New syllable, bar, rhyme-coloring, and hyphenation logic should remain
independently testable on the JVM. Extend `LyricsNotepad` for gutters while
keeping persistence in `EditorScreen`. Gutter changes also need Compose
coverage for visibility and alignment.

## Non-goals

- AI-generated or AI-assisted writing.
- Social features, analytics, or accounts beyond those needed for optional sync.
- Ads, telemetry, or monetization that compromises software freedom.

Architecture and known limitations are documented in the
[documentation index](README.md).
