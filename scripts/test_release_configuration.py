"""Protect the optimized build and the JNI names used by the native library."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]


class ReleaseConfigurationTest(unittest.TestCase):
    def test_release_enables_r8(self):
        build = (ROOT / "app/build.gradle.kts").read_text()
        self.assertRegex(build, r"release\s*\{\s*optimization\s*\{\s*enable\s*=\s*true")

    def test_native_verification_uses_the_dedicated_release_signing_configuration(self):
        build = (ROOT / "app/build.gradle.kts").read_text()
        self.assertNotIn('signingConfigs.getByName("debug")', build)
        self.assertIn('signingConfigs.getByName("releaseVerification")', build)
        self.assertIn('GHOSTWRITER_RELEASE_KEYSTORE', build)
        self.assertIn('GHOSTWRITER_STORE_PASSWORD', build)

    def test_keep_rule_matches_the_actual_jni_class_and_methods(self):
        kotlin = (ROOT / "app/src/main/java/com/prosincerity/ghostwriter/dictionary/data/EspeakIpa.kt").read_text()
        package = re.search(r"^package (\S+)", kotlin)[1]
        native = (ROOT / "app/src/main/cpp/ipa_jni.c").read_text()
        rules = (ROOT / "app/src/main/keepRules/rules.keep").read_text()
        self.assertIn(f"class {package}.EspeakIpa", rules)
        self.assertIn("private native boolean nativeInitialize(byte[]);", rules)
        self.assertIn("private native byte[] nativePhonemize(byte[], int);", rules)
        for method in ("nativeInitialize", "nativePhonemize"):
            self.assertIn("Java_" + package.replace(".", "_") + "_EspeakIpa_" + method, native)


if __name__ == "__main__":
    unittest.main()
