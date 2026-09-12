# Ghostwriter

**Songwriting Environment**

Ghostwriter is a free, open-source, no-bloat, no-AI Android app for writing rap lyrics — built to eventually feel like an IDE for bars.

## Status

🚧 **Early development.** The core songwriting editor, project persistence,
offline beat playback, and interactive waveform timeline are implemented.
Feature roadmap below.

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

Project documentation lives in [`documentation/`](documentation/):

- [Feature roadmap](documentation/FEATURES.md)
- [Architecture and agent context](documentation/AGENT_CONTEXT.md)
- [Testing and coverage](documentation/TESTING.md)
- [Build setup](documentation/SETUP_NOTES.md)

## Roadmap

Phase 1 is complete and Phase 2 is in progress. The next editor work includes
optional IDE-style gutters: a syllable counter on the left and a bar counter
on the right, each controlled by its own setting. See the
[feature roadmap](documentation/FEATURES.md) for the authoritative status and
remaining work.

## Building

1. Open this folder in a version of Android Studio compatible with the checked-in Android Gradle Plugin.
2. Let Gradle sync.
3. Run on an emulator or device (min SDK / target SDK as set in `app/build.gradle.kts`).

## Testing

- JVM tests and lint run through Gradle (`./gradlew test lint`).
- Compose instrumented tests run from Android Studio on an Android emulator.
- JaCoCo can generate local, instrumented, or unified HTML coverage reports.
  See [`documentation/TESTING.md`](documentation/TESTING.md) for the commands,
  Android Studio workflow, output paths, and ADB troubleshooting.
- Active physical-device testing uses an AOSP-based Pixel 8. Testing APKs are
  also sent to test users for additional real-device feedback.

## Contributing

Issues and pull requests are welcome. Please keep the no-AI, no-bloat
philosophy in mind for every contribution.

## License

MIT — see [LICENSE](LICENSE). You are free to use, modify, and redistribute this code.
