"""Coverage policy behavior checks; no Gradle or device required."""

import unittest
import json
import contextlib
import io
from unittest.mock import patch
from pathlib import Path
from tempfile import TemporaryDirectory
from coverage_policy import Coverage, changed_lines, check_diff, check_module, read_report, read_aggregated, main
from report_slow_tests import slow_tests

FIXTURES = Path(__file__).resolve().parent / "fixtures/coverage"


class CoveragePolicyTest(unittest.TestCase):
    def test_default_minimums_are_inclusive(self):
        self.assertEqual([], check_module("app", Coverage((80, 100), (70, 100))))

    def test_instruction_minimum_uses_exact_counters(self):
        self.assertIn("instruction", check_module("app", Coverage((7999, 10000), (70, 100)))[0])

    def test_branch_minimum_uses_exact_counters(self):
        self.assertIn("branch", check_module("app", Coverage((80, 100), (6999, 10000)))[0])

    def test_logic_modules_have_stricter_minimums(self):
        for module in ("domain", "data", "parsing", "logic"):
            with self.subTest(module=module):
                self.assertEqual(2, len(check_module(module, Coverage((89, 100), (84, 100)))))
                self.assertEqual([], check_module(module, Coverage((90, 100), (85, 100))))

    def test_branchless_module_is_valid(self):
        self.assertEqual([], check_module("app", Coverage((80, 100), (0, 0))))

    def test_empty_instruction_report_is_rejected(self):
        with self.assertRaisesRegex(ValueError, "executable instructions"):
            check_module("app", Coverage((0, 0), (0, 0)))

    def test_diff_minimum_is_inclusive(self):
        lines = {"p/File.kt": {n: n <= 4 for n in range(1, 6)}}
        self.assertEqual([], check_diff(lines, {"p/File.kt": set(range(1, 6))}))

    def test_diff_rejects_uncovered_changed_line(self):
        self.assertIn("diff coverage", check_diff({"p/File.kt": {1: False}}, {"p/File.kt": {1}})[0])

    def test_diff_ignores_nonexecutable_lines(self):
        self.assertEqual([], check_diff({"p/File.kt": {1: True}}, {"p/File.kt": {1, 2, 3}}))

    def test_missing_changed_source_is_rejected(self):
        self.assertIn("absent", check_diff({}, {"p/New.kt": {1}})[0])

    def test_rename_diff_uses_new_path_and_added_lines(self):
        diff = '+++ b/app/src/main/java/p/New.kt\n@@ -3,2 +4,3 @@\n-x\n+y\n+z\n+w\n'
        self.assertEqual({"p/New.kt": {4, 5, 6}}, changed_lines(diff, "app"))

    def test_deleted_lines_do_not_count_as_changes(self):
        self.assertEqual({}, changed_lines('+++ b/app/src/main/java/p/F.kt\n@@ -1,2 +0,0 @@\n', "app"))

    def test_nonproduction_files_do_not_count(self):
        self.assertEqual({}, changed_lines('+++ b/app/src/test/java/p/F.kt\n@@ -0,0 +1 @@\n', "app"))

    def test_preview_only_source_does_not_count(self):
        self.assertEqual({}, changed_lines('+++ b/app/src/main/java/com/prosincerity/ghostwriter/ui/screens/HomeScreenPreviews.kt\n@@ -0,0 +1 @@\n', "app"))

    def write_report(self, text):
        directory = TemporaryDirectory()
        self.addCleanup(directory.cleanup)
        path = Path(directory.name) / "coverage.xml"
        path.write_text(text)
        return path

    def test_report_uses_module_counters_not_per_file_minimums(self):
        report = read_report(FIXTURES / "module.xml")
        self.assertEqual([], check_module("app", report))
        self.assertFalse(report.lines["p/Bad.kt"][1])

    def test_report_rejects_negative_counters(self):
        with self.assertRaisesRegex(ValueError, "counter"):
            read_report(FIXTURES / "negative-counter.xml")

    def test_report_requires_instruction_counters(self):
        with self.assertRaisesRegex(ValueError, "INSTRUCTION"):
            read_report(FIXTURES / "missing-instructions.xml")

    def test_report_rejects_malformed_xml(self):
        with self.assertRaises(ValueError):
            read_report(FIXTURES / "malformed.xml")

    def test_aggregated_parser_reads_minimal_committed_sample(self):
        coverage = read_aggregated(FIXTURES / "aggregated.js")
        self.assertEqual(Coverage((80, 100), (70, 100)), coverage[("app", "debug")])

    def aggregated_report(self, classes, module=":app"):
        return {"modules": [{"name": module, "packages": [{"name": "com.prosincerity.ghostwriter.data", "classes": classes}]}]}

    def aggregated_class(self, name, covered, total, variants=None):
        return {"name": name, "testSuiteCoverages": [{"name": "Aggregated", "variantCoverages": variants or [
            {"name": "debug", "instruction": {"covered": covered, "total": total},
             "branch": {"covered": 8, "total": 10}}]}]}

    def write_aggregated(self, report):
        return self.write_report("const fullReport = " + json.dumps(report) + ";")

    def test_aggregated_gate_uses_weighted_module_counters(self):
        path = self.write_aggregated(self.aggregated_report([
            self.aggregated_class("Good", 99, 100), self.aggregated_class("Bad", 0, 10)]))
        coverage = read_aggregated(path)[("app", "debug")]
        self.assertEqual((99, 110), coverage.instruction)
        self.assertEqual([], check_module("app", coverage))

    def test_aggregated_gate_excludes_plain_data_classes(self):
        path = self.write_aggregated(self.aggregated_report([
            self.aggregated_class("Good", 80, 100), self.aggregated_class("DictionaryMatch", 0, 100)]))
        self.assertEqual((80, 100), read_aggregated(path)[("app", "debug")].instruction)

    def test_aggregated_gate_keeps_logic_in_data_classes(self):
        path = self.write_aggregated(self.aggregated_report([self.aggregated_class("ProjectSummary", 0, 100)]))
        self.assertEqual((0, 100), read_aggregated(path)[("app", "debug")].instruction)

    def test_aggregated_variants_cannot_compensate_for_each_other(self):
        variants = [
            {"name": "debug", "instruction": {"covered": 100, "total": 100}, "branch": {"covered": 10, "total": 10}},
            {"name": "release", "instruction": {"covered": 0, "total": 100}, "branch": {"covered": 0, "total": 10}},
        ]
        report = read_aggregated(self.write_aggregated(self.aggregated_report([self.aggregated_class("Logic", 0, 0, variants)])))
        self.assertEqual([], check_module("app", report[("app", "debug")]))
        self.assertEqual(2, len(check_module("app", report[("app", "release")])))

    def test_aggregated_empty_report_is_rejected(self):
        with self.assertRaisesRegex(ValueError, "no included classes"):
            read_aggregated(self.write_aggregated({"modules": []}))

    def test_aggregated_invalid_counters_are_rejected(self):
        with self.assertRaisesRegex(ValueError, "Invalid instruction counters"):
            read_aggregated(self.write_aggregated(self.aggregated_report([self.aggregated_class("Bad", 2, 1)])))

    def test_aggregated_modules_cannot_compensate_for_each_other(self):
        report = self.aggregated_report([self.aggregated_class("Good", 100, 100)])
        report["modules"] += self.aggregated_report([self.aggregated_class("Bad", 0, 10)], ":data")["modules"]
        coverage = read_aggregated(self.write_aggregated(report))
        self.assertEqual([], check_module("app", coverage[("app", "debug")]))
        self.assertEqual(2, len(check_module("data", coverage[("data", "debug")])))

    def invoke(self, *arguments):
        output = io.StringIO()
        with patch("sys.argv", ["coverage_policy.py", *arguments]), contextlib.redirect_stdout(output), contextlib.redirect_stderr(output):
            status = main()
        return status, output.getvalue()

    def test_cli_checks_changed_lines_without_baseline(self):
        diff = '+++ b/app/src/main/java/p/Good.kt\n@@ -0,0 +1 @@\n+covered\n'
        with patch("coverage_policy.subprocess.run") as git:
            git.return_value.stdout = diff
            status, output = self.invoke("--report", f"app={FIXTURES / 'module.xml'}", "--base-ref", "base")
        self.assertEqual(0, status, output)
        self.assertIn("instruction 90/100", output)

    def test_cli_rejects_uncovered_changed_lines_without_baseline(self):
        diff = '+++ b/app/src/main/java/p/Bad.kt\n@@ -0,0 +1 @@\n+uncovered\n'
        with patch("coverage_policy.subprocess.run") as git:
            git.return_value.stdout = diff
            status, output = self.invoke("--report", f"app={FIXTURES / 'module.xml'}", "--base-ref", "base")
        self.assertEqual(1, status)
        self.assertIn("diff coverage 0/1 is below 80%", output)

    def test_cli_requires_xml_report_for_changed_lines(self):
        status, output = self.invoke("--aggregated-report", "unused.js", "--base-ref", "base")
        self.assertEqual(1, status)
        self.assertIn("require an XML report", output)

    def test_cli_checks_aggregated_module_minimums(self):
        path = self.write_aggregated(self.aggregated_report([self.aggregated_class("Bad", 79, 100)]))
        status, output = self.invoke("--aggregated-report", str(path))
        self.assertEqual(1, status)
        self.assertIn("below 80%", output)

    def test_cli_accepts_aggregated_coverage_above_minimum(self):
        current = self.write_aggregated(self.aggregated_report([self.aggregated_class("Logic", 90, 100)]))
        status, output = self.invoke("--aggregated-report", str(current))
        self.assertEqual(0, status, output)
        self.assertIn("instruction (90, 100)", output)

    def test_cli_checks_xml_module_minimums(self):
        path = self.write_report('<report><counter type="INSTRUCTION" covered="79" missed="21"/></report>')
        status, output = self.invoke("--report", f"app={path}")
        self.assertEqual(1, status)
        self.assertIn("below 80%", output)

    def test_slow_test_report_names_test_and_duration(self):
        path = self.write_report('<testsuite><testcase classname="ParserTest" name="largeInput" time="3.1"/></testsuite>')
        path.rename(path.with_name("TEST-parser.xml"))
        self.assertEqual([("ParserTest", "largeInput", 3.1)], slow_tests(path.parent))

    def test_slow_test_report_ignores_fast_tests(self):
        path = self.write_report('<testsuite><testcase classname="ParserTest" name="emptyInput" time="3"/></testsuite>')
        path.rename(path.with_name("TEST-parser.xml"))
        self.assertEqual([], slow_tests(path.parent))

    def test_slow_test_report_requires_results(self):
        path = self.write_report('<testsuite/>')
        with self.assertRaisesRegex(ValueError, "No JUnit XML"):
            slow_tests(path.parent)


if __name__ == "__main__":
    unittest.main()
