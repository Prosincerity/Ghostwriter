# ghostwriter.exe

A free, open-source, no-bloat, no-AI Android app for writing rap lyrics — built to eventually feel like an IDE for bars.

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
- Android Studio + Gradle (Kotlin DSL)

## Roadmap

See [FEATURES.md](FEATURES.md) for the full, detailed feature roadmap.

**Phase 1 — MVP (current):**
- Barebones plain-text notepad, one screen, modern dark UI.

**Phase 2 — Songwriting environment:**
- Syllable counter column (left, per line, IDE-style)
- Bar counter (right, greyed-out separator: `|` or `/`)
- Customizable hyphenation
- Syllable-level rhyme detection with per-rhyme-group coloring
- Autosave, autosave backups, optional cloud sync
- Import / export lyrics
- Dictionary + rhyme dictionary
- Tested code, following current Android testing best practices

## Building

1. Open this folder in Android Studio (Meerkat or newer).
2. Let Gradle sync.
3. Run on an emulator or device (min SDK / target SDK as set in `app/build.gradle.kts`).

## Development

Built solo, pair-programmed with AI assistants: originally scaffolded and developed through Phase 2 autosave using Anthropic Claude, and actively developed using Google Antigravity. (Note: the app itself strictly contains no AI features or bloat — see philosophy).

## Contributing

Issues and PRs welcome once the initial scaffolding is in place. Please keep the "no AI, no bloat" philosophy in mind for any feature contribution.

## License

MIT — see [LICENSE](LICENSE). You are free to use, modify, and redistribute this code.
