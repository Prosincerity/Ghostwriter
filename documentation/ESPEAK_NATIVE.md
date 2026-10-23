# Native pronunciation engine

This guide describes the eSpeak NG JNI integration and bundled-data generation.

## Source and build

The source pin is the Git revision recorded for
[`third_party/espeak-ng`](../third_party/espeak-ng):

```sh
git ls-tree HEAD third_party/espeak-ng
```

Initialize it through [Build setup](SETUP_NOTES.md#configure-the-checkout).
[App configuration](../app/build.gradle.kts) declares the NDK, CMake, packaged
ABIs, and `ghostwriter_ipa` target.

[`CMakeLists.txt`](../app/src/main/cpp/CMakeLists.txt) links the upstream core
statically into the JNI library. It disables optional audio components and
asynchronous synthesis, and bypasses the upstream Sonic fetch.
The Android build does not build the upstream CLI or data compiler.

## Generate language data

On Linux, install CMake, Ninja, and a C/C++ compiler. Run from the repository root:

```sh
scripts/generate_espeak_data.sh
```

The [script](../scripts/generate_espeak_data.sh) builds the pinned host CLI,
compiles language dictionaries and shared phoneme data, copies the required
assets, and compares reduced-data IPA output with fixtures.
Review generated asset changes before committing.

Native code and data must use the same source revision. Keep the generator,
asset bundle, runtime version directory, and `DATA_FILES` list in
[`EspeakIpa.kt`](../app/src/main/java/com/prosincerity/ghostwriter/dictionary/data/EspeakIpa.kt)
consistent when updating the engine.

## Runtime contract

`EspeakIpa` lazily loads the JNI library and stages bundled data in app-private
storage on the IO dispatcher. An initialization mutex protects setup.
The JNI bridge serializes eSpeak's global state and copies returned IPA bytes
into owned memory before releasing its lock.

The bridge generates IPA from a UTF-8 word using a supported voice.
It does not synthesize audio or expose a TTS service.
Unsupported languages and invalid input return no IPA; missing data and
initialization failures follow the lookup error path.

See [Pronunciation lookup](DICTIONARIES.md#pronunciation-lookup) for fallback
precedence and [Testing](TESTING.md) for device verification.

Follow [Releasing](RELEASING.md) for packaging, page-size checks, and matching
source distribution. Preserve notices listed in
[THIRD_PARTY_LICENSES.md](../THIRD_PARTY_LICENSES.md).
