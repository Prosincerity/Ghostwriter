# Testing

## Checks without a device

Run from the repository root:

```sh
./gradlew test lint assembleDebugAndroidTest
```

This runs JVM tests and lint, then builds the instrumented test APK without
running it. JVM reports are under `app/build/reports/tests/`; lint reports are
under `app/build/reports/`. Use the generated reports for current results
rather than maintaining test or warning totals in documentation.

## Android tests

Instrumented tests can run from Android Studio or the terminal on a connected
emulator or device. Agents may run relevant tests on an already connected target,
including devices connected over wireless debugging, as allowed by
[AGENTS.md](../AGENTS.md). If no device is available, compile the tests and leave
execution to the maintainer.

To run the full suite:

```sh
./gradlew connectedDebugAndroidTest
```

For a sample test on one device, use the serial shown by `adb devices -l`:

```sh
ANDROID_SERIAL='<device serial>' ./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.prosincerity.ghostwriter.ExampleInstrumentedTest
```

Wireless debugging must already be paired and connected. Use the `adb` binary
from the SDK configured in `local.properties` if it is not on your `PATH`.
In a sandboxed agent session, access to the host ADB server and Gradle caches
may require running the command outside the sandbox.

Tests use AndroidJUnitRunner, Espresso, and Compose UI testing and can run
without Google Play Services. Reports appear under
`app/build/reports/androidTests/connected/`. For a filtered run, confirm the
expected classes appear in the generated XML before accepting the result.

## Coverage and regression guidance

- **JVM tests** (`app/src/test/`) cover project storage, metadata, playback
  state, waveform calculations, input normalization, IPA keys, search-stage
  ordering, filtering, and pagination. Dictionary search tests exercise the
  production loop through an offline row adapter. Archive tests cover gzip
  output, progress, failures, and cancellation.
- **Android tests** (`app/src/androidTest/`) cover SQLite installation and
  lookup, staging cleanup, real native IPA, playback, and Compose screens.
  Dictionary UI tests cover input changes, stale errors, and superseded
  searches. An integration test exercises real JNI after a database miss.

Use small, deterministic fixtures for matching and ordering regressions.
Keep pure logic testable on the JVM, and reserve Android tests for framework,
native, and UI behavior. Test coroutine ordering with controlled dispatchers
and explicit synchronization rather than timing sleeps. Compose test
resumptions must respect the UI thread.

The maintainer has confirmed the Android suite passes with airplane mode
enabled. This includes SQLite fixtures, native IPA checks for supported
languages, the fallback integration, and About notices. It does not replace
full release-dictionary smoke tests, a timed search benchmark, or native
page-size compatibility checks. Repeat the [release checks](RELEASING.md)
for each distributed build.

## Coverage reports

Debug builds enable JaCoCo for JVM and instrumented tests.

| Report | Command | HTML output |
| --- | --- | --- |
| JVM | `./gradlew :app:createDebugUnitTestCoverageReport` | `app/build/reports/coverage/test/debug/index.html` |
| Android | `./gradlew :app:createDebugAndroidTestCoverageReport` | `app/build/reports/coverage/androidTest/debug/connected/index.html` |
| Combined | `./gradlew :app:createCoverageReport` | `app/build/reports/code_coverage_html_report/global/index.html` |

Android and combined coverage require a running emulator or connected device
and follow the same device-testing rules. The combined task reruns both suites.
No minimum coverage threshold is enforced.

If Gradle cannot see the emulator, check `adb devices` or launch the task from
Android Studio's Gradle tool window. Failed runs do not produce complete
combined reports. Generated reports stay in `app/build/` and can be recreated.
