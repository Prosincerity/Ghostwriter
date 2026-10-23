# Build setup

This guide configures the Android toolchain and builds a debug APK.

## Requirements

| Requirement | Source of truth |
| --- | --- |
| Android Studio and Android Gradle Plugin compatibility | [Version catalog](../gradle/libs.versions.toml) |
| Gradle distribution | [Wrapper properties](../gradle/wrapper/gradle-wrapper.properties) |
| Gradle runtime JDK | [Daemon JVM criteria](../gradle/gradle-daemon-jvm.properties) |
| Android SDK, NDK, CMake, application ID, and packaged ABIs | [App configuration](../app/build.gradle.kts) |
| Git submodule source | [.gitmodules](../.gitmodules) |

Gradle's runtime JDK differs from the app's Java bytecode target.
Use the checked-in wrapper rather than a system Gradle installation.

## Configure the checkout

Work on `dev` and initialize the pinned native source from the repository root:

```sh
git switch dev
git submodule update --init third_party/espeak-ng
```

Open the repository in Android Studio. Install the declared SDK, NDK, and CMake
components through SDK Manager, then sync the project to cache dependencies.
Configure `sdk.dir` in `local.properties` for terminal builds.
Keep machine-specific configuration out of version control.

Bundled pronunciation data is checked in. Ordinary Android builds do not
require a host eSpeak build. See [Native pronunciation engine](ESPEAK_NATIVE.md)
when changing the engine or its data.

## Build and run

After caching dependencies and toolchain components:

```sh
./gradlew --offline :app:assembleDebug
```

The development APK is `app/build/outputs/apk/debug/app-debug.apk`.
Debug builds include coverage instrumentation and UI tooling.
Use Android Studio's `app` run configuration on a device matching the minimum
SDK and an ABI declared in the app configuration.

See [Testing](TESTING.md) for verification and device access instructions.
Follow [Releasing](RELEASING.md) before distributing a build.
That guide includes a repeatable signing and packaging command which produces
the final shareable ZIP and verifies compatibility with the previous APK.
