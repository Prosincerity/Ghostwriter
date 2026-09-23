# Testing and coverage

Ghostwriter has JUnit 4 tests in `app/src/test/` and Android instrumented
tests in `app/src/androidTest/` using AndroidJUnitRunner, Espresso, and
Compose UI testing. Instrumented tests run on an AOSP device or emulator
without Google Play Services. Test totals change as coverage grows.

## Regular checks

From the repository root:

```bash
./gradlew test lint assembleDebugAndroidTest
```

Local reports appear under `app/build/reports/tests/`. Connected test reports
appear under `app/build/reports/androidTests/connected/`. Run the connected
suite from Android Studio or with `./gradlew connectedDebugAndroidTest` when a
device is available.

## Coverage

Debug builds enable JaCoCo for local and instrumented tests:

| Report | Gradle task | HTML output |
| --- | --- | --- |
| Local tests | `./gradlew :app:createDebugUnitTestCoverageReport` | `app/build/reports/coverage/test/debug/index.html` |
| Instrumented tests | `./gradlew :app:createDebugAndroidTestCoverageReport` | `app/build/reports/coverage/androidTest/debug/connected/index.html` |
| Combined | `./gradlew :app:createCoverageReport` | `app/build/reports/code_coverage_html_report/global/index.html` |

The instrumented and combined reports require a connected device or running
emulator. The combined task reruns both suites with coverage enabled. Android
Gradle Plugin may warn that `reportAggregationSupport` is experimental.
No minimum coverage threshold is enforced.

If Gradle cannot see a running emulator, check `adb devices` or launch the
coverage task from Android Studio's Gradle tool window. A failed test run
does not produce a complete combined report. All generated reports live in
`app/build/` and can be recreated.
