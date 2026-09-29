# Graph Report - Gh0stwrit3r  (2026-09-29)

## Corpus Check
- 301 files · ~544,856 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2055 file(s) not represented in the graph (top: (none) 1892, .xml 82, .test 17)

## Summary
- 3374 nodes · 8472 edges · 188 communities (146 shown, 42 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 1254 edges (avg confidence: 0.84)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `03a7b8e7`
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
- Lookup
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
- TextToSpeechTestCase
- compiledata.c
- .fromIpa
- Ghostwriter agent instructions
- CheckVoiceData
- DictionaryInstaller.kt
- Synthesize
- SettingsFormatTest.kt
- SpeechSynthesis
- speech.c
- tests/encoding.c
- espeak_command.c
- MainActivity.kt
- eSpeakNGWorker
- fifo.c
- wavegen.c
- demo.js
- eSpeakActivity
- utf8_in
- ttsengine.cpp
- DictionarySearch.kt
- ucd_properties
- mbrowrap.c
- EditorScreenTest.kt
- tests/readclause.c
- espeak_Initialize
- Diacritics
- VoiceVariantPreference.java
- spect.c
- ucd.h
- main
- Phoneme Model
- eSpeakService.c
- SeekBarPreference
- status_to_espeak_error
- ProjectStorage
- VoiceVariantPreference
- FrameManagerImpl
- klatt.c
- SelectPhonemeTable
- Diacritics
- ImportVoicePreference.java
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
- .writeText
- printdata.py
- ucd.py
- Rhyme branch review
- Tune Definitions
- X-SAMPA Transcription Scheme
- SpeechWaveGeneratorImpl
- BeatPlaybackService
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- DictionaryInstallerInstrumentedTest.kt
- utf8_out
- emoji
- PlaybackNotificationUpdater
- CloseWavFile
- .createSeekBarPreference
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
- espeak_ng_CompileMbrolaVoice
- espeak_ng_CompilePhonemeDataPath
- rgroup_sorter
- common
- .cancelDownloadRestoresControlsWithoutInstalling
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
- LoadSoundFile
- android/gradlew
- CheckVoiceDataTest
- Using eSpeak NG as a library
- espeak-ng
- Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?
- SpeechSynthesisTest
- ReadClause
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- espeak_SetUriCallback
- LoadLanguageOptions
- phoneme_add_feature
- file
- eSpeak NG offline IPA fallback
- GetSampleText
- BeatPlaybackServiceTest
- LoadVoice
- Ghostwriter documentation
- EspeakIpaInstrumentedTest.kt
- row
- Diacritics
- Contribution Guide
- httpurlconnection
- FilterInputStream
- Consonants
- espeak_ng_InitializeOutput

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

## Communities (188 total, 42 thin omitted)

### Community 1 - "Android"
Cohesion: 0.10
Nodes (20): Android, Building, Building, Building eSpeak NG, Building with Gradle, Cross Compilation, Dependencies, Dependencies (+12 more)

### Community 2 - "synthesize.c"
Cohesion: 0.12
Nodes (42): FILE, PHONEME_LIST, PHONEME_TAB, GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), AdjustFormants(), AllocFrame() (+34 more)

### Community 3 - "ProjectMetadata"
Cohesion: 0.12
Nodes (10): ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, WaveformMarkerDialog(), EditorMarkerDialogs(), ProjectInfoDialog(), trimmedOrNull() (+2 more)

### Community 6 - "DictionaryScreen.kt"
Cohesion: 0.06
Nodes (73): add, alertdialog, alignment, LongBeatWarningDialog(), ReassignBeatDialog(), arrangement, arrowback, backhandler (+65 more)

### Community 7 - "BeatPlayerPanel.kt"
Cohesion: 0.14
Nodes (14): CenteredPlayerContent(), Modifier, WaveformZoomButton(), button, card, columnscope, delay, fillmaxsize (+6 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.11
Nodes (7): WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest, MediaCodec, MediaFormat

### Community 9 - "android.content.Context"
Cohesion: 0.17
Nodes (14): android.app.Application, android.content.Context, android.os.Bundle, android.preference.Preference, android.preference.Preference.OnPreferenceChangeListener, android.preference.PreferenceActivity, android.preference.PreferenceFragment, android.preference.PreferenceGroup (+6 more)

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

### Community 20 - "Lookup"
Cohesion: 0.29
Nodes (17): IsDigit09(), Lookup(), Translator, WORD_TAB, CheckDotOrdinal(), CheckThousandsGroup(), hu_number_e(), LookupAccentedLetter() (+9 more)

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
Cohesion: 0.21
Nodes (19): FMT_PARAMS, frameref_t, PHONEME_DATA, PHONEME_LIST, PHONEME_TAB, Translator, WORD_PH_DATA, CountVowelPosition() (+11 more)

### Community 31 - "EditorScreen"
Cohesion: 0.28
Nodes (5): EditorScreenTest, IntArray, EditorScreen(), PendingBeatPreparation, MutableState

### Community 32 - "TextToSpeechTestCase"
Cohesion: 0.16
Nodes (9): android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, TextToSpeechTest, Override, SuppressWarnings, TextToSpeechTestCase, Exception, Voice (+1 more)

### Community 33 - "compiledata.c"
Cohesion: 0.17
Nodes (42): PHONEME_TAB_LIST, StringToWord(), CompileContext, PHONEME_TAB, CalculateSample(), CallPhoneme(), CheckNextChar(), CompileElif() (+34 more)

### Community 34 - ".fromIpa"
Cohesion: 0.05
Nodes (18): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations, DictionarySearch, SQLiteDatabase, SearchPrefix, AssonanceFormFilter, Entry (+10 more)

### Community 35 - "Ghostwriter agent instructions"
Cohesion: 0.33
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 36 - "CheckVoiceData"
Cohesion: 0.15
Nodes (8): android.widget.ProgressBar, CheckVoiceData, Override, AsyncExtract, DownloadVoiceData, ExtractProgress, Override, FileUtils

### Community 37 - "DictionaryInstaller.kt"
Cohesion: 0.15
Nodes (14): DictionaryAsset, DictionaryDownloadProgress, DictionaryInstaller, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK, WIKTIONARY (+6 more)

### Community 38 - "Synthesize"
Cohesion: 0.20
Nodes (17): process_espeak_command(), close_stream(), fifo_terminate(), init(), pop(), say_thread(), InitNamedata(), InitText2() (+9 more)

### Community 39 - "SettingsFormatTest.kt"
Cohesion: 0.31
Nodes (3): formatPlaybackTime(), formatInterval(), SettingsFormatTest

### Community 40 - "SpeechSynthesis"
Cohesion: 0.10
Nodes (7): jsonexception, jsonobject, org.json.JSONObject, VoiceSettingsTest, SpeechSynthesis, SynthReadyCallback, VoiceSettings

### Community 41 - "speech.c"
Cohesion: 0.14
Nodes (18): audio, common, dirent, encoding, errno, espeak_ng, fcntl, io (+10 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.19
Nodes (51): espeak_ng_ENCODING, espeak_ng_TEXT_DECODER, espeak_ng_STATUS, wchar_t, create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc() (+43 more)

### Community 43 - "espeak_command.c"
Cohesion: 0.13
Nodes (31): espeak_PARAMETER, espeak_POSITION_TYPE, espeak_VOICE, t_espeak_command, wchar_t, create_espeak_char(), create_espeak_key(), create_espeak_mark() (+23 more)

### Community 44 - "MainActivity.kt"
Cohesion: 0.06
Nodes (33): AboutScreenTest, HomeScreenTest, About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home (+25 more)

### Community 45 - "eSpeakNGWorker"
Cohesion: 0.10
Nodes (18): espeak_VOICE, eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices, PrintVersion() (+10 more)

### Community 46 - "fifo.c"
Cohesion: 0.07
Nodes (42): assert, espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, getopt, libgen, pthread, gcd() (+34 more)

### Community 47 - "wavegen.c"
Cohesion: 0.08
Nodes (42): espeak_ng_OUTPUT_HOOKS, sonic, ESPEAK_NG_API, espeak_ng_SetRandSeed(), espeak_rand(), espeak_srand(), MarkerEvent(), MbrolaFill() (+34 more)

### Community 48 - "demo.js"
Cohesion: 0.06
Nodes (29): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+21 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.08
Nodes (23): android.content.BroadcastReceiver, android.content.Intent, android.os.Handler, android.os.Message, android.util.Pair, android.view.Menu, android.view.MenuItem, android.widget.EditText (+15 more)

### Community 50 - "utf8_in"
Cohesion: 0.11
Nodes (37): MatchRecord, Translator, is_str_totally_null(), IsAlpha(), IsBracket(), IsDigit(), IsSpace(), towlower2() (+29 more)

### Community 51 - "ttsengine.cpp"
Cohesion: 0.10
Nodes (37): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+29 more)

### Community 52 - "DictionarySearch.kt"
Cohesion: 0.14
Nodes (15): SQLiteDatabase, PronunciationResult, PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, sourceLabel(), context (+7 more)

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (27): poll, procfs, signal, stdarg, BOOL, close_mbrola(), close_pipes(), create_pipes() (+19 more)

### Community 55 - "EditorScreenTest.kt"
Cohesion: 0.11
Nodes (37): activitynotfoundexception, androidjunit4, assertisenabled, assertisnotenabled, asserttextcontains, atomicinteger, bytearrayinputstream, continuation (+29 more)

### Community 56 - "tests/readclause.c"
Cohesion: 0.15
Nodes (30): phoneme, readclause, speech, synthesize, clause_type_from_codepoint(), espeak_ng_STATUS, main(), set_text() (+22 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.18
Nodes (27): espeak_AUDIO_OUTPUT, espeak_POSITION_TYPE, espeak_VOICE, espeak_Initialize(), espeak_SetVoiceByName(), espeak_SetVoiceByProperties(), espeak_Synth(), espeak_EVENT (+19 more)

### Community 58 - "Diacritics"
Cohesion: 0.08
Nodes (24): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Diacritics, Fortis and Lenis, Height (+16 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.20
Nodes (15): adapterview, android.app.Activity, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter, android.widget.TextView, FileListAdapter (+7 more)

### Community 60 - "spect.c"
Cohesion: 0.11
Nodes (18): ieee80, osbyteorder, SpectFrame, SpectSeq, speechplayer, ieee_extended_to_double(), espeak_ng_STATUS, FILE (+10 more)

### Community 61 - "ucd.h"
Cohesion: 0.09
Nodes (33): category, property, script, stddef, 6.2.0 - 2013-10-16, codepoint_t, ucd_tolower(), ucd_totitle() (+25 more)

### Community 62 - "main"
Cohesion: 0.17
Nodes (26): DisplayVoices(), main(), strncpy0(), espeak_VOICE, SetVoiceStack(), espeak_ng_ERROR_CONTEXT, espeak_ng_Initialize(), espeak_ng_STATUS (+18 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (26): JavaVM, jboolean, jclass, jstring, espeak_EVENT, jint, JNICALL, JNIEnv (+18 more)

### Community 65 - "SeekBarPreference"
Cohesion: 0.21
Nodes (4): android.widget.SeekBar, OnSeekBarChangeListener, Override, SeekBarPreference

### Community 66 - "status_to_espeak_error"
Cohesion: 0.17
Nodes (16): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, ESPEAK_API, espeak_ng_STATUS, espeak_PARAMETER, wchar_t (+8 more)

### Community 67 - "ProjectStorage"
Cohesion: 0.14
Nodes (3): IntArray, ProjectStorage, ProjectStorageBeatTest

### Community 68 - "VoiceVariantPreference"
Cohesion: 0.23
Nodes (5): Override, VariantData, VariantDataListAdapter, ViewHolder, VoiceVariantPreference

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "klatt.c"
Cohesion: 0.19
Nodes (23): klatt_frame_ptr, resonator_ptr, antiresonator(), frame_t, voice_t, WGEN_DATA, DBtoLIN(), flutter() (+15 more)

### Community 71 - "SelectPhonemeTable"
Cohesion: 0.11
Nodes (29): PHONEME_DATA, PHONEME_LIST, PHONEME_LIST2, PHONEME_TAB, Translator, WORD_PH_DATA, MakePhonemeList(), ReInterpretPhoneme() (+21 more)

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 73 - "ImportVoicePreference.java"
Cohesion: 0.11
Nodes (16): accessibilityevent, android.os.AsyncTask, android.widget.Spinner, arrays, bufferedinputstream, downloadmanager, environment, filefilter (+8 more)

### Community 74 - "uprintf"
Cohesion: 0.20
Nodes (20): codepoint_t, ucd_isalnum(), ucd_isgraph(), ucd_isprint(), ucd_ispunct(), ucd_isspace(), ucd_isxdigit(), isalnum() (+12 more)

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.14
Nodes (14): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+6 more)

### Community 76 - "TranslateWord3"
Cohesion: 0.18
Nodes (20): GetVowelStress(), SetWordStress(), IsSuperscript(), SetSpellingStress(), AlphabetFromChar(), ALPHABET, EmbeddedCommand(), strchr_w() (+12 more)

### Community 77 - "compile_line"
Cohesion: 0.18
Nodes (20): isspace2(), CompileContext, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context(), compile_dictlist_end() (+12 more)

### Community 78 - ".isTtsLangCode"
Cohesion: 0.19
Nodes (6): description, org.hamcrest.Matcher, Override, TextToSpeechServiceTest, TtsServiceTest, typesafematcher

### Community 79 - "tr_languages.c"
Cohesion: 0.06
Nodes (54): LANGUAGE_OPTIONS, Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation (+46 more)

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - "BeatPlaybackService.kt"
Cohesion: 0.11
Nodes (17): Context, IBinder, Notification, LocalBinder, AudioFocusRequest, Binder, bitmap, broadcastreceiver (+9 more)

### Community 83 - "EditorScreen.kt"
Cohesion: 0.09
Nodes (28): activityresultcontracts, ComponentName, IBinder, ServiceConnection, rememberBeatPlayback(), ServiceConnection, displayNameFor(), Context (+20 more)

### Community 85 - "TtsService"
Cohesion: 0.14
Nodes (6): android.speech.tts.SynthesisRequest, Override, SuppressWarnings, TtsService, Override, Voice

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.22
Nodes (4): java.util.regex.Pattern, VoiceVariantTest, Override, VoiceVariant

### Community 88 - "sPlayer.c"
Cohesion: 0.25
Nodes (14): frame_t, sample, speechPlayer_frame_t, voice_t, WGEN_DATA, fillSpeechPlayerFrame(), isKlattFrameFollowing(), KlattFiniSP() (+6 more)

### Community 90 - "GhostwriterTheme"
Cohesion: 0.12
Nodes (15): BeatComponentsTest, DictionaryScreenTest, DictionaryMatch, DictionarySearchMode, ASSONANCE, RHYME, WORD_PREFIX, WORD_SUFFIX (+7 more)

### Community 91 - "intonation.c"
Cohesion: 0.27
Nodes (16): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+8 more)

### Community 92 - "setlengths.c"
Cohesion: 0.21
Nodes (12): SPEED_FACTORS, espeak_ng_STATUS, Translator, voice_t, CalcLengths(), DoEmbedded2(), SetParameter(), SetSpeed() (+4 more)

### Community 93 - "Callback"
Cohesion: 0.19
Nodes (3): Callback, Bundle, Intent

### Community 94 - "Phoneme Instructions"
Cohesion: 0.06
Nodes (33): Attributes, CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, Conditional Statements (+25 more)

### Community 97 - "SpeakPunctuationPreference.java"
Cohesion: 0.17
Nodes (10): android.content.DialogInterface, android.content.SharedPreferences, android.preference.DialogPreference, android.util.AttributeSet, android.widget.RadioButton, editable, preferencemanager, r (+2 more)

### Community 98 - "TtsService.java"
Cohesion: 0.10
Nodes (30): android.annotation.SuppressLint, android.speech.tts.SynthesisCallback, android.speech.tts.TextToSpeechService, android.test.ActivityUnitTestCase, android.test.AndroidTestCase, anyof, arraylist, assertthat (+22 more)

### Community 99 - ".writeText"
Cohesion: 0.15
Nodes (5): T, withDictionaryTestContext(), EspeakIpaInstrumentedTest, ProjectLyricsStorage, StagedFileWriter

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
Cohesion: 0.20
Nodes (18): get_category_string(), ucd_category, ucd_category_group, ucd_script, ucd_get_category_group_string(), ucd_get_category_string(), ucd_get_script_string(), codepoint_t (+10 more)

### Community 111 - "DictionaryInstallerInstrumentedTest.kt"
Cohesion: 0.10
Nodes (15): ContextWrapper, Context, ContextWrapper, Context, ContextWrapper, ContextWrapper, async, bytearrayoutputstream (+7 more)

### Community 112 - "utf8_out"
Cohesion: 0.60
Nodes (6): utf8_out(), PHONEME_LIST, PHONEME_TAB, GetTranslatedPhonemeString(), WritePhMnemonic(), WritePhMnemonicWithStress()

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 114 - "PlaybackNotificationUpdater"
Cohesion: 0.33
Nodes (3): PlaybackNotificationUpdater, PlaybackNotificationUpdaterTest, TestQueue

### Community 115 - "CloseWavFile"
Cohesion: 0.40
Nodes (6): espeak_EVENT, FILE, CloseWavFile(), OpenWavFile(), SynthCallback(), Write4Bytes()

### Community 116 - ".createSeekBarPreference"
Cohesion: 0.15
Nodes (5): Parameter, UnitType, Percentage, Punctuation, WordsPerMinute

### Community 117 - "Resonator"
Cohesion: 0.10
Nodes (19): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate (+11 more)

### Community 118 - "Change Log"
Cohesion: 0.17
Nodes (11): 10.0.0 - 2017-06-25, 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06 (+3 more)

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
Cohesion: 0.14
Nodes (15): shouldWarnBeforeWaveformExtraction(), parsePositiveBpm(), ProjectInfoBpmTest, WaveformWarningTest, assertequals, assertfalse, assertnotequals, assertnotnull (+7 more)

### Community 127 - "documentation/README.md"
Cohesion: 0.24
Nodes (6): Build setup, Open and build, Requirements, Coverage, Regular checks, Testing and coverage

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 130 - "LyricsNotepad"
Cohesion: 0.20
Nodes (9): LyricsNotepadTest, Modifier, LyricsNotepad(), Feature Roadmap, Guiding rules, Non-goals, Phase 1 — Barebones notepad (MVP), Phase 2 — Songwriting environment (+1 more)

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "espeak_ng_CompileMbrolaVoice"
Cohesion: 0.29
Nodes (7): basename(), espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, espeak_ng_CompileMbrolaVoice(), FILE, Write4Bytes()

### Community 134 - "espeak_ng_CompilePhonemeDataPath"
Cohesion: 0.30
Nodes (15): espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context(), espeak_ng_CompileIntonation(), espeak_ng_CompileIntonationPath(), espeak_ng_CompilePhonemeData(), espeak_ng_CompilePhonemeDataPath() (+7 more)

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - ".cancelDownloadRestoresControlsWithoutInstalling"
Cohesion: 0.20
Nodes (8): ByteArrayInputStream, ByteArrayInputStream, dictionaryArchive(), ByteArray, DictionaryDownloadsTest, InputStream, ByteArray, InputStream

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
Cohesion: 0.33
Nodes (9): category_group, codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category(), ucd_lookup_category_group(), get_category_group_string() (+1 more)

### Community 145 - "create_dict_corpus_file.py"
Cohesion: 0.40
Nodes (3): argparse, mmap, shutil

### Community 146 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 147 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.33
Nodes (10): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), jbyteArray (+2 more)

### Community 148 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.31
Nodes (10): ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage() (+2 more)

### Community 150 - "LoadSoundFile"
Cohesion: 0.29
Nodes (7): FILE, Read4Bytes(), espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, LoadSoundFile(), LoadSoundFile2(), LookupSoundicon()

### Community 151 - "android/gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 153 - "Using eSpeak NG as a library"
Cohesion: 0.50
Nodes (4): Additional info, Error solutions, Simple steps, Using eSpeak NG as a library

### Community 154 - "espeak-ng"
Cohesion: 0.67
Nodes (4): data, espeak-ng, installer, libespeak-ng

### Community 155 - "Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?, Source Nodes

### Community 157 - "ReadClause"
Cohesion: 0.31
Nodes (13): AnnouncePunctuation(), Translator, CheckPhonemeMode(), DecodeWithPhonemeMode(), Eof(), GetC(), IsRomanU(), LookupCharName() (+5 more)

### Community 171 - "LoadLanguageOptions"
Cohesion: 0.20
Nodes (12): DecodeRule(), print_dictionary_flags(), MNEM_TAB, Translator, CheckTranslator(), LoadLanguageOptions(), LookupTune(), MNEM_TAB (+4 more)

### Community 172 - "phoneme_add_feature"
Cohesion: 0.40
Nodes (5): phoneme_feature_t, espeak_ng_STATUS, PHONEME_TAB, phoneme_add_feature(), phoneme_feature_from_string()

### Community 173 - "file"
Cohesion: 0.11
Nodes (21): abs, after, ExampleInstrumentedTest, audioformat, AudioManager, ByteBuffer, byteorder, cancellationexception (+13 more)

### Community 174 - "eSpeak NG offline IPA fallback"
Cohesion: 0.18
Nodes (9): API and verification, Clean checkout and build, eSpeak NG offline IPA fallback, Regenerate the packaged data, Source and licensing, Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code (+1 more)

### Community 176 - "BeatPlaybackServiceTest"
Cohesion: 0.24
Nodes (7): BeatPlaybackServiceTest, ServiceConnection, ComponentName, IBinder, Notification, ServiceConnection, T

### Community 177 - "LoadVoice"
Cohesion: 0.11
Nodes (26): GetFileLength(), LookupEnvelopeName(), LoadDictionary(), Reverse4Bytes(), LoadConfig(), LookupMnem(), check_data_path(), espeak_ng_InitializePath() (+18 more)

### Community 178 - "Ghostwriter documentation"
Cohesion: 0.67
Nodes (3): Architecture, Current limits, Ghostwriter documentation

### Community 179 - "EspeakIpaInstrumentedTest.kt"
Cohesion: 0.32
Nodes (5): ContextWrapper, Context, ContextWrapper, assertsame, AssetManager

### Community 181 - "Diacritics"
Cohesion: 0.25
Nodes (8): Articulation, Co-articulation, Consonant Release, Diacritics, Phonation, Rounding and Labialization, Syllabicity, Tongue Root

### Community 182 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 184 - "FilterInputStream"
Cohesion: 0.33
Nodes (4): FilterInputStream, ByteArray, FilterInputStream, FilterInputStream

### Community 185 - "Consonants"
Cohesion: 0.33
Nodes (6): Consonants, Gemination, Manner of Articulation, Other Symbols, Place of Articulation, Voice

### Community 186 - "espeak_ng_InitializeOutput"
Cohesion: 0.40
Nodes (5): espeak_ng_OUTPUT_MODE, fifo_init(), espeak_ng_InitializeOutput(), espeak_ng_SetPhonemeEvents(), test_espeak_ng_phoneme_events()

## Knowledge Gaps
- **459 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+454 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 926 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **42 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Text to Phoneme Translation` connect `tr_languages.c` to `index.md`?**
  _High betweenness centrality (0.074) - this node is a cross-community bridge._
- **Why does `SelectTranslator()` connect `tr_languages.c` to `LoadVoice`, `SelectPhonemeTable`?**
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
  _Cohesion score 0.11738648947951273 - nodes in this community are weakly interconnected._