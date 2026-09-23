# eSpeak NG offline IPA fallback

Ghostwriter uses eSpeak NG 1.52.0 only to generate IPA for words absent from the
installed Wiktionary and eSpeak databases. It has no TTS service, audio output,
microphone access, or network request in this path. The native source is the
`third_party/espeak-ng` submodule, pinned to commit
`4870adfa25b1a32b4361592f1be8a40337c58d6c` (release `1.52.0`).

## Clean checkout and build

```sh
git submodule update --init third_party/espeak-ng
./gradlew assembleDebug
./gradlew test lint compileDebugAndroidTestKotlin
```

The Android build pins NDK `30.0.16248370` and CMake `4.1.2`, and packages
`arm64-v8a` and `x86_64`. `app/src/main/cpp/CMakeLists.txt` links the upstream
core statically into one JNI library and disables MBROLA, Sonic, PcAudio,
Klatt, speechPlayer, and asynchronous synthesis. Gradle builds only the
`ghostwriter_ipa` target, so the upstream CLI and data compiler are not built
for Android. The CMake adapter also bypasses an unconditional Sonic fetch in
upstream 1.52; Sonic remains disabled. No additional app dependency is introduced.

The two native library segments are 16 KB aligned with this NDK. The APK's
native entries were verified using `zipalign -c -P 16 -v 4`. Device behavior on
a 16 KB page size emulator or device still needs confirmation.

## Regenerate the packaged data

On a Linux build host with CMake, Ninja, and a C/C++ compiler, run:

```sh
scripts/generate_espeak_data.sh
```

The script builds the pinned host CLI and compiles only the `en`, `de`, and
`tr` dictionaries plus shared phoneme and intonation data. It copies the
following files into `app/src/main/assets/espeak-ng-1.52/espeak-ng-data/`:

```text
intonations  phondata  phonindex  phontab
en_dict  de_dict  tr_dict
lang/gmw/en  lang/gmw/de  lang/trk/tr
```

The script also compares reduced-data CLI output with pinned IPA fixtures for
`ghostwriter`, `Übermut`, and `ışık`. The generated data and native code must
stay on the same eSpeak revision. At runtime, the app copies these assets once
into a versioned app-private directory before initializing the engine.

## API and verification

The JNI bridge has two operations: initialize with the app-private data path,
and translate one UTF-8 word using the selected `en`, `de`, or `tr` voice. It
copies eSpeak's returned IPA bytes before releasing the engine lock. Kotlin
normalizes input to NFC, runs lookup on `Dispatchers.IO`, and uses generated
IPA only after both local SQLite databases miss. The generated result has a
separate source label and is never written into a downloaded database.

Run `EspeakIpaInstrumentedTest` from Android Studio on an x86_64 emulator or
ARM64 device. It compares the JNI output against the three host fixtures and
checks invalid input. Then verify an OOV lookup with airplane mode enabled.
These device checks remain pending.

## Source and licensing

eSpeak NG is copyright its contributors and is licensed under GPL version 3
or later. The pinned source, its `COPYING` file, and notices for bundled
components are in `third_party/espeak-ng`; its upstream source and license
are linked from the app's About screen. Preserve these notices and the exact
source revision when distributing the APK and corresponding source. The
downloaded pronunciation databases have separate data attribution and
licensing, also linked from About.
