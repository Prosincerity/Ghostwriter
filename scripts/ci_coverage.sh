#!/usr/bin/env bash
# Run from the repo root with an already booted, selected emulator.
set -euo pipefail

: "${COVERAGE_BASE_REF:?Set the PR base SHA or pre-push SHA}"
: "${ANDROID_SERIAL:?Select the booted emulator}"

task_baseline=$(mktemp -d)
git worktree add --detach "$task_baseline" "$COVERAGE_BASE_REF"
git -C "$task_baseline" submodule update --init third_party/espeak-ng

# Use identical reporting, exclusions, dependency versions and toolchain for
# both revisions; production code, tests and fixtures come from each revision.
cp app/build.gradle.kts "$task_baseline/app/build.gradle.kts"
cp build.gradle.kts settings.gradle.kts gradle.properties "$task_baseline/"
cp gradle/libs.versions.toml gradle/gradle-daemon-jvm.properties \
    gradle/coverage.gradle.kts gradle/coverage-policy.json "$task_baseline/gradle/"
cp gradlew "$task_baseline/gradlew"
cp gradle/wrapper/gradle-wrapper.* "$task_baseline/gradle/wrapper/"
mkdir -p "$task_baseline/scripts" build/coverage-baseline
cp scripts/coverage_policy.py "$task_baseline/scripts/"

# Dependency provisioning happens before the offline test commands.
(cd "$task_baseline" && ./gradlew :app:prepareOfflineTests assembleDebug assembleDebugAndroidTest --no-configuration-cache)
(cd "$task_baseline" && ./gradlew :app:jacocoDebugUnitReport :app:jacocoDebugAndroidReport \
    :app:jacocoDebugCombinedReport :app:createAggregatedCoverageReport \
    -x :app:verifyAggregatedCoverage --offline)
cp "$task_baseline/app/build/reports/jacoco/combined/coverage.xml" build/coverage-baseline/coverage.xml
cp "$task_baseline/app/build/reports/aggregated_code_coverage_html_report/global/data/report-data.js" \
    build/coverage-baseline/report-data.js

./gradlew :app:jacocoDebugUnitReport :app:jacocoDebugAndroidReport \
    :app:jacocoDebugCombinedReport :app:createAggregatedCoverageReport --offline
python3 scripts/report_slow_tests.py app/build/test-results
python3 scripts/coverage_policy.py \
    --report app=app/build/reports/jacoco/combined/coverage.xml \
    --baseline app=build/coverage-baseline/coverage.xml \
    --aggregated-report app/build/reports/aggregated_code_coverage_html_report/global/data/report-data.js \
    --aggregated-baseline build/coverage-baseline/report-data.js \
    --base-ref "$COVERAGE_BASE_REF"
