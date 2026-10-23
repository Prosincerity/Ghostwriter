#!/usr/bin/env python3
"""Create the dedicated encrypted release key and register it once."""
import argparse
import getpass
import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile

from release_signing import ROOT, DEFAULT_CONFIG, DEFAULT_KEYSTORE, KEY_ALIAS
PROTECTION = [
    "-J-Dkeystore.pkcs12.keyProtectionAlgorithm=PBEWithHmacSHA256AndAES_256",
    "-J-Dkeystore.pkcs12.certProtectionAlgorithm=PBEWithHmacSHA256AndAES_256",
    "-J-Dkeystore.pkcs12.macAlgorithm=HmacPBESHA256",
    "-J-Dkeystore.pkcs12.keyPbeIterationCount=600000",
    "-J-Dkeystore.pkcs12.certPbeIterationCount=600000",
    "-J-Dkeystore.pkcs12.macIterationCount=600000",
]


def invoke(command, env):
    result = subprocess.run([str(arg) for arg in command], env=env, capture_output=True)
    if result.returncode:
        raise ValueError("Keytool failed. Check the JDK and password; no existing release key was changed.")
    return result.stdout


def signing_command(keytool, destination, alias):
    return [keytool, *PROTECTION, "-genkeypair", "-keystore", destination, "-storetype", "PKCS12",
            "-alias", alias, "-keyalg", "RSA", "-keysize", "3072", "-sigalg", "SHA256withRSA",
            "-validity", "10000", "-dname", "CN=Ghostwriter Release",
            "-storepass:env", "GHOSTWRITER_NEW_PASSWORD", "-keypass:env", "GHOSTWRITER_NEW_PASSWORD"]


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--keystore", type=Path, default=DEFAULT_KEYSTORE,
                        help="Private destination outside the repository (default: ~/.ghostwriter/signing/release.p12)")
    parser.add_argument("--config", type=Path, default=DEFAULT_CONFIG)
    args = parser.parse_args(argv)
    destination = args.keystore.expanduser().resolve()
    config_path = args.config.expanduser().resolve()
    if destination.is_relative_to(ROOT) or destination.suffix.lower() != ".p12":
        raise ValueError("Choose a private .p12 keystore path outside the repository")
    if destination.exists() or config_path.exists():
        raise ValueError("Release key or configuration already exists; retain it and use scripts/build_release.py. Nothing was overwritten.")
    if not sys.stdin.isatty():
        raise ValueError("Run setup in a local terminal to choose the password privately")
    keytool = shutil.which("keytool")
    if not keytool: raise ValueError("Put the configured JDK's keytool on PATH")
    password = getpass.getpass("New strong keystore/private-key password (at least 16 characters): ")
    if len(password) < 16 or getpass.getpass("Confirm the new password: ") != password:
        raise ValueError("Use a matching strong password of at least 16 characters")
    env = os.environ.copy()
    env["GHOSTWRITER_NEW_PASSWORD"] = password
    old_mask = os.umask(0o077)
    try:
        destination.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
        config_path.parent.mkdir(parents=True, exist_ok=True)
        with tempfile.TemporaryDirectory(prefix=".ghostwriter-signing-", dir=destination.parent) as directory:
            staged = Path(directory) / "signing.p12"
            invoke(signing_command(keytool, staged, KEY_ALIAS), env)
            fingerprint = hashlib.sha256(invoke([keytool, "-exportcert", "-keystore", staged,
                "-alias", KEY_ALIAS, "-storepass:env", "GHOSTWRITER_NEW_PASSWORD"], env)).hexdigest()
            with destination.open("xb") as output, staged.open("rb") as input_file:
                shutil.copyfileobj(input_file, output)
                output.flush()
                os.fsync(output.fileno())
            destination.chmod(0o600)
            config = {"keystore": str(destination), "keyAlias": KEY_ALIAS,
                      "certificateSha256": fingerprint, "outputDir": "dist/releases"}
            with config_path.open("x", encoding="utf-8") as output:
                json.dump(config, output, indent=2)
                output.write("\n")
                output.flush()
                os.fsync(output.fileno())
    finally:
        os.umask(old_mask)
    print("Encrypted signing store:", destination)
    print("Certificate SHA-256:", fingerprint)
    print("Registered release configuration:", config_path)
    print("Store the password in your password manager and back up the key and configuration privately.")
    print("No password file was written. Generate this identity only once.")
    print("Next: python3 scripts/build_release.py")
    return 0


if __name__ == "__main__":
    try: sys.exit(main())
    except (ValueError, OSError) as error:
        print("Signing setup stopped:", error, file=sys.stderr)
        sys.exit(1)
