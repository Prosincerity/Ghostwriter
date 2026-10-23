# Testing

This guide defines verification scope, test design, device access, and coverage gates.

## Verification scope

Run checks locally, headless, and offline after caching dependencies and SDK components.
The repository has no GitHub Actions test workflow.

| Change stage | Required checks |
| --- | --- |
| Documentation only | Review the diff, commands, identifiers, and links; do not run Gradle or device tests |
| Code step | Compile and run JVM tests for the touched class; use Android compilation for Android-only code |
| Logical code batch | Run the full JVM suite once |
| Android-bound change | Run one relevant device class or method for media, SAF, Compose UI, or permissions |
| Merge gate | Pass both full suites, combined and aggregated module gates, and changed-line coverage |

Do not rerun passing checks unless covered code changes. Reserve full Android
and coverage runs for the merge gate. Fix or report failures; never skip,
delete, or weaken a failing test to obtain a pass.

## Test design

- For bug fixes, write and run a regression test first. Confirm failure comes
  from the intended bug, then fix it and confirm the test passes.
- Write tests alongside new logic; use test-first development when the design
  is clear. Keep one behavior per test and assert observable outcomes.
- Test private details or call order only when they form part of the contract.
  A no-throw assertion is valid only when not throwing is the specified behavior.
- Cover happy paths, empty/null/maximum/off-by-one boundaries, errors, invalid
  input, and both outcomes of applicable decisions.
- Keep UI and wiring thin. Put logic in UI-free classes; use many JVM tests,
  fewer integration tests, and very few end-to-end tests.
- Use controlled clocks, dispatchers, fakes, and isolated temporary storage.
  Avoid shared mutable state, test-order dependencies, real time, network,
  randomness, and pre-existing filesystem state.
- Use idling, `waitUntil`, or explicit synchronization instead of sleeps.
  Remove the cause of flakiness; do not add retries. Report flaky tests.
- Require a written reason and linked issue for `@Ignore` or disabled tests.
- Keep minimal fixtures beside tests. Use committed samples for parsers;
  exclude secrets, real user data, and production endpoints.
- Report tests taking more than a few seconds, with names and measured durations.
  Retain test and coverage reports for review.

## Checks without a device

Run from the repository root. Focused example:

```sh
./gradlew --offline :app:testDebugUnitTest --tests '*ProjectStorageTest*'
```

Full JVM batch check and Android test compilation:

```sh
./gradlew --offline :app:testDebugUnitTest
./gradlew --offline :app:assembleDebugAndroidTest
```

Run static analysis when relevant:

```sh
./gradlew --offline :app:lintDebug
```

Tooling checks and slow-test reporting:

```sh
python3 -m unittest discover -s scripts -p 'test_*.py'
python3 scripts/report_slow_tests.py app/build/test-results
```

The tooling suite also checks feature package paths, dependency cycles, and the
waveform/player ownership boundaries defined in [AGENTS.md](../AGENTS.md).

JVM results are in `app/build/test-results/` and `app/build/reports/tests/`.
Lint reports are in `app/build/reports/`.

## Device access

In Distrobox, invoke ADB only through the SDK path below. Every ADB command and
every tool invoking ADB, including connected Gradle tasks, requires elevated
execution permission (`sandbox_permissions="require_escalated"`). Sandboxed
ADB can crash in this environment.

Confirm an already connected emulator or physical device, then select its serial:

```sh
~/Android/Sdk/platform-tools/adb devices -l
read -r -p 'Device serial: ' ANDROID_SERIAL
export ANDROID_SERIAL
```

Wireless debugging must already be paired and connected. Outside Distrobox,
use ADB from the SDK configured in `local.properties`. If no device is available,
compile Android tests and leave execution to the maintainer.

Focused example; append `#methodName` to the class value to select a method:

```sh
./gradlew --offline :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.prosincerity.ghostwriter.projects.ui.HomeScreenTest
```

Confirm the selected classes in `app/build/outputs/androidTest-results/connected/`.
Android HTML reports are in `app/build/reports/androidTests/connected/`.
The suite uses `AndroidJUnitRunner`, Espresso, and Compose UI testing.

## Coverage and merge gate

Debug builds enable JaCoCo for both suites. Module gates use weighted counters,
not averages of file percentages. Branchless modules have no branch gate.

| Scope | Instruction minimum | Branch minimum |
| --- | --- | --- |
| Default, including `app` | 80% | 70% |
| Logic-heavy modules | 90% | 85% |

Modules named `domain`, `data`, `parsing`, or `logic` use the stricter gates.
Pass other logic-heavy module names with `--logic-module` to the checker.
New or changed executable production Kotlin/Java lines require 80% diff coverage.

[Coverage configuration](../gradle/coverage.gradle.kts) defines the tasks below.
[Policy exclusions](../gradle/coverage-policy.json) cover plain data holders,
generated resource/build classes, and previews. Keep exclusions narrow; never
exclude handwritten logic. Justify threshold changes and new exclusions explicitly.

| Task | Report under `app/build/reports/` |
| --- | --- |
| `jacocoDebugUnitReport` | `jacoco/unit/html/index.html` |
| `jacocoDebugAndroidReport` | `jacoco/android/html/index.html` |
| `jacocoDebugCombinedReport` | `jacoco/combined/html/index.html` |
| `createAggregatedCoverageReport` | `aggregated_code_coverage_html_report/global/index.html` |

Each filtered report writes `coverage.xml` beside `html/`. The dashboard shows
unfiltered data; policy verification applies exclusions. Device-dependent report
tasks follow the device rules above and can run full suites. Do not use them per step.

On the selected device, run both suites and generate all gate reports:

```sh
./gradlew --offline :app:verifyDebugCoverage --console=plain -Dorg.gradle.logging.level=lifecycle
python3 scripts/coverage_policy.py \
  --report app=app/build/reports/jacoco/combined/coverage.xml \
  --aggregated-report app/build/reports/aggregated_code_coverage_html_report/global/data/report-data.js \
  --base-ref origin/main
```

The Gradle gate checks module minimums; the Python invocation also checks the
merge diff. Require both coverage success and Gradle's `BUILD SUCCESSFUL`.
Saved reports are evidence only for the code and execution data that produced them.
Fixture suites do not replace the [release runtime checks](RELEASING.md#runtime-checks).
