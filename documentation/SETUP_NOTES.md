# Build setup

## Requirements

- Android Studio compatible with the Android Gradle Plugin declared in
  [`gradle/libs.versions.toml`](../gradle/libs.versions.toml).
- A JDK supported by that plugin and the checked-in Gradle wrapper.
  Android Studio's bundled runtime is the usual starting point.
- Android SDK, NDK, and CMake versions declared in
  [`app/build.gradle.kts`](../app/build.gradle.kts).
- Git with submodule support for the native pronunciation engine.

Gradle's runtime JDK and the application's bytecode target are separate
settings. Use the checked-in wrapper; build configuration is the source of
truth for toolchain and SDK versions.

## Checkout and build

1. Clone the repository. Use `dev` for active development or the branch being
   reviewed.
2. Initialize the native source from the repository root:

   ```sh
   git submodule update --init third_party/espeak-ng
   ```

3. Open the repository root in Android Studio and let Gradle sync. Install
   the required SDK and native build components through SDK Manager.
4. Build the app from Android Studio or a terminal:

   ```sh
   ./gradlew assembleDebug
   ```

For terminal builds, configure the Android SDK through `local.properties`
(`sdk.dir`) or the environment. Keep machine-specific paths and that local
configuration out of version control.

To run the app, the maintainer selects an emulator or device meeting `minSdk`
and a packaged ABI from `app/build.gradle.kts`, then runs the `app`
configuration in Android Studio.

The language data is checked in, so ordinary builds do not require a host
eSpeak build. See [Native pronunciation engine](ESPEAK_NATIVE.md) when changing
that data, and [Testing](TESTING.md) for verification commands.
