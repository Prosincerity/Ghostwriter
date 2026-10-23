#!/usr/bin/env python3
"""Build, verify, sign, and package a universal APK with its matching source."""
import argparse
from dataclasses import asdict
import hashlib
import io
import json
import os
from pathlib import Path, PurePosixPath
import re
import shutil
import subprocess
import sys
import tempfile
import zipfile

from release_apk import (APPLICATION_ID, ReleaseError, check_apk_payload, check_channel,
                         check_update, parse_badging, parse_certificate, sha256_file)
from check_release_native_tests import verify_native_report
from release_signing import (DEFAULT_CONFIG, KEY_ALIAS, read_config, password_environment,
                             check_certificate, verification_environment)

ROOT = Path(__file__).resolve().parents[1]


def run(command, *, cwd=ROOT, env=None, binary=False, stream=False):
    result = subprocess.run([str(arg) for arg in command], cwd=cwd, env=env,
                            capture_output=not stream, text=not binary)
    if result.returncode:
        if not stream:
            message = result.stderr or result.stdout
            if isinstance(message, bytes): message = message.decode(errors="replace")
            print(message, file=sys.stderr)
        raise ReleaseError(f"{Path(command[0]).name} failed (exit {result.returncode}); no release was published")
    return result.stdout


def find_build_tools(sdk):
    base = Path(sdk) / "build-tools"
    if not base.is_dir(): raise ReleaseError("Android build-tools are missing; follow documentation/SETUP_NOTES.md")
    versions = sorted(base.iterdir(), key=lambda p: tuple(int(n) for n in re.findall(r"\d+", p.name)), reverse=True)
    for directory in versions:
        found = {}
        for tool in ("aapt", "zipalign", "apksigner"):
            found[tool] = next((directory / name for name in (tool, tool + ".exe", tool + ".bat")
                                if (directory / name).is_file()), None)
        if all(found.values()): return found
    raise ReleaseError("Install Android SDK build-tools containing aapt, zipalign, and apksigner")


def sdk_from_properties(root):
    path = Path(root) / "local.properties"
    if not path.is_file(): return None
    match = re.search(r"^sdk\.dir\s*=\s*(.+)$", path.read_text(encoding="utf-8"), re.M)
    if not match: return None
    return Path(match[1].replace(r"\:", ":").replace(r"\\", "\\").replace(r"\ ", " "))


def check_source_member(name):
    path = PurePosixPath(name)
    if path.is_absolute() or ".." in path.parts or "\\" in name:
        raise ReleaseError("Unsafe source archive path")
    filename = path.name.lower()
    if filename.endswith((".jks", ".keystore", ".p12", ".pfx")) or filename in {
        "release.local.json", "keystore.properties", "signing.properties", ".env"
    } or (filename.startswith(".env.") and filename not in {".env.example", ".env.sample"}):
        raise ReleaseError("Signing material or local secrets are tracked in Git; remove them before packaging source")


def source_archive(root, native, revision, native_revision, output):
    seen = set()
    with zipfile.ZipFile(output, "w") as archive:
        for repo, commit, prefix in [(root, revision, "Ghostwriter/"),
                                      (native, native_revision, "Ghostwriter/third_party/espeak-ng/")]:
            raw = run(["git", "archive", "--format=zip", "--prefix=" + prefix, commit], cwd=repo, binary=True)
            with zipfile.ZipFile(io.BytesIO(raw)) as source:
                for info in source.infolist():
                    check_source_member(info.filename)
                    if info.filename in seen:
                        if info.is_dir(): continue
                        raise ReleaseError("Duplicate source file in native source archive")
                    archive.writestr(info, source.read(info))
                    seen.add(info.filename)


def release_stem(version_name, version_code):
    if not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9._-]*", version_name):
        raise ReleaseError("Use a filename-safe versionName containing letters, digits, dots, dashes, or underscores")
    return f"Ghostwriter-{version_name}-vc{version_code}"


def bundle_zip(directory, filename, members):
    directory = Path(directory)
    checksums = "".join(f"{sha256_file(directory / name)}  {name}\n" for name in sorted(members))
    (directory / "SHA256SUMS").write_text(checksums)
    output = directory / filename
    with zipfile.ZipFile(output, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        for name in [*members, "SHA256SUMS"]: archive.write(directory / name, name)
    (directory / (filename + ".sha256")).write_text(f"{sha256_file(output)}  {filename}\n")
    return output


def clean_revision(root):
    if run(["git", "status", "--porcelain", "--untracked-files=normal"], cwd=root).strip():
        raise ReleaseError("Commit source changes and leave the working tree clean before packaging a release")
    return run(["git", "rev-parse", "HEAD"], cwd=root).strip()


def previous_release(output, certificate):
    """Resolve the last release from public history, refusing missing or altered artifacts."""
    output = Path(output)
    history = output / "channel.json"
    if not history.exists():
        if any(output.glob("Ghostwriter-*/RELEASE.json")):
            raise ReleaseError("Restore channel.json before building; existing releases must not lose their update history")
        return None, None
    try:
        channel = json.loads(history.read_text(encoding="utf-8"))
        if channel["package_name"] != APPLICATION_ID or channel["certificate_sha256"] != certificate:
            raise ReleaseError("Release history does not match the registered release key")
        stem = release_stem(channel["version_name"], int(channel["version_code"]))
        apk = output / stem / (stem + ".apk")
        if not apk.is_file() or sha256_file(apk) != channel["apk_sha256"]:
            raise ReleaseError("Restore the previous release APK: it is missing or its checksum changed")
    except (OSError, ValueError, TypeError, KeyError) as error:
        raise ReleaseError("Cannot read release history; restore channel.json and the previous release") from error
    return apk, channel


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--config", type=Path, default=DEFAULT_CONFIG,
                        help="Registered release configuration (default: release.local.json)")
    parser.add_argument("--online", action="store_true", help="Allow Gradle to cache missing dependencies; otherwise build offline")
    device = parser.add_mutually_exclusive_group()
    device.add_argument("--device", help="Also run the isolated optimized JNI checks on this ADB serial")
    device.add_argument("--verify-native", metavar="SERIAL", help="Only verify optimized JNI; do not create a release")
    args = parser.parse_args(argv)
    config = read_config(args.config)
    base = args.config.resolve().parent
    def path_value(name, default=None):
        raw = config.get(name) or default
        if raw is None: return None
        path = Path(raw).expanduser()
        return (base / path).resolve() if not path.is_absolute() else path.resolve()
    keystore = path_value("keystore")
    alias = KEY_ALIAS
    output = path_value("outputDir", ROOT / "dist/releases")
    sdk = path_value("sdk", os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT")
                     or sdk_from_properties(ROOT))
    if not keystore.is_file():
        raise ReleaseError("The registered release key is missing; restore it from your private backup")
    if not sdk: raise ReleaseError("Configure sdk.dir in local.properties or ANDROID_HOME")
    tools = find_build_tools(sdk)
    keytool = shutil.which("keytool")
    java = shutil.which("java")
    if not keytool or not java: raise ReleaseError("Install the configured JDK and put java and keytool on PATH")
    signer_jar = tools["apksigner"].parent / "lib/apksigner.jar"
    if not signer_jar.is_file(): raise ReleaseError("The SDK build-tools apksigner.jar is missing")
    signer_command = [java, "-jar", signer_jar]
    # Invoke the checked-in wrapper JAR directly so paths never pass through .bat/cmd parsing on Windows.
    wrapper = [java, "-Xmx64m", "-Xms64m", "-classpath", ROOT / "gradle/wrapper/gradle-wrapper.jar",
               "org.gradle.wrapper.GradleWrapperMain"]
    revision = clean_revision(ROOT)
    native = ROOT / "third_party/espeak-ng"
    pin = run(["git", "ls-tree", revision, "third_party/espeak-ng"]).split()
    if len(pin) < 3 or pin[0] != "160000": raise ReleaseError("Initialize the pinned eSpeak Git submodule")
    native_revision = clean_revision(native)
    if pin[2] != native_revision: raise ReleaseError("The eSpeak checkout does not match the source pin")
    env = password_environment()
    certificate = hashlib.sha256(run([keytool, "-exportcert", "-keystore", keystore, "-alias", alias,
                                     "-storepass:env", "GHOSTWRITER_STORE_PASSWORD"], env=env, binary=True)).hexdigest()
    def inspect(apk): return parse_badging(run([tools["aapt"], "dump", "badging", apk]))
    def signer(apk): return parse_certificate(run([*signer_command, "verify", "--print-certs", apk]))
    check_certificate(certificate, config)
    previous, channel = previous_release(output, certificate)
    previous_info = inspect(previous) if previous else None
    previous_certificate = signer(previous) if previous else None
    if previous and (previous_info.package_name != APPLICATION_ID or previous_certificate != certificate):
        raise ReleaseError("The previous APK does not match the registered release identity")
    print("Signing certificate SHA-256:", certificate, flush=True)
    print("Running release checks…", flush=True)
    run([sys.executable, "-m", "unittest", "discover", "-s", "scripts", "-p", "test_*.py"], stream=True)
    native_tests = None
    serial = args.device or args.verify_native
    if serial:
        device_env = verification_environment(env, keystore)
        device_env["ANDROID_SERIAL"] = serial
        run([*wrapper, *([] if args.online else ["--offline"]), "--no-configuration-cache", "-Pghostwriter.releaseCheck=true",
             ":app:connectedReleaseCheckAndroidTest", "--console=plain", "-Dorg.gradle.logging.level=lifecycle"],
            env=device_env, stream=True)
        native_tests = verify_native_report()
        print(f"Optimized JNI check: {native_tests} tests passed", flush=True)
    if args.verify_native:
        return 0
    run([*wrapper, *([] if args.online else ["--offline"]), ":app:testDebugUnitTest", ":app:lintRelease",
         ":app:assembleRelease", "--console=plain", "-Dorg.gradle.logging.level=lifecycle"], stream=True)
    if clean_revision(ROOT) != revision or clean_revision(native) != native_revision:
        raise ReleaseError("Source revision changed while building; rebuild from one clean revision")
    unsigned = ROOT / "app/build/outputs/apk/release/app-release-unsigned.apk"
    mapping = ROOT / "app/build/outputs/mapping/release/mapping.txt"
    if not unsigned.is_file() or not mapping.is_file() or "# compiler: R8" not in mapping.read_text(encoding="utf-8"):
        raise ReleaseError("An unsigned optimized release APK and its R8 mapping are required")
    info = inspect(unsigned)
    check_update(info, certificate, previous_info, previous_certificate)
    check_apk_payload(unsigned)
    channel_file = output / "channel.json"
    check_channel(info, certificate, channel)
    stem = release_stem(info.version_name, info.version_code)
    destination = output / stem
    if destination.exists(): raise ReleaseError("This release directory already exists; increase versionCode for the next release")
    output.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix=".release-", dir=output) as temporary:
        work = Path(temporary)
        package = work / "package"; package.mkdir()
        aligned = work / "aligned.apk"
        apk = package / (stem + ".apk")
        run([tools["zipalign"], "-P", "16", "-f", "4", unsigned, aligned])
        run([*signer_command, "sign", "--ks", keystore, "--ks-key-alias", alias,
             "--ks-pass", "env:GHOSTWRITER_STORE_PASSWORD", "--key-pass", "env:GHOSTWRITER_KEY_PASSWORD",
             "--v1-signing-enabled", "true", "--v2-signing-enabled", "true", "--v3-signing-enabled", "true",
             "--v4-signing-enabled", "false", "--out", apk, aligned], env=env)
        if signer(apk) != certificate: raise ReleaseError("Signed APK certificate does not match the selected signing key")
        run([tools["zipalign"], "-c", "-P", "16", "4", apk])
        if inspect(apk) != info: raise ReleaseError("APK metadata changed during signing")
        source_name = stem + "-source.zip"
        source_archive(ROOT, native, revision, native_revision, package / source_name)
        metadata = {**asdict(info), "certificate_sha256": certificate, "app_revision": revision,
                    "espeak_revision": native_revision, "apk_sha256": sha256_file(apk),
                    "source_sha256": sha256_file(package / source_name), "optimized": True,
                    "previous_version_code": previous_info.version_code if previous_info else None,
                    "optimized_jni_tests": native_tests}
        (package / "RELEASE.json").write_text(json.dumps(metadata, indent=2) + "\n", encoding="utf-8")
        instructions = f"""Ghostwriter {info.version_name} (version code {info.version_code})

INSTALL / UPDATE
1. Extract this ZIP, then open {apk.name} on your Android device.
2. If asked, allow your trusted file manager/browser to install this APK.
3. Choose Install or Update. Keep the existing app installed to retain its data.
4. On first use, choose a lyric folder. Existing installations keep their data.

Requires Android {info.min_sdk} API level or later (API 24 = Android 7.0).
Includes 32-bit/64-bit ARM and x86 libraries in one universal APK.
Device management or installation policies can still restrict sideloading.
If Android reports a signing conflict, ask the sender for the APK signed with
your existing app's key. Uninstalling removes app-local beats and drafts.

VERIFY
SHA256SUMS lists checksums for the APK, matching source, notices, and this guide.
RELEASE.json records the signing fingerprint, versions, ABIs, and source revisions.

SOURCE AND NOTICES
{source_name} contains this exact app source AND the pinned eSpeak native source.
LICENSE and THIRD_PARTY_LICENSES.md describe the included software and data.
Extract the source ZIP and follow documentation/SETUP_NOTES.md to rebuild.
Downloaded dictionaries are optional, separate downloads; installed data works offline.
"""
        (package / "INSTALL.txt").write_text(instructions, encoding="utf-8")
        for name in ("LICENSE", "THIRD_PARTY_LICENSES.md"): shutil.copyfile(ROOT / name, package / name)
        members = [apk.name, source_name, "INSTALL.txt", "RELEASE.json", "LICENSE", "THIRD_PARTY_LICENSES.md"]
        bundle_zip(package, stem + ".zip", members)
        shutil.copyfile(mapping, package / "mapping.txt")
        package.rename(destination)
    history = output / ".channel.json.tmp"
    history.write_text(json.dumps(metadata, indent=2) + "\n", encoding="utf-8")
    history.replace(channel_file)
    print("Finished:", destination / (stem + ".zip"))
    print("Keep the release key, password, release.local.json, channel.json, and mapping.txt for future releases.")
    return 0


if __name__ == "__main__":
    try: sys.exit(main())
    except (ReleaseError, ValueError, OSError, zipfile.BadZipFile) as error:
        print("Release stopped:", error, file=sys.stderr)
        sys.exit(1)
