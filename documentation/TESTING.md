# Testing

## Testing policy

Coverage is a safety net, not the goal. Write tests that protect behavior and
observable outcomes; never add tests solely to reach a coverage number.

### Workflow and assertions

- **Bug fixes:** write a failing regression test first, run it, and confirm
  that it fails for the intended bug rather than a setup or compilation error.
  Then fix the production code and confirm the test passes. Run the relevant
  suite to check for regressions.
- **New logic:** write tests alongside the code, using test-first development
  where the design is clear. Keep one behavior per test; multiple assertions
  are appropriate when they describe the same outcome.
- Assert public behavior and results. Do not assert on private methods or
  internal call order unless the order itself is the contract.
- Every test must contain meaningful assertions. Tests that only execute code
  to increase coverage are forbidden. Merely checking that code does not throw
  is insufficient unless not throwing is the specified behavior.
- Cover happy paths, applicable boundaries (empty, null, maximum values,
  off-by-one), errors, and invalid input. Branch coverage concerns decisions:
  exercise both outcomes of each decision.
- No `@Ignore` or disabled tests without a written reason and a linked tracked
  issue describing the work required to restore them.

### Test layers and isolation

Follow the test pyramid: many fast JVM unit tests, fewer integration tests,
and very few end-to-end tests. Keep Activities, Fragments, Composables, and DI
glue thin. Move logic into ViewModels or use cases so it can be unit-tested,
following the repository's existing architecture and dependency constraints.
Do not force UI or wiring into unit-test coverage; test UI behavior through
UI tests or leave those layers thin.

Tests must be deterministic and independent. Do not share mutable state,
rely on test order, or depend on real time, network access, randomness, or
existing filesystem state. Use controlled clocks and coroutine dispatchers,
fixed inputs, and fakes. Tests of storage behavior may use isolated temporary
files initialized and cleaned up by the test. Synchronize explicitly with
workers and UI idling; never fix a flaky test by adding retries or sleeps.
Report flaky tests and find and remove the nondeterminism.

Keep the unit suite fast. Report any test taking more than a few seconds with
its name and measured duration. Keep fixtures next to the tests that use them;
parser tests should use minimal committed sample files. Do not put secrets,
real user data, or production endpoints in tests.

### Coverage requirements

Use JaCoCo to report **instruction and branch coverage**. Gate at module level,
not per file, using weighted covered/total counters rather than averaging
file percentages. The default baseline is approximately **80% instruction /
70% branch**, enforced as those minimums. Logic-heavy modules (domain, data,
parsing) use **90% instruction / 85% branch**. Apply stricter gates to actual
modules; this does not require splitting the current app into new modules.
Metrics with no executable instructions or branches are not applicable.

New or changed executable lines must have at least **80% diff coverage**.
Report unit and instrumented coverage separately and retain the
aggregated coverage dashboard. Enforce fixed module minimums on
combined coverage from both suites, keeping each module and variant separate.
Use the separate reports to assess each suite's responsibilities; Android UI
does not have to meet the unit-test threshold. Changed-line coverage uses the
combined JaCoCo XML report.

Exclude non-logic code from policy reports: generated Hilt/Dagger, Room, Data Binding,
`BuildConfig`, and `R` code; Compose previews; plain data classes; and trivial
DI modules. Exclusions must be narrow and must not hide handwritten logic,
custom data-class behavior, or logic embedded in UI layers.

The shared exclusion list is in `gradle/coverage-policy.json`. Current exclusions
cover Android resource/build classes, the dedicated Home previews, and reviewed
plain data-holder classes. Data classes with custom behavior, such as
`ProjectSummary`, `SystemFontFile`, and `ProjectMetadata`, remain included.
There are currently no Hilt/Dagger, Room, Data Binding, or DI modules; add narrow
generated-code exclusions if those tools are introduced with approval, rather
than blanket patterns that could hide handwritten implementations. The AGP
dashboard shows its raw data; the verifier applies the same exclusions before
summing its module counters, and the filtered JaCoCo HTML/XML reports omit them.

If a coverage gate fails, add meaningful tests. Do not lower
thresholds or add exclusions without explicitly documenting the justification
in the change. This policy replaces the former blanket per-file requirement:
module gates and focused tests protect logic without demanding artificial
unit coverage of Android UI and generated wiring.

### Local execution

Tests must run headless and offline. Verification runs locally; there are no
GitHub Actions workflows. Install SDK components, dependencies, and any
emulator images before offline verification, then pass `--offline` to Gradle
once dependencies are cached. Test execution must not require downloads or
production services. Retain test and coverage reports for review, including
any flakiness or slow tests. See the commands below for local verification.

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

In this Distrobox environment, agents must confirm the target with
`~/Android/Sdk/platform-tools/adb devices -l` using elevated execution
permission and select it with `ANDROID_SERIAL`. Every ADB command and any
tool invoking ADB, including connected Gradle tasks, must use that SDK path
and elevated execution permission as required by [AGENTS.md](../AGENTS.md).

For a sample test on one device, use the serial from that command:

```sh
ANDROID_SERIAL='<device serial>' ./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.prosincerity.ghostwriter.ui.screens.HomeScreenTest
```

Wireless debugging must already be paired and connected. Outside this
Distrobox agent environment, use the ADB binary from the SDK configured in
`local.properties`. Access to the host ADB server and Gradle caches may require
running the command outside the sandbox.

Tests use AndroidJUnitRunner, Espresso, and Compose UI testing and can run
without Google Play Services. Reports appear under
`app/build/reports/androidTests/connected/`. For a filtered run, confirm the
expected classes appear in the generated XML before accepting the result.

## Existing test responsibilities

- **JVM tests** (`app/src/test/`) cover project storage, UUID migration, binary
  waveform caches, checksummed persistent snapshots, save failures, metadata, playback
  state, waveform calculations, input normalization, IPA keys, search-stage
  ordering, filtering, and pagination. Dictionary search tests exercise the
  production loop through an offline row adapter. Archive tests cover gzip
  output, progress, failures, and cancellation.
- **Android tests** (`app/src/androidTest/`) cover shared-folder lyric recovery
  through an isolated document provider, including simulated removal of local
  working copies, failed writes, corrupted saves, and newer local drafts. They
  also cover SQLite installation and
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
| Filtered JVM | `./gradlew :app:jacocoDebugUnitReport` | `app/build/reports/jacoco/unit/html/index.html` |
| Filtered Android | `./gradlew :app:jacocoDebugAndroidReport` | `app/build/reports/jacoco/android/html/index.html` |
| Filtered combined | `./gradlew :app:jacocoDebugCombinedReport` | `app/build/reports/jacoco/combined/html/index.html` |

Android, combined, and aggregated coverage require a running emulator or connected device
and follow the same device-testing rules. The combined and aggregated tasks
run both suites.

### Gates and changed-line coverage

The filtered reports also produce `coverage.xml` next to their `html/` directory.
`gradle/coverage.gradle.kts` configures these reports and the verification tasks.
`:app:createAggregatedCoverageReport` automatically runs
`:app:verifyAggregatedCoverage` afterward, checking weighted instruction and
branch totals per module and variant with the policy exclusions. A failing file
does not independently fail the gate. Dashboard percentages are rounded, so
verification uses the exact covered/total counters.

Run `./gradlew :app:verifyDebugCoverage` on a selected connected target to run
both suites, generate separate, combined, and aggregated reports, and check
module minimums. Run `./gradlew :app:verifyAggregatedCoverage` to check an
existing dashboard without rerunning tests; saved results are not fresh coverage.
The app uses 80% instruction / 70% branch. Modules named `domain`, `data`,
`parsing`, or `logic` automatically use 90% / 85%; other logic-heavy modules
must be passed to the verifier with `--logic-module MODULE`.

The coverage checker verifies fixed module minimums in both combined XML and
the aggregated dashboard. With `--base-ref`, it also checks at least 80%
coverage of changed executable production Kotlin/Java lines in the combined
XML. This JVM line metric does not apply to documentation, Gradle scripts, or
Python tooling; the tooling has its own behavior tests.

To check fresh local reports and changed lines against a chosen revision:

```sh
python3 scripts/coverage_policy.py \
  --report app=app/build/reports/jacoco/combined/coverage.xml \
  --aggregated-report app/build/reports/aggregated_code_coverage_html_report/global/data/report-data.js \
  --base-ref '<base commit>'
```

The verifier fails on missing or malformed reports. Omit `--base-ref` to check
only fixed module minimums. Changed-line checks use the Git diff and current
combined XML report; previous coverage reports are not required.
The checker tests run headless and offline
with `python3 -m unittest discover -s scripts -p 'test_*.py'`.
`python3 scripts/report_slow_tests.py app/build/test-results` reports individual
unit tests taking more than three seconds.

When removing redundant UI tests, keep a surviving test that checks the same
behavior and retain unique assertions. `MainActivityTest` covers About navigation
and the open/rename/delete project workflow; screen tests retain attribution,
empty-home behavior, and project ordering. The notepad placeholder, default
arguments, and hoisted editing assertions share the notepad typography test.
Manual-save persistence and silent haptics share the editor metadata/exit-save
test, which also checks that edits made after a manual save survive exit.
External loop-mode updates are
checked across every mode by the playback layout test, and the haptic test checks
the complete button-driven cycle.

The behavior-focused review also consolidates player defaults, unprepared
transport actions, and loop-state callbacks. Each unprepared action gets a
fresh player. PCM preparation cancellation is checked before and during
memory loading in `PcmSourceTest`; disk cancellation remains a separate case.
`WaveformCacheTest` owns malformed-cache and invalid-write checks, including
preserving the last good waveform after each rejected replacement. Storage
tests retain cache reuse, resolution, and invalidation integration checks.
PCM amplitude boundaries go through the format-dispatching decoder, and the
stall-guard test checks both reset and rejection. Real corrupt-audio decoding
is checked on Android rather than against JVM framework stubs.

Marker normalization and unchanged-marker checks assert preserved values
rather than reference reuse; dragging and editing regressions remain covered
by UI tests. Selection callbacks still identify the original marker so equal
copies cannot edit the wrong marker. Notification tests assert
published updates and cancellation rather than the fake scheduler's queue.
Exact typography-size snapshots and constant-only settings assertions are
removed; bundled fonts, rendered typography, and selectable autosave defaults
remain checked. Home folder-selection and slider-reconfiguration scenarios
retain their unique callbacks and state assertions in consolidated tests.
Separate callback-replacement and zoom-limit regressions remain independent.
Theme-only recompositions without visual assertions and playback clicks
without outcome assertions are removed. Explicit contracts such as deferred
playback binding and allocation-free audio rendering still have dedicated tests.

Further storage consolidation keeps beat cache/marker invalidation with the
replacement and removal scenarios, manual-save replacement with autosave
synchronization, and backup overflow with rotation. Missing-newest recovery
checks both an absent manual save and an older manual save. Marker parsing
retains every malformed-entry case in one ordered-result test; optional JSON
null fields are checked in the saved file alongside their loaded values.
Notepad defaults are checked through the rendered text style instead of a
data-class constructor snapshot. Saved font-style checks inspect the loaded
Android typeface rather than unchanged fixture properties. Player input
validation retains missing-file, directory, valid-volume, and NaN cases with
fresh players for each invalid load. No UI code is added to the JVM suite.

PCM buffer tests control the worker's prefetch passes with semaphores and join
the worker before checking cleanup, avoiding wall-clock polling and repeated
copy attempts. They assert the returned samples, nonblocking page misses,
live edits, and failed-file cleanup. Large-source selection checks retained
backing files; sample correctness belongs to the buffer tests. Metadata saves
use an injected clock for exact edit-time assertions. Download and editor
fixtures remain blocked until the test releases them in `finally`; elapsed
time cannot accidentally advance those scenarios. Synchronization timeouts
are failure guards, not inputs to the expected result.
HTTP tests require finite timeouts without pinning tuning constants. Ledger
overflow and decoder-stall tests assert rejection and preserved behavior
rather than internal diagnostic wording. Cancellation assertions surround
the operation itself so fixture failures cannot satisfy the expected error.

After removing redundant tests, run the affected suites and review fresh
reports to confirm the surviving tests still protect the intended behavior
and satisfy the fixed module minimums and changed-line coverage requirement.

If Gradle cannot see the emulator, check `adb devices` or launch the task from
Android Studio's Gradle tool window. Failed runs do not produce complete
combined reports. Generated reports stay in `app/build/` and can be recreated.
