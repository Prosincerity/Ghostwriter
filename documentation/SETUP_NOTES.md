# Build setup

The repository contains the complete Android project, including the Gradle
wrapper and application manifest. No Android Studio project wizard or file
copying is required.

## Requirements

- Android Studio compatible with the checked-in Android Gradle Plugin
- Android SDK 37
- A JDK supported by the checked-in Gradle and Android Gradle Plugin versions
  (Android Studio's bundled runtime is recommended)

The application source targets Java 11 bytecode; that is separate from the JDK
version used to run Gradle.

## Open and build

1. Clone the repository and check out `dev` for active development.
2. Open the repository root in Android Studio and allow Gradle to sync.
3. Select an emulator or device running API 24 or newer.
4. Run the `app` configuration, or build from a terminal with:

```bash
./gradlew assembleDebug
```

See [TESTING.md](TESTING.md) for verification and coverage commands.
