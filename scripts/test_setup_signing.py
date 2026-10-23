from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch
import setup_signing as signing


class SigningSetupTest(unittest.TestCase):
    def test_import_encrypts_the_private_entry_with_the_new_store_password(self):
        command = signing.signing_command("keytool", Path("new.p12"), "new-alias", Path("old.jks"), "old-alias")
        self.assertEqual(command[command.index("-destkeypass:env") + 1], "GHOSTWRITER_NEW_PASSWORD")
        self.assertEqual(command[command.index("-deststorepass:env") + 1], "GHOSTWRITER_NEW_PASSWORD")
        self.assertIn("-importkeystore", command)
        self.assertIn("-J-Dkeystore.pkcs12.keyProtectionAlgorithm=PBEWithHmacSHA256AndAES_256", command)
        self.assertNotIn("-genkeypair", command)

    def test_new_key_uses_explicit_rsa_and_pkcs12_with_passwords_only_in_environment(self):
        command = signing.signing_command("keytool", Path("new.p12"), "ghostwriter")
        self.assertEqual(command[command.index("-keysize") + 1], "3072")
        self.assertEqual(command[command.index("-storetype") + 1], "PKCS12")
        self.assertIn("-storepass:env", command)
        self.assertIn("-keypass:env", command)

    def test_existing_keystore_is_not_overwritten_or_prompted_for(self):
        with tempfile.TemporaryDirectory() as directory:
            destination = Path(directory) / "existing.p12"
            destination.write_bytes(b"Original signing material")
            with patch("setup_signing.getpass.getpass") as prompt:
                with self.assertRaises(ValueError): signing.main(["--new-key", "--keystore", str(destination)])
                prompt.assert_not_called()
            self.assertEqual(destination.read_bytes(), b"Original signing material")

    def test_keystore_inside_the_repository_is_rejected(self):
        with self.assertRaises(ValueError):
            signing.main(["--new-key", "--keystore", str(signing.ROOT / "unsafe.p12")])


if __name__ == "__main__": unittest.main()
