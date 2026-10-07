#!/usr/bin/env bash
# Run from the repo root with an already booted, selected emulator.
set -euo pipefail

: "${COVERAGE_BASE_REF:?Set the revision for changed-line coverage}"
: "${ANDROID_SERIAL:?Select the booted emulator}"

./gradlew :app:jacocoDebugUnitReport :app:jacocoDebugAndroidReport \
    :app:jacocoDebugCombinedReport :app:createAggregatedCoverageReport --offline
python3 scripts/report_slow_tests.py app/build/test-results
python3 scripts/coverage_policy.py \
    --report app=app/build/reports/jacoco/combined/coverage.xml \
    --aggregated-report app/build/reports/aggregated_code_coverage_html_report/global/data/report-data.js \
    --base-ref "$COVERAGE_BASE_REF"
