# Graph Report - Gh0stwrit3r  (2026-09-29)

## Corpus Check
- 301 files · ~544,954 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2055 file(s) not represented in the graph (top: (none) 1892, .xml 82, .test 17)

## Summary
- 3375 nodes · 8478 edges · 181 communities (141 shown, 40 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 1254 edges (avg confidence: 0.84)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `3232a816`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- Android
- synthesize.c
- ProjectMetadata
- ProjectStorageTest
- WaveformViewport
- DictionaryScreen.kt
- BeatPlayerPanel.kt
- WaveformExtractor
- android.content.Context
- WaveformCache
- gradlew
- Language Attributes
- WaveformView.kt
- What You Must Do When Invoked
- Steps
- Settings
- utf8_out
- graphify reference: extra exports and benchmark
- Ghostwriter
- graphify reference: query, path, explain
- graphify reference: add a URL and watch a folder
- graphify reference: commit hook and native CLAUDE.md integration
- graphify reference: incremental update and cluster-only
- graphify reference: GitHub clone and cross-repo merge
- graphify reference: transcribe video and audio
- extraction-spec.md
- synthdata.c
- EditorScreen
- TextToSpeechTestCase.java
- compiledata.c
- .fromIpa
- Ghostwriter agent instructions
- ImportVoicePreference.java
- DictionaryInstaller.kt
- Synthesize
- SettingsFormatTest.kt
- SpeechSynthesis
- espeak_ng
- tests/encoding.c
- espeak_command.c
- GhostwriterApp
- main
- speech.c
- wavegen.c
- demo.js
- eSpeakActivity
- utf8_in
- ttsengine.cpp
- DictionarySearch.kt
- ucd_properties
- mbrowrap.c
- DictionaryDownloadsTest.kt
- tests/readclause.c
- espeak_Initialize
- Diacritics
- VoiceVariantPreference.java
- math.h
- ucd.h
- strncpy0
- Phoneme Model
- eSpeakService.c
- TranslateClauseWithTerminator
- status_to_espeak_error
- ProjectStorageBeatTest
- Row
- FrameManagerImpl
- klatt.c
- SelectPhonemeTable
- Conlang X-SAMPA Transcription Scheme
- .excludedWords
- uprintf
- speechPlayer.cpp
- TranslateWord3
- compile_line
- .isTtsLangCode
- tr_languages.c
- index.md
- SSML (Speech Synthesis Markup Language)
- BeatPlaybackService.kt
- EditorScreen.kt
- numbers.md
- TtsService
- speechWaveGenerator.cpp
- VoiceVariant
- sPlayer.c
- PlaybackFocusState
- GhostwriterTheme
- intonation.c
- setlengths.c
- Callback
- Phoneme Instructions
- CodePoint
- SynthCallback
- SpeakPunctuationPreference.java
- TtsService.java
- withDictionaryTestContext
- printdata.py
- ucd.py
- Rhyme branch review
- Tune Definitions
- Diacritics
- SpeechWaveGeneratorImpl
- BeatPlaybackService
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- DictionaryInstallerInstrumentedTest.kt
- EditorScreenTest.kt
- emoji
- PlaybackNotificationUpdater
- espeak-ng.c
- SeekBarPreference
- Resonator
- Change Log
- ProcessSsmlTag
- MainActivityTest
- DictionaryArchiveSource
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- test
- documentation/README.md
- Third-party software and dictionary data
- espeakng_glue.cpp
- LyricsNotepad
- printucddata_cpp.cpp
- Vowels
- Phoneme Tables
- DictionaryHeadword
- rgroup_sorter
- common
- InputStream
- TestConnection
- BeatPlayerInstrumentedTest
- .excludedWords
- Unicode Character Database Tools
- EspeakIpa
- eSpeak NG user guide
- ucd_lookup_category
- create_dict_corpus_file.py
- Translation fuzzers
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- espeak_ng_PrintStatusCodeMessage
- .parse
- ProjectStorage
- android/gradlew
- CheckVoiceDataTest.java
- Using eSpeak NG as a library
- espeak-ng
- Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?
- Diacritics
- ProjectLyricsStorage
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- espeak_SetUriCallback
- LookupMnemName
- Testing and coverage
- file
- eSpeak NG offline IPA fallback
- BeatPlaybackServiceTest
- LoadVoice
- Ghostwriter documentation
- row
- Contribution Guide
- httpurlconnection

## God Nodes (most connected - your core abstractions)
1. `BeatPlayer` - 51 edges
2. `SpeechSynthesis` - 49 edges
3. `GhostwriterTheme()` - 43 edges
4. `ucd_properties()` - 38 edges
5. `ProjectStorage` - 33 edges
6. `BeatPlaybackService` - 31 edges
7. `create_text_decoder()` - 31 edges
8. `text_decoder_eof()` - 31 edges
9. `espeak_Initialize()` - 31 edges
10. `ProjectStorageTest` - 30 edges

## Surprising Connections (you probably didn't know these)
- `API and verification` --references--> `EspeakIpaInstrumentedTest`  [INFERRED]
  documentation/ESPEAK_NATIVE.md → app/src/androidTest/java/com/prosincerity/ghostwriter/data/EspeakIpaInstrumentedTest.kt
- `6. Implement rhyme queries and editor behavior` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/RHYME_DETECTION_PLAN.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `Phase 2 — Songwriting environment` --references--> `EditorScreen()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/screens/EditorScreen.kt
- `Phase 2 — Songwriting environment` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `Installing` --references--> `eSpeakActivity`  [INFERRED]
  third_party/espeak-ng/docs/building.md → third_party/espeak-ng/android/src/com/reecedunn/espeak/eSpeakActivity.java

## Import Cycles
- None detected.

## Communities (181 total, 40 thin omitted)

### Community 1 - "Android"
Cohesion: 0.10
Nodes (20): Android, Building, Building, Building eSpeak NG, Building with Gradle, Cross Compilation, Dependencies, Dependencies (+12 more)

### Community 2 - "synthesize.c"
Cohesion: 0.12
Nodes (40): FILE, PHONEME_LIST, PHONEME_TAB, GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), AdjustFormants(), AllocFrame() (+32 more)

### Community 3 - "ProjectMetadata"
Cohesion: 0.11
Nodes (10): ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, WaveformMarkerDialog(), EditorMarkerDialogs(), ProjectInfoDialog(), trimmedOrNull() (+2 more)

### Community 6 - "DictionaryScreen.kt"
Cohesion: 0.06
Nodes (77): add, alertdialog, alignment, LongBeatWarningDialog(), ReassignBeatDialog(), Modifier, AboutLink(), AboutScreen() (+69 more)

### Community 7 - "BeatPlayerPanel.kt"
Cohesion: 0.14
Nodes (14): CenteredPlayerContent(), Modifier, WaveformZoomButton(), button, card, columnscope, delay, fillmaxsize (+6 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.11
Nodes (7): WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest, MediaCodec, MediaFormat

### Community 9 - "android.content.Context"
Cohesion: 0.14
Nodes (16): android.app.Application, android.content.Context, android.os.Bundle, android.preference.Preference, android.preference.Preference.OnPreferenceChangeListener, android.preference.PreferenceActivity, android.preference.PreferenceFragment, android.preference.PreferenceGroup (+8 more)

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 14 - "WaveformView.kt"
Cohesion: 0.06
Nodes (33): IntArray, Modifier, WaveformView(), ceil, cliptobounds, detectdraggestures, detecttapgestures, detecttransformgestures (+25 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "Steps"
Cohesion: 0.20
Nodes (9): 1. Pin the release inputs, 2. Download, install, and read the SQLite databases, 3. Build a minimal eSpeak NG 1.52 Android library, 4. Add the IPA-only JNI wrapper and prove it on Android, 5. Join database lookup and runtime fallback, 7. Licensing, attribution, and release verification, Goal and data flow, Rhyme detection: dictionary and eSpeak NG integration plan (+1 more)

### Community 20 - "utf8_out"
Cohesion: 0.27
Nodes (18): IsDigit09(), utf8_out(), Lookup(), Translator, WORD_TAB, CheckDotOrdinal(), CheckThousandsGroup(), hu_number_e() (+10 more)

### Community 21 - "graphify reference: extra exports and benchmark"
Cohesion: 0.22
Nodes (8): graphify reference: extra exports and benchmark, Step 6b - Wiki (only if --wiki flag), Step 7 - Neo4j export (only if --neo4j or --neo4j-push flag), Step 7a - FalkorDB export (only if --falkordb or --falkordb-push flag), Step 7b - SVG export (only if --svg flag), Step 7c - GraphML export (only if --graphml flag), Step 7d - MCP server (only if --mcp flag), Step 8 - Token reduction benchmark (only if total_words > 5000)

### Community 22 - "Ghostwriter"
Cohesion: 0.20
Nodes (10): Building, Contributing, Documentation, Ghostwriter, License, Philosophy, Roadmap, Status (+2 more)

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

### Community 30 - "synthdata.c"
Cohesion: 0.16
Nodes (24): PHONEME_LIST, PHONEME_TAB, WritePhMnemonic(), WritePhMnemonicWithStress(), FMT_PARAMS, frameref_t, PHONEME_DATA, PHONEME_LIST (+16 more)

### Community 31 - "EditorScreen"
Cohesion: 0.28
Nodes (5): EditorScreenTest, IntArray, EditorScreen(), PendingBeatPreparation, MutableState

### Community 32 - "TextToSpeechTestCase.java"
Cohesion: 0.15
Nodes (10): android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, android.test.AndroidTestCase, TextToSpeechTest, Override, SuppressWarnings, TextToSpeechTestCase, Exception (+2 more)

### Community 33 - "compiledata.c"
Cohesion: 0.06
Nodes (88): phoneme_feature_t, PHONEME_TAB_LIST, SpectFrame, SpectSeq, FILE, Read4Bytes(), StringToWord(), CompileContext (+80 more)

### Community 34 - ".fromIpa"
Cohesion: 0.15
Nodes (4): SearchPrefix, IpaSearchKeys, Keys, IpaSearchKeysTest

### Community 35 - "Ghostwriter agent instructions"
Cohesion: 0.33
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 36 - "ImportVoicePreference.java"
Cohesion: 0.10
Nodes (19): accessibilityevent, android.os.AsyncTask, android.widget.ProgressBar, arrays, bufferedinputstream, downloadmanager, environment, filefilter (+11 more)

### Community 37 - "DictionaryInstaller.kt"
Cohesion: 0.11
Nodes (18): DictionaryAsset, DictionaryDownloadProgress, DictionaryInstaller, FilterInputStream, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK (+10 more)

### Community 38 - "Synthesize"
Cohesion: 0.35
Nodes (11): process_espeak_command(), InitNamedata(), InitText2(), espeak_ng_STATUS, espeak_POSITION_TYPE, sync_espeak_Char(), sync_espeak_Key(), sync_espeak_Synth() (+3 more)

### Community 39 - "SettingsFormatTest.kt"
Cohesion: 0.31
Nodes (3): formatPlaybackTime(), formatInterval(), SettingsFormatTest

### Community 40 - "SpeechSynthesis"
Cohesion: 0.08
Nodes (7): jsonexception, org.json.JSONObject, SpeechSynthesisTest, VoiceSettingsTest, SpeechSynthesis, SynthReadyCallback, VoiceSettings

### Community 41 - "espeak_ng"
Cohesion: 0.14
Nodes (14): dirent, encoding, errno, espeak_ng, limits, memcheck, msan_interface, osbyteorder (+6 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.19
Nodes (49): espeak_ng_ENCODING, espeak_ng_TEXT_DECODER, espeak_ng_STATUS, wchar_t, create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc() (+41 more)

### Community 43 - "espeak_command.c"
Cohesion: 0.10
Nodes (39): espeak_ng_OUTPUT_MODE, espeak_Char(), espeak_Key(), espeak_Synth_Mark(), espeak_PARAMETER, espeak_POSITION_TYPE, espeak_VOICE, t_espeak_command (+31 more)

### Community 44 - "GhostwriterApp"
Cohesion: 0.12
Nodes (16): HomeScreenTest, About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, Screen (+8 more)

### Community 45 - "main"
Cohesion: 0.10
Nodes (21): espeak_VOICE, eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices, main() (+13 more)

### Community 46 - "speech.c"
Cohesion: 0.06
Nodes (47): audio, espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, io, libgen, pthread, add_time_in_ms() (+39 more)

### Community 47 - "wavegen.c"
Cohesion: 0.08
Nodes (43): espeak_ng_OUTPUT_HOOKS, sonic, ESPEAK_NG_API, espeak_ng_SetRandSeed(), espeak_rand(), espeak_srand(), GetFrameRms(), MarkerEvent() (+35 more)

### Community 48 - "demo.js"
Cohesion: 0.07
Nodes (27): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+19 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.09
Nodes (19): android.content.BroadcastReceiver, android.os.Handler, android.os.Message, android.view.Menu, android.view.MenuItem, java.lang.ref.WeakReference, listview, menuinflater (+11 more)

### Community 50 - "utf8_in"
Cohesion: 0.21
Nodes (18): MatchRecord, utf8_in(), utf8_in2(), Translator, WORD_TAB, DollarRule(), IsLetter(), IsLetterGroup() (+10 more)

### Community 51 - "ttsengine.cpp"
Cohesion: 0.10
Nodes (37): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+29 more)

### Community 52 - "DictionarySearch.kt"
Cohesion: 0.11
Nodes (20): PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, DictionarySearchMode, ASSONANCE, RHYME, WORD_PREFIX (+12 more)

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (27): poll, procfs, signal, stdarg, BOOL, close_mbrola(), close_pipes(), create_pipes() (+19 more)

### Community 55 - "DictionaryDownloadsTest.kt"
Cohesion: 0.12
Nodes (30): activitynotfoundexception, androidjunit4, ExampleInstrumentedTest, assertisenabled, assertisnotenabled, asserttextcontains, atomicinteger, bytearrayinputstream (+22 more)

### Community 56 - "tests/readclause.c"
Cohesion: 0.12
Nodes (39): phoneme, readclause, speech, synthesize, AnnouncePunctuation(), CheckPhonemeMode(), clause_type_from_codepoint(), Eof() (+31 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.19
Nodes (27): espeak_AUDIO_OUTPUT, espeak_POSITION_TYPE, espeak_VOICE, espeak_Initialize(), espeak_SetVoiceByName(), espeak_SetVoiceByProperties(), espeak_Synth(), espeak_EVENT (+19 more)

### Community 58 - "Diacritics"
Cohesion: 0.07
Nodes (30): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Consonants, Diacritics, Fortis and Lenis (+22 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.11
Nodes (22): adapterview, android.app.Activity, android.util.Pair, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter, android.widget.TextView (+14 more)

### Community 60 - "math.h"
Cohesion: 0.29
Nodes (3): ieee80, ieee_extended_to_double(), main()

### Community 61 - "ucd.h"
Cohesion: 0.13
Nodes (26): category, category_group, property, script, DecodePhonemes(), get_category_group_string(), get_category_string(), get_script_string() (+18 more)

### Community 62 - "strncpy0"
Cohesion: 0.17
Nodes (25): strncpy0(), GetTranslatedPhonemeString(), espeak_VOICE, SetVoiceStack(), espeak_TextToPhonemes(), espeak_ng_STATUS, voice_t, DoVoiceChange() (+17 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.19
Nodes (27): JavaVM, jboolean, jclass, jni, jstring, espeak_EVENT, jint, JNICALL (+19 more)

### Community 65 - "TranslateClauseWithTerminator"
Cohesion: 0.15
Nodes (23): Translator, IsAlpha(), IsBracket(), IsSpace(), towlower2(), PHONEME_LIST2, Translator, WORD_TAB (+15 more)

### Community 66 - "status_to_espeak_error"
Cohesion: 0.20
Nodes (11): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, ESPEAK_API, espeak_ng_STATUS, wchar_t, espeak_SetPunctuationList() (+3 more)

### Community 67 - "ProjectStorageBeatTest"
Cohesion: 0.17
Nodes (3): IntArray, StagedFileWriter, ProjectStorageBeatTest

### Community 68 - "Row"
Cohesion: 0.31
Nodes (6): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations, SQLiteDatabase, PronunciationResult, DictionarySearch

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "klatt.c"
Cohesion: 0.20
Nodes (22): klatt_frame_ptr, resonator_ptr, antiresonator(), frame_t, voice_t, WGEN_DATA, DBtoLIN(), flutter() (+14 more)

### Community 71 - "SelectPhonemeTable"
Cohesion: 0.18
Nodes (17): PHONEME_DATA, PHONEME_LIST, PHONEME_LIST2, PHONEME_TAB, Translator, WORD_PH_DATA, MakePhonemeList(), ReInterpretPhoneme() (+9 more)

### Community 72 - "Conlang X-SAMPA Transcription Scheme"
Cohesion: 0.14
Nodes (14): Conlang X-SAMPA Transcription Scheme, Consonants, Intonation, Length, Manner of Articulation, Other Symbols, Other Symbols, Phoneme Transcription Schemes (+6 more)

### Community 73 - ".excludedWords"
Cohesion: 0.18
Nodes (6): AssonanceFormFilter, Entry, PrefixNode, Pronounced, AssonanceFormFilterTest, Pronounced

### Community 74 - "uprintf"
Cohesion: 0.25
Nodes (14): ucd_category, ucd_category_group, ucd_script, ucd_get_category_group_string(), ucd_get_category_string(), ucd_get_script_string(), codepoint_t, FILE (+6 more)

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.19
Nodes (10): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+2 more)

### Community 76 - "TranslateWord3"
Cohesion: 0.16
Nodes (26): AppendPhonemes(), GetVowelStress(), SetWordStress(), TranslateRules(), IsSuperscript(), SetSpellingStress(), Translator, DecodeWithPhonemeMode() (+18 more)

### Community 77 - "compile_line"
Cohesion: 0.16
Nodes (22): IsDigit(), isspace2(), CompileContext, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context() (+14 more)

### Community 78 - ".isTtsLangCode"
Cohesion: 0.27
Nodes (3): Override, TextToSpeechServiceTest, TtsServiceTest

### Community 79 - "tr_languages.c"
Cohesion: 0.05
Nodes (60): LANGUAGE_OPTIONS, Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation (+52 more)

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - "BeatPlaybackService.kt"
Cohesion: 0.11
Nodes (17): Context, IBinder, Notification, LocalBinder, audioattributes, AudioFocusRequest, Binder, bitmap (+9 more)

### Community 83 - "EditorScreen.kt"
Cohesion: 0.07
Nodes (38): activityresultcontracts, Bundle, Context, MainActivity, openExternalLink(), ComponentName, IBinder, ServiceConnection (+30 more)

### Community 85 - "TtsService"
Cohesion: 0.14
Nodes (6): android.speech.tts.SynthesisRequest, Override, SuppressWarnings, TtsService, Override, Voice

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.23
Nodes (4): java.util.regex.Pattern, VoiceVariantTest, Override, VoiceVariant

### Community 88 - "sPlayer.c"
Cohesion: 0.25
Nodes (14): frame_t, sample, speechPlayer_frame_t, voice_t, WGEN_DATA, fillSpeechPlayerFrame(), isKlattFrameFollowing(), KlattFiniSP() (+6 more)

### Community 90 - "GhostwriterTheme"
Cohesion: 0.12
Nodes (12): BeatComponentsTest, AboutScreenTest, DictionaryScreenTest, DictionaryMatch, DictionarySearchResult, BeatPlaybackControls(), BeatPlayerPanel(), IntArray (+4 more)

### Community 91 - "intonation.c"
Cohesion: 0.27
Nodes (16): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+8 more)

### Community 92 - "setlengths.c"
Cohesion: 0.19
Nodes (13): SPEED_FACTORS, espeak_ng_STATUS, Translator, voice_t, CalcLengths(), DoEmbedded2(), SetParameter(), SetSpeed() (+5 more)

### Community 93 - "Callback"
Cohesion: 0.19
Nodes (3): Callback, Bundle, Intent

### Community 94 - "Phoneme Instructions"
Cohesion: 0.12
Nodes (17): CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, FMT, IfNextVowelAppend (+9 more)

### Community 97 - "SpeakPunctuationPreference.java"
Cohesion: 0.12
Nodes (14): android.content.DialogInterface, android.content.SharedPreferences, android.preference.DialogPreference, android.util.AttributeSet, android.widget.EditText, android.widget.RadioButton, android.widget.Spinner, editable (+6 more)

### Community 98 - "TtsService.java"
Cohesion: 0.09
Nodes (30): android.speech.tts.SynthesisCallback, android.speech.tts.TextToSpeechService, anyof, arraylist, assertthat, audiotrack, bundle, checksum (+22 more)

### Community 99 - "withDictionaryTestContext"
Cohesion: 0.11
Nodes (11): ByteArrayInputStream, ByteArrayInputStream, dictionaryArchive(), ByteArray, Context, ContextWrapper, T, withDictionaryTestContext() (+3 more)

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 101 - "ucd.py"
Cohesion: 0.23
Nodes (4): os, sys, parse_property_mapping(), parse_ucd_data()

### Community 102 - "Rhyme branch review"
Cohesion: 0.40
Nodes (4): Completed stages, Review limits, Rhyme branch review, Validation

### Community 103 - "Tune Definitions"
Cohesion: 0.14
Nodes (13): Clauses, endtune, head, headenv, headextend, headlast, Intonation, nucleus (+5 more)

### Community 104 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Consonant Release, Consonants, Diacritics, Intonation, Length, Manner of Articulation (+14 more)

### Community 105 - "SpeechWaveGeneratorImpl"
Cohesion: 0.11
Nodes (15): FrameManager, getCurrentFrame, getLastIndex, queueFrame, SpeechWaveGenerator, setFrameManager, SpeechWaveGeneratorImpl, cascade (+7 more)

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
Cohesion: 0.17
Nodes (20): stddef, 10.0.0 - 2017-06-25, codepoint_t, ucd_tolower(), ucd_totitle(), ucd_toupper(), codepoint_t, ucd_script (+12 more)

### Community 111 - "DictionaryInstallerInstrumentedTest.kt"
Cohesion: 0.13
Nodes (12): ContextWrapper, Context, ContextWrapper, async, bytearrayoutputstream, cancel, completabledeferred, cursor (+4 more)

### Community 112 - "EditorScreenTest.kt"
Cohesion: 0.15
Nodes (14): AudioManager, composetimeoutexception, lifecycle, longclick, MediaController, mediametadata, MediaSession, notification (+6 more)

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 114 - "PlaybackNotificationUpdater"
Cohesion: 0.33
Nodes (3): PlaybackNotificationUpdater, PlaybackNotificationUpdaterTest, TestQueue

### Community 115 - "espeak-ng.c"
Cohesion: 0.16
Nodes (15): assert, fcntl, getopt, gcd(), getopt(), getopt_internal(), getopt_long(), permute_args() (+7 more)

### Community 116 - "SeekBarPreference"
Cohesion: 0.09
Nodes (9): android.widget.SeekBar, OnSeekBarChangeListener, Override, SeekBarPreference, Parameter, UnitType, Percentage, Punctuation (+1 more)

### Community 117 - "Resonator"
Cohesion: 0.10
Nodes (19): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate (+11 more)

### Community 118 - "Change Log"
Cohesion: 0.17
Nodes (11): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.2.0 - 2013-10-16, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06 (+3 more)

### Community 119 - "ProcessSsmlTag"
Cohesion: 0.23
Nodes (19): PARAM_STACK, SSML_STACK, AddNameData(), attr_prosody_value(), attrcmp(), attrcopy_utf8(), attrlookup(), attrnumber() (+11 more)

### Community 121 - "DictionaryArchiveSource"
Cohesion: 0.34
Nodes (4): DictionaryInstallerInstrumentedTest, Context, DictionaryArchiveSource, IpaGenerator

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
Cohesion: 0.12
Nodes (18): shouldWarnBeforeWaveformExtraction(), parsePositiveBpm(), ProjectInfoBpmTest, WaveformWarningTest, assertequals, assertfalse, assertnotequals, assertnotnull (+10 more)

### Community 127 - "documentation/README.md"
Cohesion: 0.36
Nodes (3): Build setup, Open and build, Requirements

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 130 - "LyricsNotepad"
Cohesion: 0.22
Nodes (8): LyricsNotepadTest, LyricsNotepad(), Feature Roadmap, Guiding rules, Non-goals, Phase 1 — Barebones notepad (MVP), Phase 2 — Songwriting environment, 6. Implement rhyme queries and editor behavior

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "Phoneme Tables"
Cohesion: 0.12
Nodes (16): Attributes, Conditional Statements, Conditions, Customization of sound source files, endtype, lengthmod, Phoneme Definitions, Phoneme Files (+8 more)

### Community 134 - "DictionaryHeadword"
Cohesion: 0.19
Nodes (3): DictionaryHeadword, DictionaryHeadwordTest, normalizer

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - "InputStream"
Cohesion: 0.50
Nodes (3): InputStream, ByteArray, InputStream

### Community 138 - "TestConnection"
Cohesion: 0.13
Nodes (10): HttpURLConnection, openDictionaryArchive(), DictionaryArchiveSourceTest, ByteArrayInputStream, InputStream, ByteArrayInputStream, ByteArrayInputStream, HttpURLConnection (+2 more)

### Community 140 - ".excludedWords"
Cohesion: 0.28
Nodes (4): CompoundRhymeFilter, Entry, Pronounced, CompoundRhymeFilterTest

### Community 141 - "Unicode Character Database Tools"
Cohesion: 0.25
Nodes (7): Bugs, Build Dependencies, Building, Debian, License Information, Unicode Character Database Tools, Updating the UCD Data

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 144 - "ucd_lookup_category"
Cohesion: 0.22
Nodes (20): codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category(), ucd_lookup_category_group(), codepoint_t, ucd_isalnum() (+12 more)

### Community 145 - "create_dict_corpus_file.py"
Cohesion: 0.40
Nodes (3): argparse, mmap, shutil

### Community 146 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 147 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.39
Nodes (9): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), jbyteArray (+1 more)

### Community 148 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.31
Nodes (10): ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage() (+2 more)

### Community 151 - "android/gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 152 - "CheckVoiceDataTest.java"
Cohesion: 0.19
Nodes (7): android.annotation.SuppressLint, android.content.Intent, android.test.ActivityUnitTestCase, java.lang.reflect.Field, CheckVoiceDataTest, GetSampleText, Override

### Community 153 - "Using eSpeak NG as a library"
Cohesion: 0.50
Nodes (4): Additional info, Error solutions, Simple steps, Using eSpeak NG as a library

### Community 154 - "espeak-ng"
Cohesion: 0.67
Nodes (4): data, espeak-ng, installer, libespeak-ng

### Community 155 - "Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?, Source Nodes

### Community 156 - "Diacritics"
Cohesion: 0.25
Nodes (8): Articulation, Co-articulation, Consonant Release, Diacritics, Phonation, Rounding and Labialization, Syllabicity, Tongue Root

### Community 171 - "LookupMnemName"
Cohesion: 0.33
Nodes (6): DecodeRule(), print_dictionary_flags(), MNEM_TAB, LookupMnemName(), MNEM_TAB, ReadNumbers()

### Community 172 - "Testing and coverage"
Cohesion: 0.67
Nodes (3): Coverage, Regular checks, Testing and coverage

### Community 173 - "file"
Cohesion: 0.13
Nodes (17): abs, after, ContextWrapper, Context, ContextWrapper, assertsame, AssetManager, audioformat (+9 more)

### Community 174 - "eSpeak NG offline IPA fallback"
Cohesion: 0.18
Nodes (9): API and verification, Clean checkout and build, eSpeak NG offline IPA fallback, Regenerate the packaged data, Source and licensing, Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code (+1 more)

### Community 176 - "BeatPlaybackServiceTest"
Cohesion: 0.24
Nodes (7): BeatPlaybackServiceTest, ServiceConnection, ComponentName, IBinder, Notification, ServiceConnection, T

### Community 177 - "LoadVoice"
Cohesion: 0.08
Nodes (33): 1.49.1 - 2017-01-21, GetFileLength(), is_str_totally_null(), LookupEnvelopeName(), InitGroups(), LoadDictionary(), Reverse4Bytes(), LoadConfig() (+25 more)

### Community 178 - "Ghostwriter documentation"
Cohesion: 0.67
Nodes (3): Architecture, Current limits, Ghostwriter documentation

### Community 182 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

## Knowledge Gaps
- **459 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+454 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 925 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **40 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Text to Phoneme Translation` connect `tr_languages.c` to `index.md`?**
  _High betweenness centrality (0.072) - this node is a cross-community bridge._
- **Why does `eSpeakActivity` connect `eSpeakActivity` to `TextToSpeechTestCase.java`, `SpeakPunctuationPreference.java`, `VoiceVariantPreference.java`, `Android`?**
  _High betweenness centrality (0.054) - this node is a cross-community bridge._
- **Are the 23 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 23 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _459 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._
- **Should `Android` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._
- **Should `synthesize.c` be split into smaller, more focused modules?**
  _Cohesion score 0.12439024390243902 - nodes in this community are weakly interconnected._