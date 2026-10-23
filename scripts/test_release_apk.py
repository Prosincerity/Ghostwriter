"""Release compatibility and binary checks, using isolated synthetic APK fixtures."""
from dataclasses import replace
from pathlib import Path
import struct
import tempfile
import unittest
import zipfile

import release_apk as release


def dex_fixture(descriptor=release.JNI_CLASS, names=("nativeInitialize", "nativePhonemize"), flags=0x102):
    strings = [descriptor, "[B", "Z", "I", *names]
    data = bytearray(112 + 6 * 4 + 4 * 4 + 2 * 12 + 2 * 8 + 32)
    data[:8] = b"dex\n037\x00"
    string_ids, type_ids, proto_ids, method_ids, class_defs = 112, 136, 152, 176, 192
    for offset, count, position in [(56, 6, string_ids), (64, 4, type_ids),
                                    (72, 2, proto_ids), (88, 2, method_ids), (96, 1, class_defs)]:
        struct.pack_into("<II", data, offset, count, position)
    for index, value in enumerate(strings):
        struct.pack_into("<I", data, string_ids + index * 4, len(data))
        data.extend(bytes([len(value)]) + value.encode() + b"\x00")
    for index in range(4): struct.pack_into("<I", data, type_ids + index * 4, index)
    params_one = len(data); data.extend(struct.pack("<IH", 1, 1))
    params_two = len(data); data.extend(struct.pack("<IHH", 2, 1, 3))
    struct.pack_into("<III", data, proto_ids, 0, 2, params_one)
    struct.pack_into("<III", data, proto_ids + 12, 0, 1, params_two)
    struct.pack_into("<HHIHHI", data, method_ids, 0, 0, 4, 0, 1, 5)
    struct.pack_into("<I", data, class_defs + 24, len(data))
    encoded_flags = bytes([flags & 0x7f | 0x80, flags >> 7])
    data.extend(b"\x00\x00\x02\x00\x00" + encoded_flags + b"\x00\x01" + encoded_flags + b"\x00")
    return bytes(data)


def elf_fixture(alignment=16_384, abi="arm64-v8a"):
    wide = abi in {"arm64-v8a", "x86_64"}
    data = bytearray(120 if wide else 84)
    data[:7] = b"\x7fELF" + bytes([2 if wide else 1, 1, 1])
    struct.pack_into("<H", data, 18, {"arm64-v8a": 183, "armeabi-v7a": 40, "x86": 3, "x86_64": 62}[abi])
    struct.pack_into("<Q" if wide else "<I", data, 32 if wide else 28, 64 if wide else 52)
    struct.pack_into("<HH", data, 54 if wide else 42, 56 if wide else 32, 1)
    struct.pack_into("<I", data, 64 if wide else 52, 1)
    struct.pack_into("<Q" if wide else "<I", data, 112 if wide else 80, alignment)
    for symbol in release.JNI_SYMBOLS: data.extend(symbol.encode() + b"\x00")
    return bytes(data)


class ReleaseApkTest(unittest.TestCase):
    def setUp(self):
        self.old = release.ApkInfo(release.APPLICATION_ID, 5, "1.3.0-beta.1", 24, 37,
                                   ("arm64-v8a", "x86_64"), False)
        self.new = replace(self.old, version_code=6, version_name="1.3.0-beta.2",
                           abis=tuple(sorted(release.REQUIRED_ABIS)), debuggable=False)

    def test_badging_reads_install_requirements_and_debug_flag(self):
        text = """package: name='com.example.app' versionCode='6' versionName='1.0'
sdkVersion:'24'
targetSdkVersion:'37'
native-code: 'arm64-v8a' 'armeabi-v7a'
application-debuggable
"""
        info = release.parse_badging(text)
        self.assertEqual((info.package_name, info.version_code, info.min_sdk), ("com.example.app", 6, 24))
        self.assertTrue(info.debuggable)
        self.assertEqual(info.abis, ("arm64-v8a", "armeabi-v7a"))

    def test_badging_rejects_missing_or_malformed_requirements(self):
        for text in ("", "package: name='app' versionCode='bad' versionName='1'"):
            with self.subTest(text=text), self.assertRaises(release.ReleaseError): release.parse_badging(text)

    def test_certificate_requires_one_verified_signer(self):
        digest = "a" * 64
        self.assertEqual(release.parse_certificate(f"Signer #1 certificate SHA-256 digest: {digest}"), digest)
        for text in ("", f"Signer #1 certificate SHA-256 digest: {digest}\nSigner #2 certificate SHA-256 digest: {'b'*64}"):
            with self.assertRaises(release.ReleaseError): release.parse_certificate(text)

    def test_optimized_release_can_update_the_previous_release(self):
        release.check_update(self.new, "a" * 64, self.old, "a" * 64)

    def test_update_rejects_wrong_key_package_version_or_reduced_device_support(self):
        cases = [replace(self.new, package_name="other.app"), replace(self.new, version_code=5),
                 replace(self.new, version_code=4), replace(self.new, min_sdk=25),
                 replace(self.new, abis=("arm64-v8a",)), replace(self.new, debuggable=True)]
        for candidate in cases:
            with self.subTest(candidate=candidate), self.assertRaises(release.ReleaseError):
                release.check_update(candidate, "a" * 64, self.old, "a" * 64)
        with self.assertRaises(release.ReleaseError):
            release.check_update(self.new, "b" * 64, self.old, "a" * 64)

    def test_channel_history_prevents_reusing_a_version_or_changing_the_key(self):
        channel = {"package_name": release.APPLICATION_ID, "version_code": 6, "certificate_sha256": "a" * 64}
        with self.assertRaises(release.ReleaseError): release.check_channel(self.new, "a" * 64, channel)
        release.check_channel(replace(self.new, version_code=7), "a" * 64, channel)
        with self.assertRaises(release.ReleaseError):
            release.check_channel(replace(self.new, version_code=7), "b" * 64, channel)

    def test_dex_native_signatures_survive_optimization(self):
        self.assertEqual(release.dex_native_methods(dex_fixture()), release.JNI_METHODS)
        for data in (dex_fixture(descriptor="La/b;"), dex_fixture(names=("a", "b")), dex_fixture(flags=2)):
            self.assertNotEqual(release.dex_native_methods(data), release.JNI_METHODS)
        with self.assertRaises(release.ReleaseError): release.dex_native_methods(b"invalid")

    def test_elf_requires_exported_jni_names_and_16kb_load_alignment(self):
        release.check_elf(elf_fixture(), "arm64-v8a")
        with self.assertRaises(release.ReleaseError): release.check_elf(elf_fixture(4096), "arm64-v8a")
        with self.assertRaises(release.ReleaseError): release.check_elf(b"not ELF", "arm64-v8a")
        with self.assertRaises(release.ReleaseError):
            release.check_elf(elf_fixture().replace(release.JNI_SYMBOLS[0].encode(), b"x" * len(release.JNI_SYMBOLS[0])), "arm64-v8a")
        release.check_elf(elf_fixture(4096, "armeabi-v7a"), "armeabi-v7a")
        with self.assertRaises(release.ReleaseError): release.check_elf(elf_fixture(), "armeabi-v7a")

    def test_apk_requires_jni_for_every_abi_data_licenses_and_no_databases(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "fixture.apk"
            def write(extra=None, missing=None, dex=None):
                members = {"classes.dex": dex or dex_fixture()}
                members.update({name: b"data" for name in release.REQUIRED_ASSETS})
                members.update({f"lib/{abi}/libghostwriter_ipa.so": elf_fixture(abi=abi) for abi in release.REQUIRED_ABIS})
                if extra: members.update(extra)
                if missing: members.pop(missing)
                with zipfile.ZipFile(path, "w") as archive:
                    for name, data in members.items(): archive.writestr(name, data)
            write(); release.check_apk_payload(path)
            for missing in (next(iter(release.REQUIRED_ASSETS)), "lib/armeabi-v7a/libghostwriter_ipa.so"):
                write(missing=missing)
                with self.assertRaises(release.ReleaseError): release.check_apk_payload(path)
            write(extra={"assets/dictionaries/en.db": b"database"})
            with self.assertRaises(release.ReleaseError): release.check_apk_payload(path)
            write(dex=dex_fixture(names=("a", "b")))
            with self.assertRaises(release.ReleaseError): release.check_apk_payload(path)


if __name__ == "__main__": unittest.main()
