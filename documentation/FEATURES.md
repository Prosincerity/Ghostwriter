# Feature Roadmap

This document is the single source of truth for where Ghostwriter is headed. Keep it updated as features land.

## Guiding rules

- No AI functionality. Ever.
- No added bloat — every feature must earn its place for someone writing rap lyrics.
- Works offline by default.

## Phase 1 — Barebones notepad (MVP)

- [x] Single full-screen text editor
- [x] Modern, clean, dark-friendly UI (Material 3)
- [x] Create / delete lyric documents
- [x] Rename lyric documents
- [x] Basic local persistence (survives app restart)
- [x] About screen with project, source, license, and dictionary-data attribution links

## Phase 2 — Songwriting environment

- [ ] **Editor gutters** — extend `LyricsNotepad` with optional IDE-style
  columns while keeping persistence in `EditorScreen` unchanged:
  - Left gutter: show the syllable count for the current lyric line.
  - Right gutter: number bars, with one bar counted for every two lyric lines.
  - Settings: independent **Syllable counter** and **Bar counter** toggles;
    disabling either setting removes its corresponding gutter.
- [ ] **Hyphenation** — auto-hyphenate every word, customizable separator character (e.g. `-` or space), toggle on/off
- [ ] **Rhyme detection** — syllable-by-syllable analysis within a bar; syllables that rhyme with another syllable get a unique, consistent color per rhyme group
- [x] **Autosave** — continuous autosave with a configurable interval, plus a rolling ring of `autosave1.txt`..`autosaveN.txt` backups (N configurable)
- [x] **Offline beat / media player** — in-editor background audio player for instrumentals while songwriting. Built strictly using Android framework's built-in AOSP `MediaPlayer` (no heavy external libraries). Controls for play/pause, play from start, loop toggle, and volume.
- [x] **Interactive waveform timeline** — actual decoded amplitude waveform replaces the seek slider. Includes tap/drag seeking, pinch zoom, dedicated zoom buttons, horizontal panning, a centered high-contrast playhead, and project-persistent named markers that can be added, moved, renamed, deleted, and tapped to seek.
- [x] **Waveform processing safety** — extraction runs off the UI thread and is cached per project. Processing can be cancelled or retried without leaving partial cache/import files; editor navigation and save actions are guarded while processing; files at least five minutes long show a warning that processing can take a long time.
- [ ] **Instrumentals library & project beat management** — importing a selected beat directly into its project is complete; a browsable global instrumentals library (e.g. `Music/Ghostwriter/Instrumentals/` or another user-accessible directory) remains to be built. Assigned beats are copied into the project directory so projects stay self-contained.
- [x] **Project metadata (`project.json`)** — in-editor Project Info dialog and JSON file in each project directory storing musical key, BPM, time signature, notes, assigned beat references, and timestamps, using Android's built-in `org.json` (no third-party JSON libraries).
- [ ] **Cloud sync** (optional, user-provided backend/account — AOSP-friendly, so avoid anything requiring Google Play Services)
- [ ] **Import / export** — plain text at minimum; consider `.docx`/`.pdf` export later
- [ ] **Dictionary** — word lookup while writing
- [ ] **Rhyme dictionary** — look up rhymes on demand when stuck
- [ ] **Testable code blocks** — keep syllable counting, bar counting, rhyme
  detection, and hyphenation as pure logic with JVM tests; cover gutter
  visibility and alignment with Compose tests.

## Non-goals

- AI-generated or AI-assisted lyric writing
- Social features, accounts, or analytics beyond what's needed for optional cloud sync
- Ads, telemetry, or monetization that compromises the "free and open source" promise
