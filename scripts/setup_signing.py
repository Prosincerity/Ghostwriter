#!/usr/bin/env python3
"""Create an encrypted signing store, or re-encrypt an existing update identity."""
import argparse
import getpass
import hashlib
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile

ROOT = Path(__file__).resolve().parents[1]
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
        raise ValueError("Keytool failed. Check the source alias/password and JDK; the original keystore is unchanged.")
    return result.stdout


def signing_command(keytool, destination, alias, source=None, source_alias=None):
    if source:
        return [keytool, *PROTECTION, "-importkeystore", "-noprompt", "-srckeystore", source,
                "-srcalias", source_alias, "-srcstorepass:env", "GHOSTWRITER_SOURCE_STORE_PASSWORD",
                "-srckeypass:env", "GHOSTWRITER_SOURCE_KEY_PASSWORD", "-destkeystore", destination,
                "-deststoretype", "PKCS12", "-destalias", alias,
                "-deststorepass:env", "GHOSTWRITER_NEW_PASSWORD", "-destkeypass:env", "GHOSTWRITER_NEW_PASSWORD"]
    return [keytool, *PROTECTION, "-genkeypair", "-keystore", destination, "-storetype", "PKCS12",
            "-alias", alias, "-keyalg", "RSA", "-keysize", "3072", "-sigalg", "SHA256withRSA",
            "-validity", "10000", "-dname", "CN=Ghostwriter",
            "-storepass:env", "GHOSTWRITER_NEW_PASSWORD", "-keypass:env", "GHOSTWRITER_NEW_PASSWORD"]


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    mode = parser.add_mutually_exclusive_group(required=True)
    mode.add_argument("--new-key", action="store_true", help="New identity; cannot directly update an old signing channel")
    mode.add_argument("--import-keystore", type=Path, help="Re-encrypt the original key without changing update identity")
    parser.add_argument("--source-alias")
    parser.add_argument("--keystore", type=Path, required=True)
    parser.add_argument("--alias", default="ghostwriter")
    args = parser.parse_args(argv)
    destination = args.keystore.expanduser().resolve()
    if destination.is_relative_to(ROOT) or destination.exists():
        raise ValueError("Choose a new private keystore path outside the repository; existing files are never overwritten")
    source = args.import_keystore.expanduser().resolve() if args.import_keystore else None
    if source and (not source.is_file() or not args.source_alias):
        raise ValueError("Import requires the existing source keystore and its --source-alias")
    keytool = shutil.which("keytool")
    if not keytool: raise ValueError("Put the configured JDK's keytool on PATH")
    password = getpass.getpass("New strong keystore/private-key password (at least 16 characters): ")
    if len(password) < 16 or getpass.getpass("Confirm the new password: ") != password:
        raise ValueError("Use a matching strong password of at least 16 characters")
    env = os.environ.copy()
    env["GHOSTWRITER_NEW_PASSWORD"] = password
    def certificate(path, alias, password_variable):
        return hashlib.sha256(invoke([keytool, "-exportcert", "-keystore", path, "-alias", alias,
                                      "-storepass:env", password_variable], env)).hexdigest()
    previous = None
    if source:
        env["GHOSTWRITER_SOURCE_STORE_PASSWORD"] = getpass.getpass("Source keystore password: ")
        key_password = getpass.getpass("Source key password (Enter to reuse source keystore password): ")
        env["GHOSTWRITER_SOURCE_KEY_PASSWORD"] = key_password or env["GHOSTWRITER_SOURCE_STORE_PASSWORD"]
        previous = certificate(source, args.source_alias, "GHOSTWRITER_SOURCE_STORE_PASSWORD")
    destination.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
    old_mask = os.umask(0o077)
    try:
        with tempfile.TemporaryDirectory(prefix=".ghostwriter-signing-", dir=destination.parent) as directory:
            staged = Path(directory) / "signing.p12"
            invoke(signing_command(keytool, staged, args.alias, source, args.source_alias), env)
            fingerprint = certificate(staged, args.alias, "GHOSTWRITER_NEW_PASSWORD")
            if previous and fingerprint != previous:
                raise ValueError("Imported certificate differs from the original; refusing to publish a changed signing identity")
            with destination.open("xb") as output, staged.open("rb") as input_file:
                shutil.copyfileobj(input_file, output)
                output.flush()
                os.fsync(output.fileno())
            destination.chmod(0o600)
    finally:
        os.umask(old_mask)
    print("Encrypted signing store:", destination)
    print("Certificate SHA-256:", fingerprint)
    if previous: print("The original signing certificate is preserved; updates remain compatible.")
    print("Store the password in your password manager and back up the keystore privately. No password file was written.")
    return 0


if __name__ == "__main__":
    try: sys.exit(main())
    except (ValueError, OSError) as error:
        print("Signing setup stopped:", error, file=sys.stderr)
        sys.exit(1)
