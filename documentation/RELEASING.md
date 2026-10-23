# Create a release

Run these commands from the repository root on `dev`. Release builds use one
registered signing key, R8 code/resource optimization, and a universal APK for
Android 7.0/API 24 or newer on 32-bit/64-bit ARM and x86 devices.

## Set up the build machine

Install Git, Python 3.10 or newer, and Android Studio. Follow
[Build setup](SETUP_NOTES.md) for the JDK, Android SDK, NDK, CMake, and
`local.properties`. Put that JDK's `java` and `keytool` on PATH. No Python packages
are needed. On Windows, replace `python3` with `py -3` and `./gradlew` with
`gradlew.bat` in the commands below.

For a new checkout:

```sh
git clone --recurse-submodules --branch dev https://github.com/Prosincerity/Ghostwriter.git
cd Ghostwriter
```

For an existing checkout, run `git submodule update --init third_party/espeak-ng`.
Use SDK Manager to install the components declared by this checkout. The first
build needs network access to cache dependencies; later builds work offline.
Run setup and release commands in the same configured environment.

## Create the signing key once

Run in your own terminal:

```sh
python3 scripts/setup_signing.py
```

Choose and confirm a strong, unique password of at least 16 characters. Input is
hidden. Store the password in your password manager; the script never saves it.

Setup creates:

- `~/.ghostwriter/signing/release.p12`: private RSA-3072 signing key in a PKCS#12
  store protected with AES-256, PBKDF2-HMAC-SHA256, and 600,000 iterations.
- `release.local.json`: ignored local configuration containing the key's path,
  fixed alias, certificate fingerprint, and release output directory. No password.

POSIX key files are restricted to their owner. Keep the signing folder private
on other systems too. The setup command refuses to overwrite a key or existing
configuration. Do not generate another key for later releases.

Back up the key and `release.local.json` privately, and keep the password in your
password manager. On another computer, restore both files and adjust only the
paths in the local configuration. Keep its alias and fingerprint unchanged.
An optional setup `--keystore /private/path/release.p12` chooses another private
location outside the repository. There is no key-import or fallback signing path.

A newly generated key starts a new signing identity. Installations signed with a
different key cannot update directly to it. Before any planned reinstall, verify
shared lyric-folder recovery and separately preserve app-local beats and drafts;
see [Project storage](README.md#shared-lyric-folder). Future releases signed with
this registered key can update each other in place.

## Build each release

1. Update `versionName` and increase `versionCode` in `app/build.gradle.kts`.
   The code must be higher than the last packaged release, even if the visible
   name stays the same. For example, the next revision after code `6` can use:

   ```kotlin
   versionCode = 7
   versionName = "1.3.0-beta.3"
   ```

2. Test and commit the revision on `dev`. The application and native submodule
   must be clean. For a version-only change, after the required checks:

   ```sh
   ./gradlew --offline :app:testDebugUnitTest :app:lintRelease :app:assembleRelease
   git add app/build.gradle.kts
   git commit -m "chore: prepare 1.3.0-beta.3 release"
   ```

   Omit `--offline` from this check if dependencies are not cached yet. Feature
   changes also need the focused checks in [Testing](TESTING.md).

3. Build the release and enter the signing password once:

   ```sh
   python3 scripts/build_release.py
   ```

For the first build on a machine, or after changing dependencies, allow downloads:

```sh
python3 scripts/build_release.py --online
```

The first release is detected automatically. Later builds find the previous APK
through `dist/releases/channel.json` and verify its checksum, signing certificate,
version, Android requirements, and CPU support. No previous-APK path, signing-key
arguments, or manual configuration edits are needed for ordinary releases.
The command refuses a changed certificate, reused version code, missing history,
missing previous APK, or existing output directory. Restore backups rather than
resetting history to bypass these checks.

## Find the finished ZIP

The command prints the exact path when every check succeeds. For the example
version above:

```text
dist/releases/Ghostwriter-1.3.0-beta.3-vc7/
  Ghostwriter-1.3.0-beta.3-vc7.zip
  Ghostwriter-1.3.0-beta.3-vc7.zip.sha256
  mapping.txt
  ... unpacked copies of the ZIP contents
```

Distribute the `.zip`, optionally with its `.zip.sha256` checksum. It contains
one signed APK, the matching app AND pinned eSpeak source ZIP, `INSTALL.txt`,
`RELEASE.json`, license notices, and `SHA256SUMS`. It contains no private signing
files, passwords, local configuration, or crash mapping.

Retain `dist/releases/`, including `channel.json`, previous APKs, and `mapping.txt`.
The mapping is needed to interpret R8 crash traces. Back up this release history
alongside your private signing backups. Output paths can be relocated through
`outputDir` in `release.local.json` if the history is moved with them.

Recipients extract the ZIP, open the APK, allow installation from their trusted
file manager when prompted, and choose **Install** or **Update**. Updates from
the same signing identity retain app data. Device management or sideloading
restrictions can still prevent installation. A signing conflict requires checking
the installed certificate; uninstalling removes app-local data.

## Device checks

To include four optimized native pronunciation checks while creating a release:

```sh
python3 scripts/build_release.py --device DEVICE_SERIAL
```

To run those checks alone without packaging or changing release history:

```sh
python3 scripts/build_release.py --verify-native DEVICE_SERIAL
```

Both commands prompt for the same release password. They verify the registered
certificate and use that key for an isolated `.releasecheck` application and its
test APK. The native check disables Gradle configuration caching so its signing
password is not retained there. Follow [Device access](TESTING.md#device-access)
for selecting an already connected device and execution permissions.

The report guard rejects crashes, skipped cases, missing cases, and zero-test
runs. Ordinary release APKs retain the normal application ID; the native bridge
names are protected by `app/src/main/keepRules/rules.keep`.

Before a public release, complete the [testing merge gate](TESTING.md#coverage-and-merge-gate),
tag the clean revision, and exercise download/cancellation/retry, offline search,
project creation and recovery, playback, and About notices. Record revision,
device, and results. Runtime behavior on a device with 16 KB memory pages needs
its own check; static alignment checks alone do not prove all runtime behavior.

## What the release command verifies

The command runs Python tooling tests, the full JVM suite, release lint, and the
optimized build. It validates the actual APK's ID, non-debuggable flag, version,
all four ABIs, retained JNI descriptors and native symbol names, required engine
data and licenses, and absence of downloaded dictionary databases. It checks
64-bit ELF alignment, aligns the APK for 16 KB pages, signs it with the registered
key, and verifies its signature and alignment.

Source archives come from the exact clean app/native commits, preserve executable
permissions, and reject tracked signing material. All package contents have
checksums. Nothing is uploaded or published by the command.

For private automation, `GHOSTWRITER_STORE_PASSWORD` can supply the single password
through the process environment. Never put it in command-line arguments, local
JSON, tracked files, or build logs. An optional `--config PATH` selects a restored
configuration; the registered certificate is still required and checked.
