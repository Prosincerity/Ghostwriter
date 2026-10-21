# Releasing

This guide prepares verified APKs and matching source for distribution.

## Prepare the revision

Complete the [testing merge gate](TESTING.md#coverage-and-merge-gate) for the
intended code. Update `versionCode` and `versionName` in
[app configuration](../app/build.gradle.kts), increasing the code for each build
that must update an installed version. Commit the version change on `dev`.

Use a clean, tagged commit for a public release.

Follow [Build setup](SETUP_NOTES.md) to configure the SDK and native submodule.
Check both working trees and the recorded submodule revision:

```sh
git status --short
git -C third_party/espeak-ng status --short
git submodule status
```

Both status commands must produce no output. The submodule status must start
with a space, indicating that the checkout matches the recorded revision.

## Build and sign

Use the [debug build procedure](SETUP_NOTES.md#build-and-run) for testers.
Debug-key signatures can update only installations signed with the same key.
For a release build, run from the repository root:

```sh
./gradlew --offline :app:lintRelease :app:assembleRelease
```

The release configuration has no signing key. It produces
`app/build/outputs/apk/release/app-release-unsigned.apk`.
Keep keys outside the repository. Use installed SDK build tools to align,
sign, and verify the release APK; the prompts accept local paths:

```sh
read -r -p 'SDK build-tools directory: ' build_tools
read -r -p 'Release keystore path: ' release_keystore
mkdir -p build/release
"$build_tools/zipalign" -P 16 -f 4 app/build/outputs/apk/release/app-release-unsigned.apk build/release/app-aligned.apk
"$build_tools/apksigner" sign --ks "$release_keystore" --out build/release/app-release.apk build/release/app-aligned.apk
"$build_tools/apksigner" verify --verbose build/release/app-release.apk
"$build_tools/zipalign" -c -P 16 -v 4 build/release/app-release.apk
```

Inspect the APK intended for distribution:

- Include native libraries for every ABI declared in app configuration.
- Include the language files listed in `EspeakIpa.DATA_FILES`.
- Include all project and third-party notices under `assets/licenses/`.
- Exclude downloaded SQLite dictionary databases.

ZIP alignment does not establish native runtime compatibility.

## Runtime checks

- Exercise download progress, cancellation, retry, and continued access to an
  older installed source after an unsuccessful update.
- Install full dictionaries and enable airplane mode. Search each supported
  language and exercise pronunciation fallback after a database miss.
- Measure search latency with release dictionaries, including phonetic suffix
  and broad double-rhyme searches. Record input, data release, device, and timing.
- Check **Settings → About** for licenses, warranty and redistribution notices,
  dictionary attribution, and source links. Verify links on the published branch.
- Verify native loading and IPA generation on Android with 16 KB memory pages.

Record the app revision, dictionary release, and results in release notes.
Fixture tests do not establish full release-data behavior or page-size support.

## Package matching source

Distribute the exact app and pinned native source with the APK, including build
scripts, notices, and language/phoneme sources used to generate bundled data.
GitHub's automatic app archive omits submodule contents.

From the same clean commit used for the APK, use GNU tar and an empty
`build/release-source` directory:

```sh
mkdir -p build/release-source
git archive --format=tar --prefix=Ghostwriter/ --output=build/release-source/Ghostwriter-source.tar HEAD
git -C third_party/espeak-ng archive --format=tar --prefix=Ghostwriter/third_party/espeak-ng/ HEAD > build/release-source/espeak-source.tar
tar --concatenate --file=build/release-source/Ghostwriter-source.tar build/release-source/espeak-source.tar
gzip -n build/release-source/Ghostwriter-source.tar
```

Extract `Ghostwriter-source.tar.gz` to a fresh directory and configure the SDK.
Cache dependencies first, then verify from that directory without prior outputs:

```sh
./gradlew --offline :app:testDebugUnitTest :app:lintRelease :app:assembleRelease :app:assembleDebugAndroidTest --no-build-cache
```

Upload the verified source archive and
[THIRD_PARTY_LICENSES.md](../THIRD_PARTY_LICENSES.md) alongside the APK.
Identify the app tag or commit and the eSpeak commit in release notes.
