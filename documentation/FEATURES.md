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
- [ ] Rename lyric documents
- [x] Basic local persistence (survives app restart)

## Phase 2 — Songwriting environment

- [ ] **Syllable counter column** — left-hand gutter, one number per line, IDE-style
- [ ] **Bar counter** — right-hand side, bars separated by a greyed-out `|` or `/` (user-configurable)
- [ ] **Hyphenation** — auto-hyphenate every word, customizable separator character (e.g. `-` or space), toggle on/off
- [ ] **Rhyme detection** — syllable-by-syllable analysis within a bar; syllables that rhyme with another syllable get a unique, consistent color per rhyme group
- [x] **Autosave** — continuous autosave with a configurable interval, plus a rolling ring of `autosave1.txt`..`autosaveN.txt` backups (N configurable)
- [x] **Offline beat / media player** — in-editor background audio player for instrumentals while songwriting. Built strictly using Android framework's built-in AOSP `MediaPlayer` (no heavy external libraries). Controls for play/pause, loop toggle, seek bar, and volume.
- [ ] **Instrumentals library & project beat management** — importing a selected beat directly into its project is complete; a browsable global instrumentals library (e.g. `Music/Ghostwriter/Instrumentals/` or another user-accessible directory) remains to be built. Assigned beats are copied into the project directory so projects stay self-contained.
- [x] **Project metadata (`project.json`)** — in-editor Project Info dialog and JSON file in each project directory storing musical key, BPM, time signature, notes, assigned beat references, and timestamps, using Android's built-in `org.json` (no third-party JSON libraries).
- [ ] **Cloud sync** (optional, user-provided backend/account — AOSP-friendly, so avoid anything requiring Google Play Services)
- [ ] **Import / export** — plain text at minimum; consider `.docx`/`.pdf` export later
- [ ] **Dictionary** — word lookup while writing
- [ ] **Rhyme dictionary** — look up rhymes on demand when stuck
- [ ] **Testable code blocks** — core text-processing logic (syllable counting, rhyme detection, hyphenation) built as pure, unit-testable modules using current Android testing tools (JUnit, Compose UI testing, etc.)

## Non-goals

- AI-generated or AI-assisted lyric writing
- Social features, accounts, or analytics beyond what's needed for optional cloud sync
- Ads, telemetry, or monetization that compromises the "free and open source" promise
