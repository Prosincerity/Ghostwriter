# Release verification and corresponding source

Build releases from a clean, tagged commit after merging the branch. Initialize
the recorded native source before building:

```sh
git submodule update --init third_party/espeak-ng
git diff --exit-code
git diff --cached --exit-code
git -C third_party/espeak-ng diff --exit-code
git -C third_party/espeak-ng diff --cached --exit-code
git submodule status
./gradlew test lint assembleRelease assembleDebugAndroidTest
```

The submodule status must start with a space (the checked-out revision matches
the recorded revision), and its working tree must also be clean. The eSpeak
revision, NDK/CMake versions, and language-data generation instructions are in
[ESPEAK_NATIVE.md](ESPEAK_NATIVE.md). The release build currently produces an
unsigned APK; sign the verified build using the maintainer's release key.

The maintainer runs `connectedDebugAndroidTest` on an emulator or device.
With dictionaries installed, enable airplane mode and verify dictionary
searches and out-of-vocabulary eSpeak fallback in English, German, and Turkish.
Scroll through Settings → About and verify the project license, warranty and
redistribution notice, dictionary attribution, and eSpeak source/license links.
Confirm native loading on a 16 KB page-size device or emulator before claiming
that device compatibility. Record results in
[RHYME_DETECTION_PLAN.md](RHYME_DETECTION_PLAN.md).

Inspect the APK for both native ABIs, the ten reduced eSpeak data files, and
the complete notices under `assets/licenses/`. Downloaded SQLite dictionaries
must remain outside the APK. Their CC BY-SA 4.0 terms remain separate from the
app's GPL-3.0-or-later terms; preserve their attribution/modification notice
when redistributing them.

## Publish matching source alongside the APK

Keep source available with the distributed APK as described by GPL section
6(d). Publish the exact app source and the complete pinned eSpeak source,
including build scripts, notices, and language/phoneme sources used to generate
the packaged data. GitHub's automatic app source archive does not include the
submodule's files. From the same clean commit used to build the APK, make a
combined source archive with GNU tar:

```sh
mkdir -p build/release-source
git archive --format=tar --prefix=Ghostwriter/ --output=build/release-source/Ghostwriter-source.tar HEAD
git -C third_party/espeak-ng archive --format=tar --prefix=Ghostwriter/third_party/espeak-ng/ --output=../../build/release-source/espeak-source.tar HEAD
tar --concatenate --file=build/release-source/Ghostwriter-source.tar build/release-source/espeak-source.tar
gzip -n build/release-source/Ghostwriter-source.tar
```

Use an empty output directory for each release. Upload
`Ghostwriter-source.tar.gz` alongside the APK and identify the app commit/tag
and eSpeak commit in the release notes. Include
[THIRD_PARTY_LICENSES.md](../THIRD_PARTY_LICENSES.md) with the distribution.
Extract the combined archive to a fresh directory, configure the Android SDK,
and repeat the build commands to verify that the source is sufficient.

After merging to `main`, verify that the About screen's project license and
third-party-notice links resolve on GitHub. Do this before publishing the APK.
