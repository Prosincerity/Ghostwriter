"""Dependency-free checks for distributable Android APKs and in-place updates."""
from dataclasses import dataclass
from pathlib import Path
import hashlib
import re
import struct
import zipfile

APPLICATION_ID = "com.prosincerity.ghostwriter"
REQUIRED_ABIS = {"arm64-v8a", "armeabi-v7a", "x86", "x86_64"}
JNI_CLASS = "Lcom/prosincerity/ghostwriter/dictionary/data/EspeakIpa;"
JNI_METHODS = {"nativeInitialize([B)Z", "nativePhonemize([BI)[B"}
JNI_SYMBOLS = tuple("Java_com_prosincerity_ghostwriter_dictionary_data_EspeakIpa_" + name
                    for name in ("nativeInitialize", "nativePhonemize"))
ESPEAK_DATA = ("intonations", "phondata", "phonindex", "phontab", "en_dict", "de_dict", "tr_dict",
               "lang/gmw/en", "lang/gmw/de", "lang/trk/tr")
REQUIRED_ASSETS = {"assets/espeak-ng-1.52/espeak-ng-data/" + name for name in ESPEAK_DATA} | {
    "assets/licenses/GPL-3.0-or-later.txt", "assets/licenses/THIRD_PARTY_LICENSES.md",
    "assets/licenses/Roboto-Apache-2.0.txt", "assets/licenses/Ghostwriter-Dict-MIT.txt",
    "assets/licenses/Ghostwriter-Dict-LICENSE-DATA.md", "assets/licenses/COPYING.UCD",
}


class ReleaseError(Exception):
    """A failed release precondition; the caller must not publish an artifact."""


@dataclass(frozen=True)
class ApkInfo:
    package_name: str
    version_code: int
    version_name: str
    min_sdk: int
    target_sdk: int
    abis: tuple[str, ...]
    debuggable: bool


def parse_badging(text):
    def required(pattern):
        found = re.search(pattern, text, re.M)
        if not found: raise ReleaseError("APK metadata is incomplete or malformed")
        return found[1]
    package = required(r"^package: name='([^']+)'")
    code = int(required(r"^package:.* versionCode='(\d+)'") )
    name = required(r"^package:.* versionName='([^']+)'")
    minimum = int(required(r"^sdkVersion:'(\d+)'") )
    target = int(required(r"^targetSdkVersion:'(\d+)'") )
    abis = tuple(sorted(re.findall(r"'([^']+)'", required(r"^native-code: (.+)$"))))
    if code <= 0 or not abis: raise ReleaseError("APK must have a positive version code and native ABIs")
    return ApkInfo(package, code, name, minimum, target, abis, "application-debuggable" in text.splitlines())


def parse_certificate(text):
    certificates = re.findall(r"^Signer #\d+ certificate SHA-256 digest: ([0-9a-fA-F]{64})$", text, re.M)
    if len(certificates) != 1: raise ReleaseError("Expected exactly one verified APK signing certificate")
    return certificates[0].lower()


def check_update(candidate, certificate, previous=None, previous_certificate=None):
    if candidate.package_name != APPLICATION_ID or candidate.debuggable:
        raise ReleaseError("Distribution requires the original application ID and a non-debuggable release APK")
    if not REQUIRED_ABIS.issubset(candidate.abis):
        raise ReleaseError("The universal APK must include all four supported Android ABIs")
    if previous is None: return
    if previous.package_name != candidate.package_name or certificate != previous_certificate:
        raise ReleaseError("The previous APK has a different application ID or signing key; it cannot be updated in place")
    if candidate.version_code <= previous.version_code:
        raise ReleaseError("Increase versionCode above the previous shared APK before building another release")
    if candidate.min_sdk > previous.min_sdk or not set(previous.abis).issubset(candidate.abis):
        raise ReleaseError("The new APK would drop devices supported by the previous APK")


def check_channel(candidate, certificate, channel):
    if not channel: return
    try:
        same_identity = channel["package_name"] == candidate.package_name and channel["certificate_sha256"] == certificate
        newer = candidate.version_code > int(channel["version_code"])
    except (KeyError, TypeError, ValueError) as error:
        raise ReleaseError("Release channel history is malformed; preserve and repair it before publishing") from error
    if not same_identity: raise ReleaseError("Release channel history uses a different signing identity")
    if not newer: raise ReleaseError("Increase versionCode above the last packaged release recorded in channel.json")


def sha256_file(path):
    digest = hashlib.sha256()
    with Path(path).open("rb") as source:
        for chunk in iter(lambda: source.read(1024 * 1024), b""): digest.update(chunk)
    return digest.hexdigest()


def dex_native_methods(data):
    """Read native method descriptors from class_data, independent of R8 mapping names."""
    try:
        if len(data) < 112 or not re.fullmatch(rb"dex\n0(?:3[5-9]|40)\x00", data[:8]):
            raise ReleaseError("Unsupported or truncated DEX header")
        def u32(offset): return struct.unpack_from("<I", data, offset)[0]
        def u16(offset): return struct.unpack_from("<H", data, offset)[0]
        def uleb(offset):
            value = 0
            for shift in range(0, 35, 7):
                byte = data[offset]; offset += 1
                value |= (byte & 0x7f) << shift
                if not byte & 0x80: return value, offset
            raise ReleaseError("Invalid DEX variable-length integer")
        string_count, string_table = u32(56), u32(60)
        type_count, type_table = u32(64), u32(68)
        proto_count, proto_table = u32(72), u32(76)
        method_count, method_table = u32(88), u32(92)
        class_count, class_table = u32(96), u32(100)
        for count, start, size in [(string_count, string_table, 4), (type_count, type_table, 4),
                                   (proto_count, proto_table, 12), (method_count, method_table, 8),
                                   (class_count, class_table, 32)]:
            if start + count * size > len(data): raise ReleaseError("Truncated DEX table")
        def string(index):
            if index >= string_count: raise ReleaseError("Invalid DEX string index")
            _, start = uleb(u32(string_table + index * 4))
            # All descriptors and native names checked here are ASCII; unrelated MUTF-8 strings are never decoded.
            return data[start:data.index(0, start)].decode("ascii")
        def descriptor(index):
            if index >= type_count: raise ReleaseError("Invalid DEX type index")
            return string(u32(type_table + index * 4))
        found = set()
        for index in range(class_count):
            definition = class_table + index * 32
            # Check raw bytes to avoid decoding unrelated class names.
            type_index = u32(definition)
            if type_index >= type_count: raise ReleaseError("Invalid DEX class index")
            _, start = uleb(u32(string_table + u32(type_table + type_index * 4) * 4))
            if data[start:data.index(0, start)] != JNI_CLASS.encode(): continue
            position = u32(definition + 24)
            if not position: continue
            counts = []
            for _ in range(4):
                value, position = uleb(position); counts.append(value)
            for _ in range(counts[0] + counts[1]):
                _, position = uleb(position); _, position = uleb(position)
            for count in counts[2:]:
                method_index = 0
                for _ in range(count):
                    difference, position = uleb(position); method_index += difference
                    flags, position = uleb(position); _, position = uleb(position)
                    if method_index >= method_count: raise ReleaseError("Invalid DEX method index")
                    if not flags & 0x100: continue
                    method = method_table + method_index * 8
                    proto = u16(method + 2)
                    if proto >= proto_count: raise ReleaseError("Invalid DEX prototype index")
                    proto = proto_table + proto * 12
                    parameters = u32(proto + 8)
                    args = "" if not parameters else "".join(descriptor(u16(parameters + 4 + i * 2))
                                                            for i in range(u32(parameters)))
                    found.add(string(u32(method + 4)) + "(" + args + ")" + descriptor(u32(proto + 4)))
        return found
    except (IndexError, ValueError, UnicodeError, struct.error) as error:
        raise ReleaseError("Malformed DEX while checking JNI names") from error


def check_elf(data, abi):
    try:
        if data[:4] != b"\x7fELF" or data[5] != 1 or data[4] not in (1, 2):
            raise ReleaseError(f"Invalid ELF library for {abi}")
        wide = data[4] == 2
        machines = {"arm64-v8a": (True, 183), "armeabi-v7a": (False, 40), "x86": (False, 3), "x86_64": (True, 62)}
        if abi not in machines or (wide, struct.unpack_from("<H", data, 18)[0]) != machines[abi]:
            raise ReleaseError(f"ELF architecture does not match its APK ABI directory: {abi}")
        offset = struct.unpack_from("<Q" if wide else "<I", data, 32 if wide else 28)[0]
        size, count = struct.unpack_from("<HH", data, 54 if wide else 42)
        if size < (56 if wide else 32) or not count or offset + size * count > len(data):
            raise ReleaseError(f"Invalid ELF program headers for {abi}")
        loads = []
        for i in range(count):
            header = offset + size * i
            if struct.unpack_from("<I", data, header)[0] == 1:
                loads.append(struct.unpack_from("<Q" if wide else "<I", data, header + (48 if wide else 28))[0])
        minimum = 16_384 if abi in {"arm64-v8a", "x86_64"} else 4096
        if not loads or any(value < minimum for value in loads):
            raise ReleaseError(f"Native LOAD segments for {abi} need at least {minimum}-byte alignment")
        for symbol in JNI_SYMBOLS:
            if symbol.encode() + b"\x00" not in data:
                raise ReleaseError(f"Missing native JNI symbol for {abi}: {symbol}")
    except (IndexError, struct.error) as error:
        raise ReleaseError(f"Truncated ELF library for {abi}") from error


def check_apk_payload(path):
    with zipfile.ZipFile(path) as apk:
        names = set(apk.namelist())
        missing = REQUIRED_ASSETS - names
        if missing: raise ReleaseError("APK is missing required data/notices: " + ", ".join(sorted(missing)))
        if any(apk.getinfo(name).file_size == 0 for name in REQUIRED_ASSETS):
            raise ReleaseError("APK contains empty required data or notices")
        if any(name.lower().endswith((".db", ".db.gz", ".sqlite", ".sqlite3")) for name in names):
            raise ReleaseError("Downloaded dictionary databases must not enter the APK")
        found = set()
        for name in names:
            if re.fullmatch(r"classes(?:\d+)?\.dex", name): found.update(dex_native_methods(apk.read(name)))
        if not JNI_METHODS.issubset(found):
            raise ReleaseError("Optimized DEX is missing the original EspeakIpa native class/method signatures")
        for abi in REQUIRED_ABIS:
            name = f"lib/{abi}/libghostwriter_ipa.so"
            if name not in names: raise ReleaseError(f"APK is missing the native library for {abi}")
            check_elf(apk.read(name), abi)
