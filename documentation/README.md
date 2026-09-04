# Gh0stwrit3r.exe — Documentation

Welcome to the project documentation directory for **Gh0stwrit3r.exe**. All design documents, architecture specifications, feature roadmaps, and setup instructions live in this directory.

## Documentation Index

- **[FEATURES.md](FEATURES.md)**  
  The single source of truth for the phased feature roadmap, what features are currently implemented (MVP notepad, autosave rotation), what is planned next (offline beat player, global instrumentals folder, project metadata, syllable/bar counters, rhyme detection), and non-goals (strict no-AI, no-bloat policy).

- **[AGENT_CONTEXT.md](AGENT_CONTEXT.md)**  
  Comprehensive developer and AI-assistant handoff document. Details:
  - Non-negotiable project philosophies (No AI, zero added bloat, AOSP-only with no Google Play Services, Compose + Material 3).
  - Tech stack specifications (Kotlin, Compose BOM, Android Gradle Plugin, AOSP built-in `MediaPlayer`, built-in `org.json`).
  - Architecture breakdown of all current screens (`HomeScreen`, `EditorScreen`, `SettingsScreen`) and data handlers (`Settings`, `ProjectStorage`).
  - Offline Beat Player design and lifecycle management.
  - Beat storage architecture (self-contained project folders and global `Music/Gh0stwrit3r/Instrumentals/` folder).
  - Project metadata specification (`project.json`).
  - Deliberate architectural decisions and test coverage.

- **[SETUP_NOTES.md](SETUP_NOTES.md)**  
  Historical setup and bootstrapping notes for initializing the project within Android Studio.
