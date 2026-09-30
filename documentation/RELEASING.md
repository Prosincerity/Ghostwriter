# Releasing

## Build and verify

Use a clean, tagged commit containing the intended release changes. Initialize
the recorded native source and check the working trees:

```sh
git submodule update --init third_party/espeak-ng
git status --short
git -C third_party/espeak-ng status --short
git submodule status
./gradlew test lint assembleRelease assembleDebugAndroidTest
```

Both status commands should produce no output. The eSpeak submodule status
must start with a space, indicating that its checkout matches the recorded
revision. Toolchain and native-data requirements are described in
[Build setup](SETUP_NOTES.md) and [Native pronunciation engine](ESPEAK_NATIVE.md).

The release build produces an unsigned APK. Sign the verified build with the
release key, then inspect the distributed APK:

- Native libraries are present for every ABI configured in Gradle.
- The language assets listed in `EspeakIpa.kt` are present.
- `assets/licenses/` contains the complete project and third-party notices,
  including the dictionary producer's attribution and modification notice.
- Downloaded SQLite dictionaries are outside the APK.
- Native entries pass the SDK alignment check:

  ```sh
  zipalign -c -P 16 -v 4 path/to/release.apk
  ```

The APK path is a placeholder; use the signed artifact intended for release.
Alignment checks do not replace native runtime verification.

## Maintainer runtime checks

Run the Android suite as described in [Testing](TESTING.md), then check:

- Download progress, cancellation, retry, and continued access to an older
  installed source after an unsuccessful update.
- With full release dictionaries installed and airplane mode enabled,
  searches in each supported language and a database-miss pronunciation
  fallback.
- Settings → About: the project license, warranty and redistribution notice,
  dictionary attribution, and eSpeak source/license links are visible.
- Native loading and pronunciation generation in an Android environment
  using 16 KB memory pages.

Record the tested app revision, dictionary release, and verification results
in the release notes. Before publishing, verify that About links resolve on
the published source branch. A passing fixture suite does not establish full
release-data behavior or page-size compatibility.

## Package matching source

Publish the exact app and pinned native source alongside the APK, including
build scripts, notices, and the language/phoneme sources used to generate
bundled data. GitHub's automatic app source archive omits submodule contents.
Keep the combined source archive available with the binary distribution.

From the same clean commit used to build the APK, use GNU tar and an empty
output directory:

```sh
mkdir -p build/release-source
git archive --format=tar --prefix=Ghostwriter/ --output=build/release-source/Ghostwriter-source.tar HEAD
git -C third_party/espeak-ng archive --format=tar --prefix=Ghostwriter/third_party/espeak-ng/ --output=../../build/release-source/espeak-source.tar HEAD
tar --concatenate --file=build/release-source/Ghostwriter-source.tar build/release-source/espeak-source.tar
gzip -n build/release-source/Ghostwriter-source.tar
```

Extract `Ghostwriter-source.tar.gz` to a fresh directory, configure the Android
SDK, and verify that it builds without relying on previous outputs:

```sh
./gradlew test lint assembleRelease assembleDebugAndroidTest --no-build-cache
```

Upload the verified source archive with the APK and identify the app tag or
commit and eSpeak commit in the release notes. Include
[THIRD_PARTY_LICENSES.md](../THIRD_PARTY_LICENSES.md) with the distribution.
That notice is the reference for software and dictionary-data licensing;
downloaded data retains its separate attribution and sharing terms.
