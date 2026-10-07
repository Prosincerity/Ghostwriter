#!/usr/bin/env python3
"""Check JaCoCo module minimums and coverage of changed executable lines."""

import argparse
from dataclasses import dataclass, field
import fnmatch
import json
from pathlib import Path
import re
import subprocess
import sys
import xml.etree.ElementTree as ET

POLICY = Path(__file__).resolve().parents[1] / "gradle/coverage-policy.json"


@dataclass
class Coverage:
    instruction: tuple[int, int]
    branch: tuple[int, int]
    lines: dict[str, dict[int, bool]] = field(default_factory=dict)


def read_report(path):
    try:
        root = ET.parse(path).getroot()
        if root.tag != "report":
            raise ValueError("Expected a JaCoCo report")
        counters = {}
        for counter in root.findall("counter"):
            covered, missed = int(counter.attrib["covered"]), int(counter.attrib["missed"])
            if covered < 0 or missed < 0:
                raise ValueError("Invalid coverage counter")
            metric = counter.attrib["type"]
            if metric in counters:
                raise ValueError(f"Duplicate {metric} counter")
            counters[metric] = (covered, covered + missed)
        if "INSTRUCTION" not in counters:
            raise ValueError("Missing INSTRUCTION counters")
        lines = {}
        for package in root.findall("package"):
            for source in package.findall("sourcefile"):
                name = f'{package.attrib["name"]}/{source.attrib["name"]}'.lstrip("/")
                if name in lines:
                    raise ValueError(f"Duplicate source: {name}")
                lines[name] = {}
                for line in source.findall("line"):
                    number = int(line.attrib["nr"])
                    covered, missed = int(line.attrib["ci"]), int(line.attrib["mi"])
                    if number <= 0 or covered < 0 or missed < 0 or number in lines[name]:
                        raise ValueError(f"Invalid line counter: {name}")
                    if covered + missed:
                        lines[name][number] = covered > 0
        return Coverage(counters["INSTRUCTION"], counters.get("BRANCH", (0, 0)), lines)
    except (ET.ParseError, KeyError, OSError) as error:
        raise ValueError(f"Invalid coverage report {path}: {error}") from error


def check_module(module, coverage, logic_heavy=False):
    if coverage.instruction[1] == 0:
        raise ValueError(f"{module}: report has no executable instructions")
    strict = logic_heavy or module.split(":")[-1] in {"domain", "data", "parsing", "logic"}
    failures = []
    for metric, minimum in zip(("instruction", "branch"), (90, 85) if strict else (80, 70)):
        covered, total = getattr(coverage, metric)
        if total and covered * 100 < total * minimum:
            failures.append(f"{module}: {metric} coverage {covered}/{total} is below {minimum}%")
    return failures


def changed_lines(diff, module):
    changes = {}
    source = None
    prefix = re.compile(rf"{re.escape(module)}/src/main/(?:java|kotlin)/(.+\.(?:kt|java))$")
    excluded = json.loads(POLICY.read_text())["sourceExclusions"]
    for line in diff.splitlines():
        if line.startswith("+++ "):
            match = prefix.fullmatch(line[4:].removeprefix("b/"))
            source = match.group(1) if match else None
            if source in excluded:
                source = None
        elif source and line.startswith("@@ "):
            match = re.match(r"@@ -\d+(?:,\d+)? \+(\d+)(?:,(\d+))? @@", line)
            if not match:
                raise ValueError("Malformed diff hunk")
            start, count = int(match[1]), int(match[2] or 1)
            if count:
                changes.setdefault(source, set()).update(range(start, start + count))
    return changes


def check_diff(lines, changes):
    failures = []
    covered = total = 0
    for source, numbers in changes.items():
        if source not in lines:
            failures.append(f"Changed source absent from coverage report: {source}")
            continue
        for number in numbers & lines[source].keys():
            total += 1
            covered += lines[source][number]
    if total and covered * 100 < total * 80:
        failures.append(f"diff coverage {covered}/{total} is below 80%")
    return failures


def read_aggregated(path):
    """AGP's dashboard embeds JSON; sum included classes per module and variant."""
    script = Path(path).read_text().strip()
    prefix = "const fullReport = "
    if not script.startswith(prefix) or not script.endswith(";"):
        raise ValueError("Unrecognized aggregated coverage report format")
    report = json.loads(script[len(prefix):-1])
    exclusions = json.loads(POLICY.read_text())["classExclusions"]
    results = {}
    for module in report["modules"]:
        for package in module["packages"]:
            for cls in package["classes"]:
                name = package["name"].replace(".", "/") + "/" + cls["name"] + ".class"
                if any(fnmatch.fnmatchcase(name, pattern) for pattern in exclusions):
                    continue
                suites = [suite for suite in cls["testSuiteCoverages"] if suite["name"] == "Aggregated"]
                if len(suites) != 1 or not suites[0]["variantCoverages"]:
                    raise ValueError(f"Missing aggregated variants for {name}")
                for variant in suites[0]["variantCoverages"]:
                    key = (module["name"].lstrip(":"), variant["name"])
                    counts = results.setdefault(key, [0, 0, 0, 0])
                    for i, metric in enumerate(("instruction", "branch")):
                        counter = variant[metric]
                        covered, total = counter["covered"], counter["total"]
                        if not isinstance(covered, int) or not isinstance(total, int) or not 0 <= covered <= total:
                            raise ValueError(f"Invalid {metric} counters for {name}")
                        counts[i * 2] += covered
                        counts[i * 2 + 1] += total
    if not results:
        raise ValueError("Aggregated coverage report has no included classes")
    return {key: Coverage(tuple(counts[:2]), tuple(counts[2:])) for key, counts in results.items()}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--report", action="append", default=[], metavar="MODULE=XML")
    parser.add_argument("--aggregated-report")
    parser.add_argument("--logic-module", action="append", default=[])
    parser.add_argument("--base-ref", help="Git revision for changed production lines; requires XML reports")
    args = parser.parse_args()
    try:
        reports = dict(item.split("=", 1) for item in args.report)
        if not reports and not args.aggregated_report:
            raise ValueError("At least one coverage report is required")
        if args.base_ref and not reports:
            raise ValueError("Changed-line checks require an XML report")
        diff = subprocess.run(["git", "-c", "core.quotePath=false", "diff", "--no-ext-diff",
                               "--unified=0", "--find-renames", args.base_ref, "--"],
                              check=True, capture_output=True, text=True).stdout if args.base_ref else None
        failures = []
        for module, path in reports.items():
            current = read_report(Path(path))
            failures += check_module(module, current, module in args.logic_module)
            print(f"{module}: instruction {current.instruction[0]}/{current.instruction[1]}, "
                  f"branch {current.branch[0]}/{current.branch[1]}")
            if diff is not None:
                failures += check_diff(current.lines, changed_lines(diff, module))
        if args.aggregated_report:
            current = read_aggregated(args.aggregated_report)
            for (module, variant), coverage in current.items():
                label = f"{module} [{variant}, aggregated]"
                failures += check_module(module, coverage, module in args.logic_module)
                print(f"{label}: instruction {coverage.instruction}, branch {coverage.branch}")
        if failures:
            print("\n".join(failures), file=sys.stderr)
            return 1
        return 0
    except (ValueError, KeyError, TypeError, OSError, subprocess.CalledProcessError) as error:
        print(str(error), file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
