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
| Aggregated | `./gradlew :app:createAggregatedCoverageReport` | `app/build/reports/aggregated_code_coverage_html_report/global/index.html` |

Android, combined, and aggregated coverage require a running emulator or connected device
and follow the same device-testing rules. The combined and aggregated tasks
run both suites.

Every source file in the aggregated report must meet **90% instruction coverage**
and **80% branch coverage** for each reported build variant, combining JVM and
Android coverage. Counters for all classes generated from the same source file
are summed within that file; coverage from other files, modules, or variants
cannot compensate for a file below a minimum. A metric with zero instructions
or branches is not applicable. No source files are excluded from this check.
These permanent minimums are configured in `app/build.gradle.kts`.
`:app:createAggregatedCoverageReport` automatically runs
`:app:verifyAggregatedCoverage` afterward and fails if any file misses a minimum,
listing each failing file, variant, metric, and covered/total count.
The check uses exact covered/total counters, not the dashboard's rounded
percentages, and leaves the HTML report available when a minimum is missed.
Run `./gradlew :app:verifyAggregatedCoverage` to check an existing report without
rerunning tests; this checks saved results and does not establish fresh coverage.
The gate's boundary, per-file grouping, and invalid-report regression checks run with
`python3 scripts/test_coverage_verification.py` and do not require a device.

When removing redundant UI tests, keep a surviving test that checks the same
behavior and retain unique assertions. `MainActivityTest` covers About navigation
and the open/rename/delete project workflow; screen tests retain attribution,
empty-home behavior, and project ordering. Regenerate aggregated coverage after
test removal to confirm that both minimums still hold.

If Gradle cannot see the emulator, check `adb devices` or launch the task from
Android Studio's Gradle tool window. Failed runs do not produce complete
combined reports. Generated reports stay in `app/build/` and can be recreated.
