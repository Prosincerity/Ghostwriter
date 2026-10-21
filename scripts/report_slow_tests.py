#!/usr/bin/env python3
"""Report individual JVM tests exceeding three seconds from JUnit XML."""

import argparse
from pathlib import Path
import xml.etree.ElementTree as ET


def slow_tests(directory):
    reports = list(Path(directory).rglob("TEST-*.xml"))
    if not reports:
        raise ValueError(f"No JUnit XML reports in {directory}")
    results = []
    for report in reports:
        for case in ET.parse(report).getroot().iter("testcase"):
            duration = float(case.get("time", "0"))
            if duration > 3:
                results.append((case.get("classname", ""), case.get("name", ""), duration))
    return sorted(results)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("directory")
    args = parser.parse_args()
    for classname, name, duration in slow_tests(args.directory):
        print(f"Slow unit test: {classname}.{name}: {duration:.3f}s")
