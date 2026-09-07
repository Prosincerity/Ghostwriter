# Ghostwriter

**Songwriting Environment**

Ghostwriter is a free, open-source, no-bloat, no-AI Android app for writing rap lyrics — built to eventually feel like an IDE for bars.

## Status

🚧 **Early development.** Currently a barebones, modern-UI notepad. Feature roadmap below.

## Philosophy

- Free and open source, forever.
- No AI-generated lyrics, no AI "assist" features, no bloat.
- Fast, offline-first, distraction-free writing.
- Built to grow from a plain notepad into a full songwriting environment without changing that philosophy.

## Tech stack

- Kotlin
- Jetpack Compose + Material 3 (modern declarative UI)
- Android AOSP framework (built-in `android.media.MediaPlayer`, built-in `org.json`)
- Android Studio + Gradle (Kotlin DSL)

## Documentation

Comprehensive project documentation lives in the [`documentation/`](documentation/) folder:

- **[Feature Roadmap](documentation/FEATURES.md)** — Phased feature list and progress tracking
- **[Agent Context & Architecture](documentation/AGENT_CONTEXT.md)** — Core design principles, storage layouts, media player specs, and decisions
- **[Setup Notes](documentation/SETUP_NOTES.md)** — Bootstrapping and setup history

## Roadmap

See [FEATURES.md](documentation/FEATURES.md) for the full, detailed feature roadmap.

**Phase 1 — MVP (completed):**
- Barebones plain-text notepad, modern dark UI, local file persistence.

**Phase 2 — Songwriting environment (in progress):**
- Autosave & rolling backup ring (`autosave1.txt`..`autosaveN.txt`) (completed)
- In-editor project info dialog & project metadata (`project.json` for musical key, BPM, timestamps) (completed)
- In-editor offline beat / media player with project-local beat import (completed; built-in AOSP `MediaPlayer`)
- Instrumentals library & project beat management (global beats library still planned; imported project beats are self-contained)
- Syllable counter column (left, per line, IDE-style)
- Bar counter (right, greyed-out separator: `|` or `/`)
- Customizable hyphenation
- Syllable-level rhyme detection with per-rhyme-group coloring
- Import / export lyrics
- Dictionary + rhyme dictionary
- Tested code, following current Android testing best practices

## Building

1. Open this folder in Android Studio (Meerkat or newer).
2. Let Gradle sync.
3. Run on an emulator or device (min SDK / target SDK as set in `app/build.gradle.kts`).

## Development

Built solo with assisted coding tools: originally scaffolded and developed through Phase 2 autosave using Anthropic Claude, then actively developed with Google Antigravity and OpenAI Codex. (Note: the app itself strictly contains no AI features or bloat — see philosophy).

## Contributing

Issues and PRs welcome once the initial scaffolding is in place. Please keep the "no AI, no bloat" philosophy in mind for any feature contribution.

## License

MIT — see [LICENSE](LICENSE). You are free to use, modify, and redistribute this code.
