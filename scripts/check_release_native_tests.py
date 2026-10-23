#!/usr/bin/env python3
"""Reject empty/failed optimized JNI test runs, even if Gradle reports success."""
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "app/build/outputs/androidTest-results/connected/releaseCheck"
CLASS = "com.prosincerity.ghostwriter.dictionary.data.EspeakReleaseCheckInstrumentation"


def verify_native_report(directory=REPORT):
    tests = []
    try:
        for path in Path(directory).glob("TEST-*.xml"):
            tests.extend(t for t in ET.parse(path).getroot().iter("testcase") if t.get("classname") == CLASS)
    except (ET.ParseError, OSError) as error:
        raise ValueError("Cannot read optimized JNI test reports") from error
    required = {"englishPronunciation", "germanPronunciation", "turkishPronunciation", "decomposedGermanPronunciation"}
    passed = {test.get("name") for test in tests
              if not any(test.find(tag) is not None for tag in ("failure", "error", "skipped"))}
    if not required.issubset(passed) or len(passed) != len(tests):
        raise ValueError("Optimized JNI tests were empty, incomplete, skipped, or failed; inspect the device report")
    return len(tests)


if __name__ == "__main__":
    try: print(f"Optimized JNI check: {verify_native_report()} tests passed")
    except ValueError as error:
        print(error, file=sys.stderr)
        sys.exit(1)
