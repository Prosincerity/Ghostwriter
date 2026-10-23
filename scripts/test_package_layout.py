"""Keep feature ownership and dependency boundaries part of repository checks."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
BASE = "com.prosincerity.ghostwriter"
FEATURES = {"about", "core", "dictionary", "editor", "player", "projects",
            "settings", "typography", "waveform"}


class PackageLayoutTest(unittest.TestCase):
    def test_source_packages_match_feature_directories(self):
        for source_set in ("main", "test", "androidTest"):
            root = ROOT / "app/src" / source_set / "java"
            for source in root.rglob("*"):
                if source.suffix not in {".kt", ".java"}:
                    continue
                with self.subTest(source=source.relative_to(ROOT)):
                    expected = ".".join(source.relative_to(root).parts[:-1])
                    package = re.search(r"^package ([\w.]+)", source.read_text(), re.M)
                    self.assertIsNotNone(package)
                    self.assertEqual(expected, package[1])
                    suffix = expected.removeprefix(BASE).strip(".")
                    if suffix:
                        self.assertIn(suffix.split(".")[0], FEATURES)

    def test_feature_dependencies_are_acyclic_and_respect_ownership(self):
        edges = {feature: set() for feature in FEATURES}
        root = ROOT / "app/src/main/java/com/prosincerity/ghostwriter"
        for source in root.rglob("*.kt"):
            relative = source.relative_to(root)
            if len(relative.parts) == 1:  # App navigation composes the features.
                continue
            feature = relative.parts[0]
            references = re.findall(re.escape(BASE) + r"\.(\w+)\.", source.read_text())
            edges[feature].update(target for target in references
                                  if target in FEATURES and target != feature)
        self.assertFalse(edges["waveform"] & {"player", "projects", "editor"})
        self.assertFalse(edges["player"] & {"projects", "editor"})

        def visit(feature, path):
            self.assertNotIn(feature, path, "Feature cycle: " + " -> ".join(path + [feature]))
            for dependency in sorted(edges[feature]):
                visit(dependency, path + [feature])

        for feature in sorted(FEATURES):
            visit(feature, [])


if __name__ == "__main__":
    unittest.main()
