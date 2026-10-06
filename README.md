# Ghostwriter

Ghostwriter is a libre, open-source Android app for writing rap lyrics.
It combines a focused lyric editor, local projects, offline beat playback,
an interactive waveform timeline, and pronunciation-based dictionary search.
It has no AI features, ads, or Google Play Services dependency.

Choose a lyric folder on your device before editing. Saved lyrics remain there
when the app is uninstalled; select the same folder after reinstall to restore
your projects. Beat files remain in app storage.

The app is in early development. See the
[features and roadmap](documentation/FEATURES.md) for current capabilities
and planned work.

## Build and test

Open the repository in Android Studio after initializing the native source:

```sh
git submodule update --init third_party/espeak-ng
./gradlew assembleDebug
./gradlew test lint
```

Toolchain requirements and SDK configuration are documented in
[Build setup](documentation/SETUP_NOTES.md). See
[Testing](documentation/TESTING.md) for Android tests and coverage reports.

## Documentation and contributions

The [documentation index](documentation/README.md) links to architecture,
dictionary behavior, native builds, and release procedures.

Issues and pull requests are welcome. Follow the project constraints and
workflow in [AGENTS.md](AGENTS.md): keep features offline by default,
dependencies small, and the writing experience free of AI assistance.

## License

GNU General Public License v3.0 or later (`GPL-3.0-or-later`); see
[LICENSE](LICENSE). Third-party software and dictionary data have separate
notices in [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md).
