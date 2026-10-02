# Native pronunciation engine

Ghostwriter uses the eSpeak NG core to generate IPA locally when dictionary
lookup misses. This integration does not synthesize audio or expose a TTS
service. Lookup behavior is documented in [Dictionaries](DICTIONARIES.md).

## Source and Android build

The native source is the [`third_party/espeak-ng`](../third_party/espeak-ng)
submodule. Its recorded Git revision is the source pin:

```sh
git ls-tree HEAD third_party/espeak-ng
```

Follow [Build setup](SETUP_NOTES.md) to initialize it and build the app.
[`app/build.gradle.kts`](../app/build.gradle.kts) defines NDK/CMake versions,
packaged ABIs, and the `ghostwriter_ipa` build target.
[`CMakeLists.txt`](../app/src/main/cpp/CMakeLists.txt) links the upstream core
statically into the JNI library. It disables optional audio components and
asynchronous synthesis, and bypasses the upstream Sonic fetch while Sonic is
disabled. The Android build does not build the upstream command-line tools
or data compiler.

## Regenerating language data

On a Linux host with CMake, Ninja, and a C/C++ compiler, run from the repository
root:

```sh
scripts/generate_espeak_data.sh
```

The [generation script](../scripts/generate_espeak_data.sh) builds the pinned
host CLI and compiles the supported language dictionaries with their shared
phoneme and intonation data. It defines the packaged asset paths and compares
reduced-data output against IPA fixtures. Review generated asset changes
before committing them.

Native code and compiled data must use the same eSpeak revision. If updating
the engine, keep the generator, asset bundle, runtime version directory, and
`DATA_FILES` list in [`EspeakIpa.kt`](../app/src/main/java/com/prosincerity/ghostwriter/data/EspeakIpa.kt)
consistent. Compare the host and JNI output with the dictionary producer's
`espeak-ng -q --ipa -v <language>` convention before adopting a new version.

## Runtime contract

The JNI bridge initializes the engine from an app-private data directory and
converts a UTF-8 word using a supported language voice. Kotlin lazily loads the
library and installs bundled data under an initialization lock on the IO
dispatcher. Native calls serialize eSpeak's global state and copy returned
IPA bytes into owned memory before releasing the lock.

Missing data and initialization failures are reported through the lookup
error path. Unsupported languages and invalid input do not produce IPA.
Bundled data is installed through a staging directory and reused once complete.

## Verification and distribution

Host fixture checks run in the generation script. Android tests compare real
JNI output with host fixtures and exercise a database-miss fallback. See
[Testing](TESTING.md) for execution responsibilities and coverage limits.

Follow [Releasing](RELEASING.md) for native packaging, memory-page
compatibility checks, and the combined app/native source archive.

Preserve upstream source and notices. Authoritative license and attribution
details are in [THIRD_PARTY_LICENSES.md](../THIRD_PARTY_LICENSES.md).
