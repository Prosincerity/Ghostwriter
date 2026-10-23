from pathlib import Path
import tempfile
import unittest
from check_release_native_tests import CLASS, verify_native_report


class NativeReportTest(unittest.TestCase):
    def test_empty_missing_skipped_or_failed_reports_are_not_success(self):
        with tempfile.TemporaryDirectory() as folder:
            for xml in ("<testsuites/>", "<bad", '<testsuite><testcase name="other"/></testsuite>',
                        f'<testsuite><testcase classname="{CLASS}" name="englishPronunciation"><failure/></testcase></testsuite>'):
                Path(folder, "TEST-device.xml").write_text(xml)
                with self.subTest(xml=xml), self.assertRaises(ValueError): verify_native_report(folder)

    def test_actual_native_test_entry_points_must_pass(self):
        with tempfile.TemporaryDirectory() as folder:
            cases = ''.join(f'<testcase classname="{CLASS}" name="{name}"/>' for name in
                            ['englishPronunciation', 'germanPronunciation', 'turkishPronunciation', 'decomposedGermanPronunciation'])
            Path(folder, 'TEST-device.xml').write_text('<testsuite>'+cases+'</testsuite>')
            self.assertEqual(verify_native_report(folder), 4)


if __name__ == "__main__": unittest.main()
