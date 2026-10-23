"""The one registered release identity; passwords stay in the terminal/process."""
import getpass
import json
import os
from pathlib import Path
import re
import sys

from release_apk import ReleaseError

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_CONFIG = ROOT / "release.local.json"
DEFAULT_KEYSTORE = Path.home() / ".ghostwriter/signing/release.p12"
KEY_ALIAS = "ghostwriter-release"


def read_config(path):
    config_path = Path(path).resolve()
    try:
        config = json.loads(config_path.read_text(encoding="utf-8"))
    except (OSError, ValueError) as error:
        raise ReleaseError("Run python3 scripts/setup_signing.py once to create the release key and configuration") from error
    allowed = {"keystore", "keyAlias", "certificateSha256", "sdk", "outputDir"}
    if not isinstance(config, dict) or set(config) - allowed or any(not isinstance(v, str) for v in config.values()):
        raise ReleaseError("Release configuration accepts only registered signing metadata and paths, never passwords")
    if config.get("keyAlias") != KEY_ALIAS or not re.fullmatch(r"[0-9a-f]{64}", config.get("certificateSha256", "")):
        raise ReleaseError("Use the release configuration created by scripts/setup_signing.py")
    raw = config.get("keystore")
    if not raw:
        raise ReleaseError("The registered release keystore path is missing")
    key = Path(raw).expanduser()
    key = key.resolve() if key.is_absolute() else (config_path.parent / key).resolve()
    if key.is_relative_to(ROOT) or key.suffix.lower() != ".p12":
        raise ReleaseError("Keep the registered PKCS#12 release key outside the repository")
    return config


def password_environment():
    env = os.environ.copy()
    password = env.get("GHOSTWRITER_STORE_PASSWORD")
    if not password:
        if not sys.stdin.isatty():
            raise ReleaseError("Run in a terminal to enter the release password, or set GHOSTWRITER_STORE_PASSWORD privately")
        password = getpass.getpass("Release signing password: ")
    if not password:
        raise ReleaseError("The release signing password cannot be empty")
    env["GHOSTWRITER_STORE_PASSWORD"] = password
    env["GHOSTWRITER_KEY_PASSWORD"] = password
    return env


def check_certificate(actual, config):
    if actual != config["certificateSha256"]:
        raise ReleaseError("The keystore does not match the registered release certificate; restore the original release key")


def verification_environment(env, keystore):
    result = env.copy()
    result["GHOSTWRITER_RELEASE_KEYSTORE"] = str(keystore)
    result["GHOSTWRITER_RELEASE_KEY_ALIAS"] = KEY_ALIAS
    return result
