# Features and roadmap

This is the authoritative feature status. Planned items are not commitments
to a release date or implementation order.

## Implemented

- **Lyric projects:** create, rename, and delete projects; write in a
  full-screen Material 3 editor with dark-theme support.
- **Local persistence:** manual saves and configurable periodic autosave
  with rotating backups.
- **Project information:** musical key, tempo, time signature, notes,
  beat references, and timestamps stored with each project.
- **Offline beat playback:** import a beat into a project; play, pause,
  restart, loop, and adjust volume from the editor.
- **Background playback:** continue across screens and other apps, with
  Android notification and lock-screen controls. Loop state stays in sync
  with the editor, and audio interruptions pause playback.
- **Waveform timeline:** seek, zoom, pan, and manage named markers.
  Waveforms are cached; processing supports cancel and retry, with a warning
  for long audio files.
- **Rhyme dictionary:** download pronunciation sources independently and
  search for rhymes, assonance, written prefixes, and phonetic suffixes.
  Missing-word pronunciations can be generated locally. See
  [Dictionaries](DICTIONARIES.md) for supported languages and search behavior.
- **About and attribution:** project information, source links, licenses,
  and dictionary-data notices.

## Planned

| Feature | Intended scope |
| --- | --- |
| Editor gutters | Left syllable counts and right bar numbers, with independent toggles; bars group pairs of lyric lines |
| Hyphenation | Optional word splitting with a configurable separator |
| Rhyme coloring | Syllable analysis within a bar, with consistent colors for rhyme groups |
| Instrumentals library | Browse reusable beats; assigned beats remain copied into each project |
| Import and export | Plain text first; richer document formats may follow |
| General dictionary | Word definitions alongside pronunciation search |
| Android Auto | Browse project instrumentals using offline playback and the existing media session |
| Optional cloud sync | A user-provided backend that does not require Google Play Services |

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
