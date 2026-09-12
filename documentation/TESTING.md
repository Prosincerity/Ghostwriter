# Testing and coverage

Ghostwriter has local JVM tests and Android instrumented tests. Debug builds
have JaCoCo coverage enabled for both test types, and Gradle can combine their
results into one HTML report.

## Test stack

- Local tests use JUnit 4 and live in `app/src/test/`.
- Instrumented tests use `AndroidJUnitRunner`, Espresso, and Compose UI testing
  and live in `app/src/androidTest/`.
- Coverage uses the JaCoCo support built into the Android Gradle Plugin.
- Instrumented tests can run on an AOSP emulator; Google Play Services are not
  required.

## Regular verification

Run the local tests, lint, and compile the instrumented-test APK with:

```bash
./gradlew test lint assembleDebugAndroidTest
```

Local test result pages are written below:

```text
app/build/reports/tests/
```

After a connected instrumented-test run, its result pages are written below:

```text
app/build/reports/androidTests/connected/
```

## Coverage reports

### Unified report

Start an AVD and wait until Android Studio reports that it is online. Then run
this task from the project root:

```bash
./gradlew :app:createCoverageReport
```

This task runs the debug local tests and connected debug instrumented tests,
then combines both coverage data sets. Open the resulting report at:

```text
app/build/reports/code_coverage_html_report/index.html
```

Unified report aggregation is currently an experimental Android Gradle Plugin
feature. The warning printed by Gradle for
`android.experimental.reportAggregationSupport=true` is therefore expected.

### Local-test-only report

This report does not require an emulator:

```bash
./gradlew :app:createDebugUnitTestCoverageReport
```

Open:

```text
app/build/reports/coverage/test/debug/index.html
```

### Instrumented-test-only report

With an AVD or device connected, run:

```bash
./gradlew :app:createDebugAndroidTestCoverageReport
```

Open:

```text
app/build/reports/coverage/androidTest/debug/connected/index.html
```

## Running the unified report from Android Studio

1. Start the AVD from **Tools > Device Manager** and wait for it to finish
   booting.
2. Open **View > Tool Windows > Gradle**.
3. Use **Execute Gradle Task** in the Gradle tool window and enter
   `:app:createCoverageReport`. The integrated terminal can run the same
   command when it has access to the Android SDK and the AVD's ADB server.
4. Wait for both the local and connected tests to finish. A previous successful
   test run is not reused; the coverage task runs the tests again with coverage
   collection enabled.
5. In the Project tool window, switch from the **Android** view to the
   **Project** view and open
   `app/build/reports/code_coverage_html_report/index.html`. If Android Studio
   does not preview it, use **Open In > Browser** or open that file directly in
   a web browser.

The unified report lets you drill down from packages to classes and source
lines. Green lines were executed, red lines were missed, and yellow lines were
only partially covered. Compose and Kotlin compiler-generated code can add
noise, so source-level line and branch coverage are more useful than treating
the overall percentage as a target. No minimum coverage threshold is currently
enforced.

## ADB and container troubleshooting

The coverage task must run in an environment that can see the emulator. If an
AVD is visible to Android Studio but not inside Distrobox, run the Gradle task
through Android Studio's Gradle tool window or from a host terminal instead.
Confirm device visibility with:

```bash
adb devices
```

The emulator should appear with the state `device`. If the coverage task fails,
fix the failing test or device connection first; Gradle will not create a
complete unified report after a failed test run.

All generated test and coverage pages are under `app/build/`. They are ignored
by Git, can be regenerated at any time, and are removed by `./gradlew clean`.
