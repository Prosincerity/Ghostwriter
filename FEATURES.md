# Feature Roadmap

This document is the single source of truth for where Gh0stwrit3r.exe is headed. Keep it updated as features land.

## Guiding rules

- No AI functionality. Ever.
- No added bloat — every feature must earn its place for someone writing rap lyrics.
- Works offline by default.

## Phase 1 — Barebones notepad (MVP)

- [ ] Single full-screen text editor
- [ ] Modern, clean, dark-friendly UI (Material 3)
- [ ] Create / rename / delete lyric documents
- [ ] Basic local persistence (survives app restart)

## Phase 2 — Songwriting environment

- [ ] **Syllable counter column** — left-hand gutter, one number per line, IDE-style
- [ ] **Bar counter** — right-hand side, bars separated by a greyed-out `|` or `/` (user-configurable)
- [ ] **Hyphenation** — auto-hyphenate every word, customizable separator character (e.g. `-` or space), toggle on/off
- [ ] **Rhyme detection** — syllable-by-syllable analysis within a bar; syllables that rhyme with another syllable get a unique, consistent color per rhyme group
- [ ] **Autosave** — continuous autosave, rolling autosave backups, optional cloud sync (user-provided backend/account)
- [ ] **Import / export** — plain text at minimum; consider `.docx`/`.pdf` export later
- [ ] **Dictionary** — word lookup while writing
- [ ] **Rhyme dictionary** — look up rhymes on demand when stuck
- [ ] **Testable code blocks** — core text-processing logic (syllable counting, rhyme detection, hyphenation) built as pure, unit-testable modules using current Android testing tools (JUnit, Compose UI testing, etc.)

## Non-goals

- AI-generated or AI-assisted lyric writing
- Social features, accounts, or analytics beyond what's needed for optional cloud sync
- Ads, telemetry, or monetization that compromises the "free and open source" promise
