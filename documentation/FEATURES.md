# Features

This checklist records implemented features and planned work.

Checked items are **Implemented**. Unchecked items are **Planned** and have no
release commitment. [Contributor instructions](../AGENTS.md#project-constraints)
define project constraints and non-goals.

## Projects and lyrics

- [x] **Lyric editor:** edit lyrics in a full-screen notepad.
- [x] **Project management:** create, rename, delete, and reopen projects.
- [x] **Local persistence:** save manually, autosave at a configurable interval,
  save when the activity stops and on editor exit; configure rotating backups.
- [x] **Shared lyric folder:** retain lyrics and project metadata outside app
  storage. See [Project storage](README.md#project-storage) for recovery limits.
- [x] **Project information:** store tempo, musical key, time signature, notes,
  beat references, and timestamps.
- [x] **Typography settings:** select a device font or System default, size,
  line height, letter spacing, and alignment; preview changes in Settings.
  Settings apply across projects and respect Android font scaling.
- [x] **Dark interface:** use a dark Material 3 theme in either device appearance.
- [ ] **Syllable counter:** show a left gutter with per-line counts,
  independently selectable from bar numbers.
- [ ] **Bar counter:** show a right gutter grouping pairs of lyric lines, with
  an optional greyed-out `|` or `/` separator.
- [ ] **Hyphenation:** split words automatically with a configurable separator
  and an on/off toggle.
- [ ] **Rhyme coloring:** assign consistent colors to matching syllables within a bar.
- [ ] **Text-processing verification:** add JVM tests for counters, hyphenation,
  and rhyme analysis, plus UI tests for gutter visibility and alignment.

## Beats and playback

- [x] **Beat import:** copy an audio file into a project.
- [x] **Transport controls:** play, pause, restart, adjust volume, and mute.
- [x] **Collapsible player:** minimize and reopen the Beat Player panel while
  preserving playback and waveform zoom.
- [x] **Background playback:** use notification and lock-screen controls;
  pause for audio interruptions and stop when dismissed from Recents.
- [x] **Waveform timeline:** seek, pan, zoom, and add, move, rename, or delete markers.
- [x] **Loop modes:** select Off, Whole beat, or Markers; assign start and end
  markers. See [Playback architecture](README.md#playback-and-waveforms).
- [x] **Waveform preparation:** cache peaks, warn before long-file processing,
  and provide cancellation and retry actions.
- [x] **Interaction haptics:** provide native feedback for transport, marker,
  and deletion actions while respecting system settings.
- [ ] **Instrumentals library:** browse and reuse beats across projects.
- [ ] **Android Auto:** browse project instrumentals through the media session.

## Dictionaries

- [x] **Pronunciation search:** download independent sources and search installed
  data offline. See [Dictionaries](DICTIONARIES.md) for languages and modes.
- [ ] **Definitions:** provide general word lookup alongside pronunciation search.

## Import, export, and sync

- [ ] **File import and export:** support plain text through Android's Storage
  Access Framework; consider `.docx` and `.pdf` later.
- [ ] **Optional sync:** use a user-provided backend or account while retaining
  local writing and playback.

## About

- [x] **Attribution:** display project information, source links, licenses,
  and dictionary-data notices.
