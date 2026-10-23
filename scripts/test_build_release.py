"""Release packaging must preserve source identity and exclude signing material."""
from pathlib import Path
import json
import os
import subprocess
import tempfile
import unittest
import zipfile

import build_release as build
from release_apk import ReleaseError


class BuildReleaseTest(unittest.TestCase):
    def test_sdk_tools_are_selected_by_numeric_version_and_support_windows_names(self):
        with tempfile.TemporaryDirectory() as folder:
            sdk = Path(folder)
            for version in ("9.0.0", "36.0.0"):
                tools = sdk / "build-tools" / version
                tools.mkdir(parents=True)
                for name in ("aapt.exe", "zipalign.exe", "apksigner.bat"): (tools / name).touch()
            found = build.find_build_tools(sdk)
            self.assertEqual(found["aapt"].parent.name, "36.0.0")
            self.assertEqual(found["apksigner"].name, "apksigner.bat")
            with self.assertRaises(ReleaseError): build.find_build_tools(sdk / "missing")

    def test_local_properties_supports_escaped_sdk_paths(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            (root / "local.properties").write_text(r"sdk.dir=C\:\\Android\\Sdk" + "\n")
            self.assertEqual(str(build.sdk_from_properties(root)), r"C:\Android\Sdk")

    def test_source_archive_contains_both_exact_revisions_without_untracked_files(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder) / "app"; native = Path(folder) / "native"
            for repo in (root, native):
                repo.mkdir()
                subprocess.run(["git", "init", "-q", str(repo)], check=True)
                (repo / "tracked.txt").write_text(repo.name)
                subprocess.run(["git", "-C", str(repo), "add", "tracked.txt"], check=True)
                subprocess.run(["git", "-C", str(repo), "-c", "user.name=Test", "-c", "user.email=test@example.invalid",
                                "commit", "-qm", "Fixture"], check=True)
                (repo / "untracked.keystore").write_text("Never archive this")
            output = Path(folder) / "source.zip"
            build.source_archive(root, native, "HEAD", "HEAD", output)
            with zipfile.ZipFile(output) as archive:
                names = set(archive.namelist())
                self.assertIn("Ghostwriter/tracked.txt", names)
                self.assertIn("Ghostwriter/third_party/espeak-ng/tracked.txt", names)
                self.assertFalse(any("keystore" in name for name in names))

    def test_source_archive_refuses_tracked_signing_material(self):
        for name in ("Ghostwriter/release.local.json", "Ghostwriter/private.jks", "Ghostwriter/.env",
                     "Ghostwriter/keystore.properties"):
            with self.subTest(name=name), self.assertRaises(ReleaseError): build.check_source_member(name)
        build.check_source_member("Ghostwriter/.env.example")
        with self.assertRaises(ReleaseError): build.check_source_member("../outside")

    def test_bundle_contains_installable_apk_source_notices_and_checksums_but_not_mapping(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            for name in ("app.apk", "source.zip", "INSTALL.txt", "LICENSE", "THIRD_PARTY_LICENSES.md", "RELEASE.json"):
                (root / name).write_text(name)
            (root / "mapping.txt").write_text("Keep for crash reports")
            members = ["app.apk", "source.zip", "INSTALL.txt", "LICENSE", "THIRD_PARTY_LICENSES.md", "RELEASE.json"]
            output = build.bundle_zip(root, "friends.zip", members)
            with zipfile.ZipFile(output) as archive:
                self.assertEqual(set(archive.namelist()), set(members) | {"SHA256SUMS"})
                self.assertNotIn("mapping.txt", archive.namelist())
                checksums = archive.read("SHA256SUMS").decode()
                self.assertIn("  app.apk\n", checksums)
                self.assertIn("  source.zip\n", checksums)

    def test_version_names_cannot_escape_the_output_directory(self):
        self.assertEqual(build.release_stem("1.3.0-beta.2", 6), "Ghostwriter-1.3.0-beta.2-vc6")
        for version in ("../escape", "", ".", "a/b", "a\\b"):
            with self.subTest(version=version), self.assertRaises(ReleaseError): build.release_stem(version, 6)

    def test_config_does_not_accept_password_fields(self):
        with tempfile.TemporaryDirectory() as folder:
            path = Path(folder) / "release.local.json"
            path.write_text(json.dumps({"storePassword": "Never store passwords here"}))
            with self.assertRaises(ReleaseError): build.read_config(path)


if __name__ == "__main__": unittest.main()
