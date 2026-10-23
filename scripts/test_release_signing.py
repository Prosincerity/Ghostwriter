from pathlib import Path
import json
import os
import tempfile
import unittest
from unittest.mock import patch

import release_signing as signing
from release_apk import ReleaseError


class ReleaseSigningTest(unittest.TestCase):
    def test_configuration_requires_the_registered_identity_and_excludes_passwords(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "release.local.json"
            good = {"keystore": str(Path(directory) / "release.p12"), "keyAlias": signing.KEY_ALIAS,
                    "certificateSha256": "a" * 64, "outputDir": "dist/releases"}
            path.write_text(json.dumps(good))
            self.assertEqual(signing.read_config(path), good)
            for change in ({"certificateSha256": ""}, {"keyAlias": "other"}, {"password": "secret"},
                           {"keystore": str(signing.ROOT / "unsafe.p12")}, {"keystore": "other.jks"}):
                path.write_text(json.dumps({**good, **change}))
                with self.subTest(change=change), self.assertRaises(ReleaseError): signing.read_config(path)
            with self.assertRaises(ReleaseError): signing.read_config(path.with_name("missing.json"))

    def test_different_certificate_is_refused(self):
        signing.check_certificate("a" * 64, {"certificateSha256": "a" * 64})
        with self.assertRaises(ReleaseError):
            signing.check_certificate("b" * 64, {"certificateSha256": "a" * 64})

    def test_one_password_prompt_supplies_both_store_and_private_key(self):
        with patch.dict(os.environ, {}, clear=True), patch("release_signing.sys.stdin.isatty", return_value=True), \
                patch("release_signing.getpass.getpass", return_value="locally entered password") as prompt:
            env = signing.password_environment()
        prompt.assert_called_once()
        self.assertEqual(env["GHOSTWRITER_STORE_PASSWORD"], env["GHOSTWRITER_KEY_PASSWORD"])
        with patch.dict(os.environ, {"GHOSTWRITER_STORE_PASSWORD": "private", "GHOSTWRITER_KEY_PASSWORD": "wrong"}, clear=True):
            env = signing.password_environment()
            self.assertEqual(env["GHOSTWRITER_KEY_PASSWORD"], "private")

    def test_noninteractive_execution_never_falls_back_to_a_visible_password_prompt(self):
        with patch.dict(os.environ, {}, clear=True), patch("release_signing.sys.stdin.isatty", return_value=False):
            with self.assertRaises(ReleaseError): signing.password_environment()

    def test_native_verification_uses_the_registered_store_and_alias(self):
        env = signing.verification_environment({"GHOSTWRITER_STORE_PASSWORD": "private"}, Path("release.p12"))
        self.assertEqual(env["GHOSTWRITER_RELEASE_KEYSTORE"], "release.p12")
        self.assertEqual(env["GHOSTWRITER_RELEASE_KEY_ALIAS"], signing.KEY_ALIAS)


if __name__ == "__main__": unittest.main()
