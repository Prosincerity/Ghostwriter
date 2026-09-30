# Graph Report - Gh0stwrit3r  (2026-09-30)

## Corpus Check
- 320 files · ~548,739 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2055 file(s) not represented in the graph (top: (none) 1892, .xml 82, .test 17)

## Summary
- 3538 nodes · 8991 edges · 206 communities (163 shown, 43 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 1316 edges (avg confidence: 0.84)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `52d54128`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- Android
- synthesize.c
- ProjectMetadata
- ProjectStorageTest
- WaveformViewport
- SettingsScreen.kt
- .writeText
- WaveformExtractor
- SystemFontFile
- VoiceSettingsTest
- gradlew
- DictionaryDownloads.kt
- WaveformView.kt
- What You Must Do When Invoked
- CheckVoiceData
- formatPlaybackTime
- BeatPlaybackService.kt
- graphify reference: extra exports and benchmark
- Ghostwriter
- graphify reference: query, path, explain
- graphify reference: add a URL and watch a folder
- graphify reference: commit hook and native CLAUDE.md integration
- graphify reference: incremental update and cluster-only
- graphify reference: GitHub clone and cross-repo merge
- graphify reference: transcribe video and audio
- extraction-spec.md
- ProjectStorageBeatTest
- EditorScreen
- TranslateClauseWithTerminator
- compiledata.c
- .fromIpa
- Ghostwriter agent instructions
- math.h
- DictionaryInstaller.kt
- TextToSpeechTestCase
- DictionaryScreen
- SpeechSynthesis
- speech.c
- tests/encoding.c
- t_espeak_command
- TestConnection
- composable
- event.c
- wavegen.c
- demo.js
- eSpeakActivity
- .excludedWords
- ttsengine.cpp
- LyricsNotepadTest.kt
- ucd_properties
- mbrowrap.c
- MainActivityTest.kt
- tests/readclause.c
- espeak_Initialize
- Diacritics
- ImportVoicePreference.java
- tr_languages.c
- ucd.h
- MainActivity.kt
- Phoneme Model
- eSpeakService.c
- BeatPlayerPanel.kt
- Language Attributes
- ProjectLyricsStorage
- PlaybackFocusState
- FrameManagerImpl
- klatt.c
- DictionaryScreen.kt
- Diacritics
- espeak_ng_Initialize
- DictionarySearch
- speechPlayer.cpp
- TtsService
- compile_line
- .isTtsLangCode
- EditorScreen.kt
- index.md
- SSML (Speech Synthesis Markup Language)
- .failedAssetCopyCleansStagingAndPreservesExistingData
- Row
- numbers.md
- .parse
- speechWaveGenerator.cpp
- VoiceVariant
- voices.c
- sPlayer.c
- espeak_api.c
- intonation.c
- PlaybackNotificationUpdater
- TranslateWord3
- Phoneme Instructions
- CodePoint
- SeekBarPreference
- Callback
- TtsService.java
- withDictionaryTestContext
- printdata.py
- ucd.py
- utf8_in
- Tune Definitions
- X-SAMPA Transcription Scheme
- SpeechWaveGeneratorImpl
- BeatPlaybackService
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- BeatPlayerInstrumentedTest
- HomeScreen.kt
- emoji
- setlengths.c
- ucd_lookup_category
- uprintf
- Resonator
- Change Log
- DictionaryDownloadsTest.kt
- ProjectStorage
- DictionaryHeadword
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- test
- Third-party software and dictionary data
- Third-party software and dictionary data
- GetFileLength
- Consonants
- printucddata_cpp.cpp
- Vowels
- unpackDictionaryArchive
- Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability.
- GhostwriterTheme
- common
- android.content.Context
- ProcessSsmlTag
- Lookup
- .excludedWords
- Unicode Character Database Tools
- EditorScreenTest.kt
- eSpeak NG user guide
- ContextWrapper
- create_dict_corpus_file.py
- Translation fuzzers
- mutablestateof
- synthdata.c
- SelectPhonemeTable
- espeak_callback
- android/gradlew
- CheckVoiceDataTest
- Using eSpeak NG as a library
- espeak-ng
- Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?
- SettingsScreen
- void
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- InputStream
- AnnouncePunctuation
- Dictionaries
- rgroup_sorter
- Architecture
- ucd_lookup_category_group
- BeatPlaybackServiceTest
- GhostwriterApp
- .plan
- DictionaryArchive.kt
- row
- EspeakIpaInstrumentedTest
- Contribution Guide
- httpurlconnection
- Settings
- Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?
- Dictionary data license and attribution
- eSpeakNGWorker
- Build setup
- Native pronunciation engine
- Testing
- CloseWavFile
- Diacritics
- Features and roadmap
- Releasing
- utf8_out
- ParallelFormantGenerator
- DictionarySearchMode
- getopt.c
- SpeakPunctuationPreference
- LyricsNotepad
- WaveformWarningTest.kt
- ProjectInfoBpmTest.kt
- espeak_SetUriCallback
- espeakng_glue.cpp

## God Nodes (most connected - your core abstractions)
1. `BeatPlayer` - 51 edges
2. `GhostwriterTheme()` - 50 edges
3. `SpeechSynthesis` - 49 edges
4. `ucd_properties()` - 38 edges
5. `ProjectStorage` - 33 edges
6. `BeatPlaybackService` - 33 edges
7. `LyricTextSettings` - 31 edges
8. `create_text_decoder()` - 31 edges
9. `text_decoder_eof()` - 31 edges
10. `espeak_Initialize()` - 31 edges

## Surprising Connections (you probably didn't know these)
- `Projects and settings` --references--> `LyricTextSettings`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/data/LyricTextSettings.kt
- `Planned` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `Planned` --references--> `EditorScreen()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/screens/EditorScreen.kt
- `Installing` --references--> `eSpeakActivity`  [INFERRED]
  third_party/espeak-ng/docs/building.md → third_party/espeak-ng/android/src/com/reecedunn/espeak/eSpeakActivity.java
- `License Information` --references--> `getopt_long()`  [INFERRED]
  third_party/espeak-ng/README.md → third_party/espeak-ng/src/compat/getopt.c

## Import Cycles
- None detected.

## Communities (206 total, 43 thin omitted)

### Community 1 - "Android"
Cohesion: 0.10
Nodes (20): Android, Building, Building, Building eSpeak NG, Building with Gradle, Cross Compilation, Dependencies, Dependencies (+12 more)

### Community 2 - "synthesize.c"
Cohesion: 0.12
Nodes (42): FILE, PHONEME_LIST, PHONEME_TAB, GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), AdjustFormants(), AllocFrame() (+34 more)

### Community 3 - "ProjectMetadata"
Cohesion: 0.14
Nodes (10): ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, WaveformMarkerDialog(), EditorMarkerDialogs(), ProjectInfoDialog(), trimmedOrNull() (+2 more)

### Community 6 - "SettingsScreen.kt"
Cohesion: 0.11
Nodes (25): alertdialog, ReassignBeatDialog(), column, contentdescription, dp, dpsize, ghostbuttonshape, height (+17 more)

### Community 7 - ".writeText"
Cohesion: 0.16
Nodes (4): IntArray, StagedFileWriter, IntArray, WaveformCache

### Community 8 - "WaveformExtractor"
Cohesion: 0.11
Nodes (5): WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest

### Community 9 - "SystemFontFile"
Cohesion: 0.14
Nodes (13): SystemFontCatalogTest, LyricFont, lyricFontFromPreference(), SystemFontFile, systemFontOptions(), SystemFontCatalog, LyricFontTest, assertnotequals (+5 more)

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "DictionaryDownloads.kt"
Cohesion: 0.16
Nodes (18): AboutLink(), AboutScreen(), AboutSectionTitle(), DictionaryDownloads(), DictionaryDownloadsScreen(), downloadSourceLabel(), Modifier, arrowback (+10 more)

### Community 14 - "WaveformView.kt"
Cohesion: 0.08
Nodes (24): ceil, cliptobounds, detectdraggestures, detecttapgestures, detecttransformgestures, drawtext, fillmaxheight, floor (+16 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "CheckVoiceData"
Cohesion: 0.14
Nodes (9): android.os.AsyncTask, android.widget.ProgressBar, CheckVoiceData, Override, AsyncExtract, DownloadVoiceData, ExtractProgress, Override (+1 more)

### Community 19 - "formatPlaybackTime"
Cohesion: 0.20
Nodes (5): LongBeatWarningDialog(), formatPlaybackTime(), formatInterval(), formatTypographyNumber(), SettingsFormatTest

### Community 20 - "BeatPlaybackService.kt"
Cohesion: 0.10
Nodes (18): Context, IBinder, Intent, LocalBinder, AudioFocusRequest, AudioManager, Binder, bitmap (+10 more)

### Community 21 - "graphify reference: extra exports and benchmark"
Cohesion: 0.22
Nodes (8): graphify reference: extra exports and benchmark, Step 6b - Wiki (only if --wiki flag), Step 7 - Neo4j export (only if --neo4j or --neo4j-push flag), Step 7a - FalkorDB export (only if --falkordb or --falkordb-push flag), Step 7b - SVG export (only if --svg flag), Step 7c - GraphML export (only if --graphml flag), Step 7d - MCP server (only if --mcp flag), Step 8 - Token reduction benchmark (only if total_words > 5000)

### Community 22 - "Ghostwriter"
Cohesion: 0.50
Nodes (4): Build and test, Documentation and contributions, Ghostwriter, License

### Community 23 - "graphify reference: query, path, explain"
Cohesion: 0.33
Nodes (5): For /graphify explain, For /graphify path, graphify reference: query, path, explain, Step 0 — Constrained query expansion (REQUIRED before traversal), Step 1 — Traversal

### Community 24 - "graphify reference: add a URL and watch a folder"
Cohesion: 0.50
Nodes (3): For /graphify add, For --watch, graphify reference: add a URL and watch a folder

### Community 25 - "graphify reference: commit hook and native CLAUDE.md integration"
Cohesion: 0.50
Nodes (3): For git commit hook, For native CLAUDE.md integration, graphify reference: commit hook and native CLAUDE.md integration

### Community 26 - "graphify reference: incremental update and cluster-only"
Cohesion: 0.50
Nodes (3): For --cluster-only, For --update (incremental re-extraction), graphify reference: incremental update and cluster-only

### Community 31 - "EditorScreen"
Cohesion: 0.30
Nodes (5): EditorScreenTest, IntArray, EditorScreen(), PendingBeatPreparation, MutableState

### Community 32 - "TranslateClauseWithTerminator"
Cohesion: 0.19
Nodes (19): Translator, is_str_totally_null(), IsSpace(), towlower2(), InitGroups(), PHONEME_LIST2, Translator, WORD_TAB (+11 more)

### Community 33 - "compiledata.c"
Cohesion: 0.08
Nodes (76): phoneme_feature_t, PHONEME_TAB_LIST, FILE, Read4Bytes(), StringToWord(), CompileContext, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS (+68 more)

### Community 34 - ".fromIpa"
Cohesion: 0.15
Nodes (3): IpaSearchKeys, Keys, IpaSearchKeysTest

### Community 35 - "Ghostwriter agent instructions"
Cohesion: 0.40
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 36 - "math.h"
Cohesion: 0.14
Nodes (15): ieee80, SpectFrame, SpectSeq, ieee_extended_to_double(), espeak_ng_STATUS, FILE, GetFrameLength(), LoadFrame() (+7 more)

### Community 37 - "DictionaryInstaller.kt"
Cohesion: 0.16
Nodes (12): DictionaryInstaller, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK, WIKTIONARY, Context, HttpURLConnection (+4 more)

### Community 38 - "TextToSpeechTestCase"
Cohesion: 0.16
Nodes (10): android.annotation.SuppressLint, android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, TextToSpeechTest, Override, SuppressWarnings, TextToSpeechTestCase, Exception (+2 more)

### Community 39 - "DictionaryScreen"
Cohesion: 0.33
Nodes (4): DictionaryScreenTest, DictionarySearchResult, dictionaryLanguageLabel(), DictionaryScreen()

### Community 40 - "SpeechSynthesis"
Cohesion: 0.06
Nodes (9): android.content.SharedPreferences, editable, jsonexception, jsonobject, org.json.JSONObject, preferencemanager, SpeechSynthesisTest, SpeechSynthesis (+1 more)

### Community 41 - "speech.c"
Cohesion: 0.11
Nodes (22): audio, encoding, errno, espeak_ng, fcntl, io, libgen, limits (+14 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.14
Nodes (60): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), espeak_ng_ENCODING (+52 more)

### Community 43 - "t_espeak_command"
Cohesion: 0.09
Nodes (39): espeak_ng_OUTPUT_MODE, espeak_PARAMETER, espeak_POSITION_TYPE, espeak_VOICE, t_espeak_command, wchar_t, create_espeak_char(), create_espeak_key() (+31 more)

### Community 44 - "TestConnection"
Cohesion: 0.12
Nodes (11): FilterInputStream, openDictionaryArchive(), FilterInputStream, DictionaryArchiveSourceTest, ByteArrayInputStream, InputStream, ByteArrayInputStream, ByteArrayInputStream (+3 more)

### Community 45 - "composable"
Cohesion: 0.14
Nodes (12): Modifier, composable, darkcolorscheme, fillmaxwidth, issystemindarktheme, keyboardcapitalization, keyboardoptions, lightcolorscheme (+4 more)

### Community 46 - "event.c"
Cohesion: 0.18
Nodes (21): add_time_in_ms(), espeak_EVENT, espeak_ng_STATUS, clock_gettime2(), event_clear_all(), event_copy(), event_declare(), event_delete() (+13 more)

### Community 47 - "wavegen.c"
Cohesion: 0.08
Nodes (43): espeak_ng_OUTPUT_HOOKS, sonic, ESPEAK_NG_API, espeak_ng_SetRandSeed(), espeak_rand(), espeak_srand(), GetFrameRms(), MarkerEvent() (+35 more)

### Community 48 - "demo.js"
Cohesion: 0.06
Nodes (28): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+20 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.08
Nodes (23): android.content.BroadcastReceiver, android.content.Intent, android.os.Bundle, android.os.Handler, android.os.Message, android.view.Menu, android.view.MenuItem, java.lang.ref.WeakReference (+15 more)

### Community 50 - ".excludedWords"
Cohesion: 0.15
Nodes (7): AssonanceFormFilter, Entry, IntArray, PrefixNode, Pronounced, AssonanceFormFilterTest, Pronounced

### Community 51 - "ttsengine.cpp"
Cohesion: 0.10
Nodes (37): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+29 more)

### Community 52 - "LyricsNotepadTest.kt"
Cohesion: 0.08
Nodes (27): LyricFontFamily, CURSIVE, MONOSPACE, SANS_SERIF, SERIF, SYSTEM_DEFAULT, LyricTextAlignment, CENTER (+19 more)

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (27): poll, procfs, signal, stdarg, BOOL, close_mbrola(), close_pipes(), create_pipes() (+19 more)

### Community 55 - "MainActivityTest.kt"
Cohesion: 0.12
Nodes (28): activitynotfoundexception, assertisenabled, assertisnotenabled, asserttextcontains, before, context, contextwrapper, continuation (+20 more)

### Community 56 - "tests/readclause.c"
Cohesion: 0.15
Nodes (33): phoneme, readclause, speech, synthesize, CheckPhonemeMode(), clause_type_from_codepoint(), IsRomanU(), ReadClause() (+25 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.16
Nodes (31): espeak_AUDIO_OUTPUT, espeak_POSITION_TYPE, espeak_Initialize(), espeak_SetVoiceByName(), espeak_Synth(), t_espeak_callback, event_set_callback(), t_espeak_callback (+23 more)

### Community 58 - "Diacritics"
Cohesion: 0.08
Nodes (24): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Diacritics, Fortis and Lenis, Height (+16 more)

### Community 59 - "ImportVoicePreference.java"
Cohesion: 0.09
Nodes (30): adapterview, android.app.Activity, android.preference.DialogPreference, android.util.Pair, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter (+22 more)

### Community 60 - "tr_languages.c"
Cohesion: 0.06
Nodes (54): LANGUAGE_OPTIONS, Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation (+46 more)

### Community 61 - "ucd.h"
Cohesion: 0.15
Nodes (22): category, property, script, get_category_string(), get_script_string(), codepoint_t, isalnum(), isalpha() (+14 more)

### Community 62 - "MainActivity.kt"
Cohesion: 0.16
Nodes (13): Bundle, Context, MainActivity, openExternalLink(), ComponentActivity, enableedgetoedge, launch, localcontext (+5 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.19
Nodes (27): JavaVM, jboolean, jclass, jni, jstring, espeak_EVENT, jint, JNICALL (+19 more)

### Community 65 - "BeatPlayerPanel.kt"
Cohesion: 0.14
Nodes (14): WaveformZoomButton(), button, card, columnscope, delay, fillmaxsize, ImageVector, launchedeffect (+6 more)

### Community 66 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "klatt.c"
Cohesion: 0.16
Nodes (23): klatt_frame_ptr, resonator_ptr, speechplayer, antiresonator(), frame_t, voice_t, WGEN_DATA, DBtoLIN() (+15 more)

### Community 71 - "DictionaryScreen.kt"
Cohesion: 0.15
Nodes (13): PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, sourceLabel(), arrangement, dropdownmenu, dropdownmenuitem (+5 more)

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 73 - "espeak_ng_Initialize"
Cohesion: 0.14
Nodes (21): process_espeak_command(), LoadConfig(), InitNamedata(), InitText2(), espeak_ng_STATUS, SetParameter(), espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS (+13 more)

### Community 74 - "DictionarySearch"
Cohesion: 0.16
Nodes (8): PronunciationResult, DictionaryMatch, DictionarySearch, DictionarySearchData, DictionarySearchRows, InstalledDictionarySearchData, AssonanceData, DictionarySearchTest

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.15
Nodes (13): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+5 more)

### Community 76 - "TtsService"
Cohesion: 0.15
Nodes (6): android.speech.tts.SynthesisRequest, Override, SuppressWarnings, TtsService, Override, Voice

### Community 77 - "compile_line"
Cohesion: 0.17
Nodes (20): IsDigit(), isspace2(), CompileContext, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context() (+12 more)

### Community 78 - ".isTtsLangCode"
Cohesion: 0.27
Nodes (3): Override, TextToSpeechServiceTest, TtsServiceTest

### Community 79 - "EditorScreen.kt"
Cohesion: 0.13
Nodes (17): activityresultcontracts, displayNameFor(), Context, atomicboolean, atomiclong, book, coroutinestart, dispatchers (+9 more)

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - ".failedAssetCopyCleansStagingAndPreservesExistingData"
Cohesion: 0.29
Nodes (4): ContextWrapper, Context, ContextWrapper, AssetManager

### Community 83 - "Row"
Cohesion: 0.32
Nodes (4): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations, SQLiteDatabase

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.18
Nodes (5): java.util.regex.Pattern, VoiceVariantTest, VariantData, Override, VoiceVariant

### Community 88 - "voices.c"
Cohesion: 0.10
Nodes (43): dirent, main(), strncpy0(), EncodePhonemes(), MNEM_TAB, Translator, CheckTranslator(), LoadLanguageOptions() (+35 more)

### Community 89 - "sPlayer.c"
Cohesion: 0.18
Nodes (18): KlattInit(), KlattReset(), frame_t, sample, speechPlayer_frame_t, voice_t, WGEN_DATA, fillSpeechPlayerFrame() (+10 more)

### Community 90 - "espeak_api.c"
Cohesion: 0.17
Nodes (20): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, ESPEAK_API, espeak_ng_STATUS, espeak_PARAMETER, espeak_VOICE (+12 more)

### Community 91 - "intonation.c"
Cohesion: 0.27
Nodes (16): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+8 more)

### Community 92 - "PlaybackNotificationUpdater"
Cohesion: 0.33
Nodes (3): PlaybackNotificationUpdater, PlaybackNotificationUpdaterTest, TestQueue

### Community 93 - "TranslateWord3"
Cohesion: 0.15
Nodes (26): IsAlpha(), IsBracket(), AppendPhonemes(), GetVowelStress(), SetWordStress(), TranslateRules(), IsSuperscript(), SetSpellingStress() (+18 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.06
Nodes (33): Attributes, CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, Conditional Statements (+25 more)

### Community 96 - "SeekBarPreference"
Cohesion: 0.09
Nodes (10): android.content.DialogInterface, android.widget.SeekBar, OnSeekBarChangeListener, Override, SeekBarPreference, Parameter, UnitType, Percentage (+2 more)

### Community 98 - "TtsService.java"
Cohesion: 0.09
Nodes (30): android.speech.tts.SynthesisCallback, android.speech.tts.TextToSpeechService, android.test.AndroidTestCase, anyof, arraylist, assertthat, audiotrack, bundle (+22 more)

### Community 99 - "withDictionaryTestContext"
Cohesion: 0.20
Nodes (10): DictionaryInstallerInstrumentedTest, ByteArrayInputStream, ByteArrayInputStream, dictionaryArchive(), ByteArray, T, withDictionaryTestContext(), DictionaryDownloadsTest (+2 more)

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 101 - "ucd.py"
Cohesion: 0.23
Nodes (4): os, sys, parse_property_mapping(), parse_ucd_data()

### Community 102 - "utf8_in"
Cohesion: 0.14
Nodes (24): MatchRecord, utf8_in(), utf8_in2(), DecodeRule(), print_dictionary_flags(), Translator, WORD_TAB, DollarRule() (+16 more)

### Community 103 - "Tune Definitions"
Cohesion: 0.15
Nodes (13): Clauses, endtune, head, headenv, headextend, headlast, Intonation, nucleus (+5 more)

### Community 104 - "X-SAMPA Transcription Scheme"
Cohesion: 0.14
Nodes (14): Consonants, Intonation, Length, Manner of Articulation, Other Symbols, Other Symbols, Phoneme Transcription Schemes, References (+6 more)

### Community 105 - "SpeechWaveGeneratorImpl"
Cohesion: 0.15
Nodes (11): FrameManager, getCurrentFrame, getLastIndex, queueFrame, SpeechWaveGeneratorImpl, cascade, frameManager, fricGenerator (+3 more)

### Community 107 - "comentrypoints.c"
Cohesion: 0.27
Nodes (12): REFCLSID, BOOL, HRESULT, IClassFactory, REFIID, ULONG, ClassFactory_AddRef(), ClassFactory_LockServer() (+4 more)

### Community 108 - "espeakng.js"
Cohesion: 0.15
Nodes (8): eSpeakNG(), Building, Credits, Demo, Download, espeakng.js, Notes, Usage

### Community 109 - "utils.h"
Cohesion: 0.23
Nodes (6): sample, speechPlayer_frame_t, calculateValueAtFadePosition(), ISNAN(), MAX(), MIN()

### Community 110 - "uprintf"
Cohesion: 0.37
Nodes (12): 10.0.0 - 2017-06-25, codepoint_t, FILE, fget_utf8c(), fput_utf8c(), iswblank(), main(), print_file() (+4 more)

### Community 112 - "HomeScreen.kt"
Cohesion: 0.08
Nodes (25): add, alignment, box, clickable, delete, iconbutton, imeaction, keyboardactions (+17 more)

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 114 - "setlengths.c"
Cohesion: 0.24
Nodes (10): SPEED_FACTORS, Translator, voice_t, CalcLengths(), DoEmbedded2(), SetSpeed(), SetSpeedFactors(), SetSpeedMods() (+2 more)

### Community 115 - "ucd_lookup_category"
Cohesion: 0.33
Nodes (15): ucd_lookup_category(), codepoint_t, ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl(), ucd_isdigit(), ucd_isgraph() (+7 more)

### Community 116 - "uprintf"
Cohesion: 0.17
Nodes (19): stddef, 6.2.0 - 2013-10-16, codepoint_t, ucd_tolower(), ucd_totitle(), ucd_toupper(), totitle(), toupper() (+11 more)

### Community 117 - "Resonator"
Cohesion: 0.17
Nodes (11): Resonator, a, anti, b, bandwidth, c, frequency, p1 (+3 more)

### Community 118 - "Change Log"
Cohesion: 0.18
Nodes (10): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06, 9.0.0 - 2016-12-28 (+2 more)

### Community 119 - "DictionaryDownloadsTest.kt"
Cohesion: 0.10
Nodes (17): accessibilityevent, atomicinteger, bufferedinputstream, bytearrayinputstream, bytearrayoutputstream, countdownlatch, fileinputstream, fileoutputstream (+9 more)

### Community 120 - "ProjectStorage"
Cohesion: 0.22
Nodes (3): MainActivityTest, Context, ProjectStorage

### Community 121 - "DictionaryHeadword"
Cohesion: 0.20
Nodes (3): DictionaryHeadword, DictionaryHeadwordTest, normalizer

### Community 122 - "lb.md"
Cohesion: 0.18
Nodes (10): Build and use the project, Integration, Introduction, lb_emoji, lb_list, lb_rules, Luxembourgish customization, Phoneme inventory (+2 more)

### Community 123 - "MBROLA Voices"
Cohesion: 0.18
Nodes (11): 1. Add MBROLA voice definition file, 2. Add MBROLA phoneme translation file, 3. Compile voice and update Makefile.am file, Adding new MBROLA voice entry to eSpeak NG, Installation of MBROLA package from source, Installation of standard packages, Linux Installation, MBROLA Voices (+3 more)

### Community 124 - "Kirshenbaum (ASCII-IPA) Transcription Scheme"
Cohesion: 0.18
Nodes (11): Consonants, Diacritics, Kirshenbaum (ASCII-IPA) Transcription Scheme, Length, Other Symbols, Other Symbols, Phoneme Transcription Schemes, References (+3 more)

### Community 125 - "CascadeFormantGenerator"
Cohesion: 0.18
Nodes (10): CascadeFormantGenerator, r1, r2, r3, r4, r5, r6, rN0 (+2 more)

### Community 126 - "test"
Cohesion: 0.13
Nodes (20): assertarrayequals, assertequals, assertfalse, assertnotnull, assertnull, assertsame, assertthrows, asserttrue (+12 more)

### Community 127 - "Third-party software and dictionary data"
Cohesion: 0.50
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 129 - "GetFileLength"
Cohesion: 0.19
Nodes (14): GetFileLength(), LoadDictionary(), Reverse4Bytes(), ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context() (+6 more)

### Community 130 - "Consonants"
Cohesion: 0.33
Nodes (6): Consonants, Gemination, Manner of Articulation, Other Symbols, Place of Articulation, Voice

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "unpackDictionaryArchive"
Cohesion: 0.33
Nodes (7): unpackDictionaryArchive(), DictionaryAsset, DictionaryDownloadProgress, DictionaryArchiveTest, ByteArray, ByteArrayInputStream, TrackedInput

### Community 134 - "Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability."
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability., Source Nodes

### Community 135 - "GhostwriterTheme"
Cohesion: 0.14
Nodes (11): BeatComponentsTest, AboutScreenTest, BeatPlaybackControls(), BeatPlayerPanel(), CenteredPlayerContent(), IntArray, Modifier, IntArray (+3 more)

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - "android.content.Context"
Cohesion: 0.13
Nodes (14): android.app.Application, android.content.Context, android.preference.Preference, android.preference.Preference.OnPreferenceChangeListener, android.preference.PreferenceActivity, android.preference.PreferenceFragment, android.preference.PreferenceGroup, android.util.AttributeSet (+6 more)

### Community 138 - "ProcessSsmlTag"
Cohesion: 0.23
Nodes (19): PARAM_STACK, SSML_STACK, AddNameData(), attr_prosody_value(), attrcmp(), attrcopy_utf8(), attrlookup(), attrnumber() (+11 more)

### Community 139 - "Lookup"
Cohesion: 0.29
Nodes (17): IsDigit09(), Lookup(), Translator, WORD_TAB, CheckDotOrdinal(), CheckThousandsGroup(), hu_number_e(), LookupAccentedLetter() (+9 more)

### Community 140 - ".excludedWords"
Cohesion: 0.28
Nodes (4): CompoundRhymeFilter, Entry, Pronounced, CompoundRhymeFilterTest

### Community 141 - "Unicode Character Database Tools"
Cohesion: 0.25
Nodes (7): Bugs, Build Dependencies, Building, Debian, License Information, Unicode Character Database Tools, Updating the UCD Data

### Community 142 - "EditorScreenTest.kt"
Cohesion: 0.11
Nodes (26): abs, after, androidjunit4, ExampleInstrumentedTest, audioformat, ByteBuffer, byteorder, composetimeoutexception (+18 more)

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 144 - "ContextWrapper"
Cohesion: 0.33
Nodes (3): Context, ContextWrapper, ContextWrapper

### Community 145 - "create_dict_corpus_file.py"
Cohesion: 0.40
Nodes (3): argparse, mmap, shutil

### Community 146 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 147 - "mutablestateof"
Cohesion: 0.17
Nodes (12): ComponentName, IBinder, ServiceConnection, rememberBeatPlayback(), ServiceConnection, audioattributes, disposableeffect, getvalue (+4 more)

### Community 148 - "synthdata.c"
Cohesion: 0.22
Nodes (18): FMT_PARAMS, frameref_t, PHONEME_DATA, PHONEME_LIST, PHONEME_TAB, Translator, WORD_PH_DATA, CountVowelPosition() (+10 more)

### Community 149 - "SelectPhonemeTable"
Cohesion: 0.16
Nodes (18): PHONEME_DATA, PHONEME_LIST, PHONEME_LIST2, PHONEME_TAB, Translator, WORD_PH_DATA, MakePhonemeList(), ReInterpretPhoneme() (+10 more)

### Community 151 - "android/gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 152 - "CheckVoiceDataTest"
Cohesion: 0.33
Nodes (3): android.test.ActivityUnitTestCase, java.lang.reflect.Field, CheckVoiceDataTest

### Community 153 - "Using eSpeak NG as a library"
Cohesion: 0.50
Nodes (4): Additional info, Error solutions, Simple steps, Using eSpeak NG as a library

### Community 154 - "espeak-ng"
Cohesion: 0.67
Nodes (4): data, espeak-ng, installer, libespeak-ng

### Community 155 - "Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?, Source Nodes

### Community 156 - "SettingsScreen"
Cohesion: 0.20
Nodes (6): SettingsScreenTest, T, SettingsDropdownRow(), SettingsScreen(), SettingsSliderRow(), ClosedFloatingPointRange

### Community 157 - "void"
Cohesion: 0.12
Nodes (16): espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, PrintVersion(), fifo_is_busy(), fifo_stop(), fifo_terminate(), ESPEAK_API (+8 more)

### Community 170 - "InputStream"
Cohesion: 0.50
Nodes (3): InputStream, ByteArray, InputStream

### Community 171 - "AnnouncePunctuation"
Cohesion: 0.24
Nodes (12): DecodePhonemes(), AnnouncePunctuation(), Translator, DecodeWithPhonemeMode(), Eof(), GetC(), LookupCharName(), LookupSpecial() (+4 more)

### Community 172 - "Dictionaries"
Cohesion: 0.33
Nodes (6): Dictionaries, Pronunciation lookup, Release inputs and installation, Responsiveness and verification, Search behavior, Using dictionaries

### Community 174 - "Architecture"
Cohesion: 0.33
Nodes (6): Architecture, Dictionaries, Ghostwriter documentation, Known limitations, Playback and waveforms, Projects and settings

### Community 175 - "ucd_lookup_category_group"
Cohesion: 0.18
Nodes (12): category_group, codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category_group(), get_category_group_string(), lookup_category_group() (+4 more)

### Community 176 - "BeatPlaybackServiceTest"
Cohesion: 0.24
Nodes (7): BeatPlaybackServiceTest, ServiceConnection, ComponentName, IBinder, Notification, ServiceConnection, T

### Community 177 - "GhostwriterApp"
Cohesion: 0.17
Nodes (12): HomeScreenTest, About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, Screen (+4 more)

### Community 178 - ".plan"
Cohesion: 0.27
Nodes (3): DictionarySearchPlan, Prefix, DictionarySearchPlanTest

### Community 179 - "DictionaryArchive.kt"
Cohesion: 0.20
Nodes (7): ByteArray, FilterInputStream, FilterInputStream, coroutinecontext, cursor, ensureactive, gzipinputstream

### Community 181 - "EspeakIpaInstrumentedTest"
Cohesion: 0.18
Nodes (3): EspeakIpaInstrumentedTest, EspeakIpa, ByteArray

### Community 182 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 185 - "Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?, Source Nodes

### Community 186 - "Dictionary data license and attribution"
Cohesion: 0.33
Nodes (5): Conditions when redistributing, Dictionary data license and attribution, Modifications, Source attribution, Warranty

### Community 187 - "eSpeakNGWorker"
Cohesion: 0.17
Nodes (9): espeak_VOICE, eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices, ESPEAK_API (+1 more)

### Community 188 - "Build setup"
Cohesion: 0.67
Nodes (3): Build setup, Checkout and build, Requirements

### Community 189 - "Native pronunciation engine"
Cohesion: 0.40
Nodes (5): Native pronunciation engine, Regenerating language data, Runtime contract, Source and Android build, Verification and distribution

### Community 190 - "Testing"
Cohesion: 0.40
Nodes (5): Android tests, Checks without a device, Coverage and regression guidance, Coverage reports, Testing

### Community 192 - "CloseWavFile"
Cohesion: 0.33
Nodes (7): espeak_EVENT, FILE, CloseWavFile(), DisplayVoices(), OpenWavFile(), SynthCallback(), Write4Bytes()

### Community 193 - "Diacritics"
Cohesion: 0.25
Nodes (8): Articulation, Co-articulation, Consonant Release, Diacritics, Phonation, Rounding and Labialization, Syllabicity, Tongue Root

### Community 194 - "Features and roadmap"
Cohesion: 0.50
Nodes (4): Features and roadmap, Implemented, Non-goals, Planned

### Community 195 - "Releasing"
Cohesion: 0.50
Nodes (4): Build and verify, Maintainer runtime checks, Package matching source, Releasing

### Community 196 - "utf8_out"
Cohesion: 0.29
Nodes (10): utf8_out(), LookupEnvelopeName(), PHONEME_LIST, PHONEME_TAB, GetTranslatedPhonemeString(), WritePhMnemonic(), WritePhMnemonicWithStress(), LookupMnem() (+2 more)

### Community 197 - "ParallelFormantGenerator"
Cohesion: 0.22
Nodes (8): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate

### Community 198 - "DictionarySearchMode"
Cohesion: 0.25
Nodes (6): DictionarySearchMode, ASSONANCE, RHYME, WORD_PREFIX, WORD_SUFFIX, modeLabel()

### Community 199 - "getopt.c"
Cohesion: 0.46
Nodes (7): assert, getopt, gcd(), getopt(), getopt_internal(), getopt_long(), permute_args()

### Community 200 - "SpeakPunctuationPreference"
Cohesion: 0.38
Nodes (4): android.widget.EditText, android.widget.RadioButton, Override, SpeakPunctuationPreference

## Knowledge Gaps
- **475 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+470 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 947 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **43 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Text to Phoneme Translation` connect `tr_languages.c` to `index.md`?**
  _High betweenness centrality (0.054) - this node is a cross-community bridge._
- **Why does `eSpeakActivity` connect `eSpeakActivity` to `SpeakPunctuationPreference`, `Android`, `ImportVoicePreference.java`, `TextToSpeechTestCase`?**
  _High betweenness centrality (0.045) - this node is a cross-community bridge._
- **Are the 23 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 23 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _475 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._
- **Should `Android` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._
- **Should `synthesize.c` be split into smaller, more focused modules?**
  _Cohesion score 0.11738648947951273 - nodes in this community are weakly interconnected._