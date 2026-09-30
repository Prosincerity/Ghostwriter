# Features and roadmap

This is the authoritative feature status. Planned items are not commitments
to a release date or implementation order.

## Implemented

- **Lyric projects:** create, rename, and delete projects; write in a
  full-screen Material 3 editor with dark-theme support.
- **Local persistence:** manual saves and configurable periodic autosave
  with rotating backups.
- **Lyric-pad typography:** choose a device font discovered through Android's
  system font API (Android 10+), or a generic platform family, plus font size,
  relative line height, letter spacing, and alignment in Settings, with a live
  preview. Line height (0.5–4×) and letter spacing (−2–10 sp) use sliders.
  Choices apply across projects and follow Android's font scaling.
- **Project information:** musical key, tempo, time signature, notes,
  beat references, and timestamps stored with each project.
- **Offline beat playback:** import a beat into a project; play, pause,
  restart, loop, and adjust volume from the editor.
- **Background playback:** continue across screens and other apps, with
  Android notification and lock-screen controls. Loop state stays in sync
  with the editor, and audio interruptions pause playback.
- **Waveform timeline:** seek, zoom, pan, and manage named markers.
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
