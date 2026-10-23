from pathlib import Path
from contextlib import redirect_stdout
import hashlib
import io
import json
import tempfile
import unittest
from unittest.mock import patch
import setup_signing as signing


class SigningSetupTest(unittest.TestCase):
    def test_new_key_uses_explicit_rsa_aes_and_one_password(self):
        command = signing.signing_command("keytool", Path("new.p12"), "ghostwriter-release")
        self.assertEqual(command[command.index("-keysize") + 1], "3072")
        self.assertEqual(command[command.index("-storetype") + 1], "PKCS12")
        self.assertIn("-J-Dkeystore.pkcs12.keyProtectionAlgorithm=PBEWithHmacSHA256AndAES_256", command)
        self.assertIn("-J-Dkeystore.pkcs12.keyPbeIterationCount=600000", command)
        self.assertEqual(command[command.index("-storepass:env") + 1], command[command.index("-keypass:env") + 1])

    def test_setup_registers_the_new_certificate_without_saving_the_password(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            destination, config = root / "private/release.p12", root / "release.local.json"
            password = "A strong local test password"
            def invoke(command, env):
                if "-genkeypair" in command:
                    Path(command[command.index("-keystore") + 1]).write_bytes(b"encrypted fixture")
                    self.assertEqual(env["GHOSTWRITER_NEW_PASSWORD"], password)
                return b"public certificate DER"
            output = io.StringIO()
            with patch("setup_signing.getpass.getpass", side_effect=[password, password]), \
                    patch("setup_signing.sys.stdin.isatty", return_value=True), \
                    patch("setup_signing.invoke", side_effect=invoke), redirect_stdout(output):
                self.assertEqual(signing.main(["--keystore", str(destination), "--config", str(config)]), 0)
            registered = json.loads(config.read_text())
            self.assertEqual(registered["keystore"], str(destination))
            self.assertEqual(registered["keyAlias"], "ghostwriter-release")
            self.assertEqual(registered["certificateSha256"], hashlib.sha256(b"public certificate DER").hexdigest())
            self.assertIn(f"Encrypted signing store: {destination}", output.getvalue())
            self.assertIn(f"Certificate SHA-256: {registered['certificateSha256']}", output.getvalue())
            self.assertNotIn(password, config.read_text())
            self.assertEqual(destination.stat().st_mode & 0o077, 0)

    def test_existing_key_or_configuration_is_not_overwritten_or_prompted_for(self):
        for existing in ("key", "config"):
            with self.subTest(existing=existing), tempfile.TemporaryDirectory() as directory:
                root = Path(directory)
                destination, config = root / "existing.p12", root / "release.local.json"
                retained = destination if existing == "key" else config
                retained.write_bytes(b"Keep original")
                with patch("setup_signing.getpass.getpass") as prompt:
                    with self.assertRaises(ValueError):
                        signing.main(["--keystore", str(destination), "--config", str(config)])
                    prompt.assert_not_called()
                self.assertEqual(retained.read_bytes(), b"Keep original")

    def test_keystore_inside_the_repository_is_rejected(self):
        with tempfile.TemporaryDirectory() as directory, self.assertRaises(ValueError):
            signing.main(["--keystore", str(signing.ROOT / "unsafe.p12"), "--config", str(Path(directory)/"config.json")])


if __name__ == "__main__": unittest.main()
