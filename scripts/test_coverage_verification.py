#!/usr/bin/env python3
"""Exercise the Gradle coverage gate with synthetic reports; no device needed."""

import json
from pathlib import Path
import subprocess
import tempfile


ROOT = Path(__file__).resolve().parents[1]


def variant(instruction, branch, name="debug"):
    # Deliberately misleading display percentages: use exact counters.
    return {
        "name": name,
        "instruction": {"covered": instruction[0], "total": instruction[1], "percent": 100},
        "branch": {"covered": branch[0], "total": branch[1], "percent": 100},
    }


def source(instruction, branch, name="Example.kt", extra_variants=()):
    return {
        "name": "ExampleClass",
        "sourceFileName": name,
        "testSuiteCoverages": [{
            "name": "Aggregated",
            "variantCoverages": [variant(instruction, branch), *extra_variants],
        }],
    }


def module(*sources, name=":app", package="example"):
    return {"name": name, "packages": [{"name": package, "classes": list(sources)}]}


def report(*sources):
    return {"modules": [module(*sources)]}


def main():
    cases = [
        ("inclusive minimums", report(source((90, 100), (80, 100))), None),
        ("instruction below minimum", report(source((8999, 10000), (80, 100))),
         "instruction coverage 89.99% is below 90%"),
        ("branch below minimum", report(source((90, 100), (7999, 10000))),
         "branch coverage 79.99% is below 80%"),
        ("both below minimum", report(source((89, 100), (79, 100))),
         ["instruction coverage 89.00% is below 90%", "branch coverage 79.00% is below 80%"]),
        ("global totals cannot hide failing file",
         report(source((99, 100), (99, 100), "Good.kt"), source((0, 10), (0, 10), "Bad.kt")),
         ":app/example/Bad.kt [debug]: instruction coverage 0.00% is below 90%"),
        ("classes in one file use weighted counters",
         report(source((99, 100), (99, 100)), source((0, 10), (0, 10))), None),
        ("same filename in separate modules stays separate", {"modules": [
            module(source((99, 100), (99, 100))),
            module(source((0, 10), (0, 10)), name=":library"),
        ]}, ":library/example/Example.kt [debug]: instruction coverage 0.00% is below 90%"),
        ("same filename in separate packages stays separate", {"modules": [
            {"name": ":app", "packages": [
                module(source((99, 100), (99, 100)))["packages"][0],
                module(source((0, 10), (0, 10)), package="other")["packages"][0],
            ]},
        ]}, ":app/other/Example.kt [debug]: instruction coverage 0.00% is below 90%"),
        ("variants cannot compensate for each other",
         report(source((99, 100), (99, 100), extra_variants=[variant((0, 10), (0, 10), "release")])),
         "Example.kt [release]: instruction coverage 0.00% is below 90%"),
        ("branchless file", report(source((90, 100), (0, 0))), None),
        ("file without executable code", report(source((0, 0), (0, 0))), None),
        ("uncovered executable file", report(source((0, 10), (0, 0))),
         "instruction coverage 0.00% is below 90%"),
        ("invalid counters", report(source((101, 100), (80, 100))),
         "Invalid instruction coverage counters"),
        ("empty report", {"modules": []}, "Aggregated coverage report has no modules"),
        ("no source files", report(), "Aggregated coverage report has no source files"),
        ("malformed report", "invalid", "Unrecognized aggregated coverage report format"),
    ]
    with tempfile.TemporaryDirectory(prefix="ghostwriter-coverage-") as temporary:
        directory = Path(temporary)
        fixture = directory / "report-data.js"
        init = directory / "fixture.gradle"
        init.write_text("""
gradle.beforeProject { project ->
    if (project.path == ':app') {
        project.afterEvaluate {
            project.tasks.named('verifyAggregatedCoverage') {
                coverageReport.set(new File(System.getProperty('ghostwriter.coverageFixture')))
            }
        }
    }
}
""")
        for name, fixture_report, error in cases:
            fixture.write_text(
                "const fullReport = " + json.dumps(fixture_report) + ";"
                if isinstance(fixture_report, dict) else fixture_report
            )
            result = subprocess.run(
                [str(ROOT / "gradlew"), ":app:verifyAggregatedCoverage", "--no-configuration-cache",
                 "--console=plain", "--init-script", str(init),
                 f"-Dghostwriter.coverageFixture={fixture}"],
                cwd=ROOT, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
            )
            errors = [error] if isinstance(error, str) else error
            if (errors is None and result.returncode != 0) or (
                errors is not None and (result.returncode == 0 or any(e not in result.stdout for e in errors))
            ):
                raise AssertionError(f"{name}: unexpected result\n{result.stdout}")
            print(f"PASS: {name}", flush=True)


if __name__ == "__main__":
    main()
