# Graph Report - Gh0stwrit3r  (2026-09-27)

## Corpus Check
- 294 files · ~541,543 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2052 file(s) not represented in the graph (top: (none) 1892, .xml 79, .test 17)

## Summary
- 3242 nodes · 8152 edges · 177 communities (141 shown, 36 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 1218 edges (avg confidence: 0.84)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b7aab274`
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
- DictionaryDownloadsTest.kt
- WaveformExtractor
- android.content.Context
- .writeText
- gradlew
- Language Attributes
- WaveformView.kt
- What You Must Do When Invoked
- Steps
- Settings
- EditorScreen.kt
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
- IpaSearchKeys
- Ghostwriter agent instructions
- espeak_api.c
- eSpeakNGWorker
- EditorScreenTest.kt
- SettingsFormatTest
- VoiceSettingsTest
- speech.c
- tests/encoding.c
- Synthesize
- MainActivity.kt
- strncpy0
- fifo.c
- wavegen.c
- demo.js
- eSpeakActivity
- TranslateWord3
- ttsengine.cpp
- DictionarySearch.kt
- ucd_properties
- mbrowrap.c
- DictionaryScreenTest.kt
- tests/readclause.c
- espeak_Initialize
- Phoneme Features and the International Phonetic Alphabet
- VoiceVariantPreference.java
- spect.c
- ucd.h
- LoadVoice
- Phoneme Model
- eSpeakService.c
- BeatPlayerPanel
- espeak_ng_PrintStatusCodeMessage
- ProjectStorageBeatTest
- GhostwriterTheme
- FrameManagerImpl
- klatt.c
- SelectPhonemeTable
- Diacritics
- .createSeekBarPreference
- BeatPlayerPanel.kt
- speechPlayer.cpp
- SpeechSynthesis
- espeak_ng_CompileDictionary
- TtsService
- tr_languages.c
- index.md
- SSML (Speech Synthesis Markup Language)
- BeatPlaybackService
- ucd_lookup_category
- ProcessSsmlTag
- Lookup
- speechWaveGenerator.cpp
- VoiceVariant
- utf8_in
- ImportVoicePreference.java
- LoadDataFile
- intonation.c
- .isTtsLangCode
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- Phoneme Instructions
- CodePoint
- DictionaryArchiveSource
- .excludedWords
- TtsService.java
- espeak_ng_CompilePhonemeDataPath
- printdata.py
- ucd.py
- Rhyme branch review
- Tune Definitions
- Diacritics
- SpeechWaveGeneratorImpl
- ContextWrapper
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- Row
- BeatPlayer.kt
- emoji
- BeatPlayerInstrumentedTest
- espeak-ng.c
- SeekBarPreference
- Resonator
- Change Log
- espeak_SetUriCallback
- ProjectStorage
- uprintf
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- test
- documentation/README.md
- Third-party software and dictionary data
- Phoneme Tables
- SynthCallback
- printucddata_cpp.cpp
- Vowels
- main
- inputstream
- ParallelFormantGenerator
- common
- EspeakIpaInstrumentedTest
- DictionaryInstaller.kt
- DictionaryHeadword
- .excludedWords
- Unicode Character Database Tools
- EspeakIpa
- eSpeak NG user guide
- Feature Roadmap
- create_dict_corpus_file.py
- Translation fuzzers
- utf8_out
- Diacritics
- ucd_lookup_category_group
- phoneme_add_feature
- android/gradlew
- bytearrayinputstream
- Using eSpeak NG as a library
- espeak-ng
- Contribution Guide
- espeakng_glue.cpp
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- composable
- rgroup_sorter
- ucd_tolower
- Third-party software and dictionary data
- CheckVoiceDataTest
- BeatPlaybackServiceTest
- row
- ProjectLyricsStorage

## God Nodes (most connected - your core abstractions)
1. `BeatPlayer` - 49 edges
2. `SpeechSynthesis` - 49 edges
3. `GhostwriterTheme()` - 42 edges
4. `ucd_properties()` - 38 edges
5. `ProjectStorage` - 33 edges
6. `create_text_decoder()` - 31 edges
7. `text_decoder_eof()` - 31 edges
8. `espeak_Initialize()` - 31 edges
9. `ProjectStorageTest` - 30 edges
10. `ReadClause()` - 30 edges

## Surprising Connections (you probably didn't know these)
- `6. Implement rhyme queries and editor behavior` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/RHYME_DETECTION_PLAN.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `API and verification` --references--> `EspeakIpaInstrumentedTest`  [INFERRED]
  documentation/ESPEAK_NATIVE.md → app/src/androidTest/java/com/prosincerity/ghostwriter/data/EspeakIpaInstrumentedTest.kt
- `Phase 2 — Songwriting environment` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `Phase 2 — Songwriting environment` --references--> `EditorScreen()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/screens/EditorScreen.kt
- `Installing` --references--> `eSpeakActivity`  [INFERRED]
  third_party/espeak-ng/docs/building.md → third_party/espeak-ng/android/src/com/reecedunn/espeak/eSpeakActivity.java

## Import Cycles
- None detected.

## Communities (177 total, 36 thin omitted)

### Community 1 - "Android"
Cohesion: 0.10
Nodes (20): Android, Building, Building, Building eSpeak NG, Building with Gradle, Cross Compilation, Dependencies, Dependencies (+12 more)

### Community 2 - "synthesize.c"
Cohesion: 0.09
Nodes (50): SPEED_FACTORS, voice_t, SetSpeed(), SetSpeedFactors(), SetSpeedMods(), SetSpeedMultiplier(), FILE, PHONEME_LIST (+42 more)

### Community 3 - "ProjectMetadata"
Cohesion: 0.13
Nodes (8): ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, ProjectInfoDialog(), trimmedOrNull(), ProjectMetadataTest, jsonarray

### Community 6 - "DictionaryScreen.kt"
Cohesion: 0.09
Nodes (47): add, alertdialog, HomeScreen(), NewProjectDialog(), RenameProjectDialog(), arrowback, backhandler, check (+39 more)

### Community 7 - "DictionaryDownloadsTest.kt"
Cohesion: 0.10
Nodes (20): ByteArrayInputStream, dictionaryArchive(), ByteArray, Context, ContextWrapper, T, withDictionaryTestContext(), ContextWrapper (+12 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.11
Nodes (6): WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest, MediaFormat

### Community 9 - "android.content.Context"
Cohesion: 0.11
Nodes (18): android.app.Application, android.content.Context, android.os.Bundle, android.preference.Preference, android.preference.Preference.OnPreferenceChangeListener, android.preference.PreferenceActivity, android.preference.PreferenceFragment, android.preference.PreferenceGroup (+10 more)

### Community 10 - ".writeText"
Cohesion: 0.14
Nodes (5): IntArray, StagedFileWriter, IntArray, WaveformCache, ioexception

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 14 - "WaveformView.kt"
Cohesion: 0.06
Nodes (31): canvas, ceil, cliptobounds, detectdraggestures, detecttapgestures, detecttransformgestures, drawtext, fillmaxheight (+23 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "Steps"
Cohesion: 0.20
Nodes (10): 1. Pin the release inputs, 2. Download, install, and read the SQLite databases, 3. Build a minimal eSpeak NG 1.52 Android library, 4. Add the IPA-only JNI wrapper and prove it on Android, 5. Join database lookup and runtime fallback, 6. Implement rhyme queries and editor behavior, 7. Licensing, attribution, and release verification, Goal and data flow (+2 more)

### Community 20 - "EditorScreen.kt"
Cohesion: 0.09
Nodes (25): activityresultcontracts, ComponentName, IBinder, ServiceConnection, rememberBeatPlayback(), ServiceConnection, ReassignBeatDialog(), displayNameFor() (+17 more)

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
Nodes (23): Translator, CalcLengths(), DoEmbedded2(), FMT_PARAMS, frameref_t, PHONEME_DATA, PHONEME_LIST, PHONEME_TAB (+15 more)

### Community 31 - "EditorScreen"
Cohesion: 0.31
Nodes (4): EditorScreenTest, IntArray, EditorScreen(), MutableState

### Community 32 - "TextToSpeechTestCase"
Cohesion: 0.16
Nodes (9): android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, TextToSpeechTest, Override, SuppressWarnings, TextToSpeechTestCase, Exception, Voice (+1 more)

### Community 33 - "compiledata.c"
Cohesion: 0.21
Nodes (35): PHONEME_TAB_LIST, StringToWord(), CompileContext, PHONEME_TAB, CallPhoneme(), CheckNextChar(), CompileElif(), CompileElse() (+27 more)

### Community 34 - "IpaSearchKeys"
Cohesion: 0.15
Nodes (5): SQLiteDatabase, SearchPrefix, IpaSearchKeys, Keys, IpaSearchKeysTest

### Community 35 - "Ghostwriter agent instructions"
Cohesion: 0.33
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 36 - "espeak_api.c"
Cohesion: 0.21
Nodes (16): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, ESPEAK_API, espeak_ng_STATUS, espeak_VOICE, wchar_t (+8 more)

### Community 37 - "eSpeakNGWorker"
Cohesion: 0.18
Nodes (8): espeak_VOICE, eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices, espeak_GetCurrentVoice()

### Community 38 - "EditorScreenTest.kt"
Cohesion: 0.10
Nodes (31): abs, after, androidjunit4, Context, ExampleInstrumentedTest, async, audioformat, ByteBuffer (+23 more)

### Community 41 - "speech.c"
Cohesion: 0.13
Nodes (18): audio, common, dirent, encoding, errno, espeak_ng, io, limits (+10 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.19
Nodes (50): espeak_ng_ENCODING, espeak_ng_TEXT_DECODER, espeak_ng_STATUS, wchar_t, create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc() (+42 more)

### Community 43 - "Synthesize"
Cohesion: 0.09
Nodes (42): espeak_POSITION_TYPE, espeak_VOICE, t_espeak_command, wchar_t, create_espeak_char(), create_espeak_key(), create_espeak_mark(), create_espeak_punctuation_list() (+34 more)

### Community 44 - "MainActivity.kt"
Cohesion: 0.08
Nodes (30): AboutScreenTest, About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, Bundle (+22 more)

### Community 45 - "strncpy0"
Cohesion: 0.22
Nodes (20): strncpy0(), espeak_VOICE, SetVoiceStack(), espeak_ng_STATUS, voice_t, DoVoiceChange(), ESPEAK_API, ESPEAK_NG_API (+12 more)

### Community 46 - "fifo.c"
Cohesion: 0.08
Nodes (37): assert, espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, libgen, pthread, add_time_in_ms(), espeak_EVENT (+29 more)

### Community 47 - "wavegen.c"
Cohesion: 0.07
Nodes (44): espeak_ng_OUTPUT_HOOKS, sonic, ESPEAK_NG_API, espeak_ng_SetRandSeed(), espeak_rand(), espeak_srand(), espeak_ng_STATUS, SetParameter() (+36 more)

### Community 48 - "demo.js"
Cohesion: 0.07
Nodes (27): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+19 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.09
Nodes (21): android.content.BroadcastReceiver, android.content.Intent, android.os.Handler, android.os.Message, android.view.Menu, android.view.MenuItem, intentfilter, java.lang.ref.WeakReference (+13 more)

### Community 50 - "TranslateWord3"
Cohesion: 0.13
Nodes (31): IsAlpha(), IsBracket(), IsDigit(), IsSpace(), AppendPhonemes(), GetVowelStress(), SetWordStress(), TranslateRules() (+23 more)

### Community 51 - "ttsengine.cpp"
Cohesion: 0.09
Nodes (36): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+28 more)

### Community 52 - "DictionarySearch.kt"
Cohesion: 0.16
Nodes (13): PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, sourceLabel(), coroutinecontext, cursor, dispatchers (+5 more)

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (27): poll, procfs, signal, stdarg, BOOL, close_mbrola(), close_pipes(), create_pipes() (+19 more)

### Community 55 - "DictionaryScreenTest.kt"
Cohesion: 0.12
Nodes (24): activitynotfoundexception, assertisenabled, assertisnotenabled, asserttextcontains, continuation, createandroidcomposerule, createcomposerule, ghostwriter_repository_url (+16 more)

### Community 56 - "tests/readclause.c"
Cohesion: 0.11
Nodes (44): phoneme, readclause, speech, synthesize, AnnouncePunctuation(), Translator, CheckPhonemeMode(), clause_type_from_codepoint() (+36 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.22
Nodes (25): espeak_AUDIO_OUTPUT, espeak_POSITION_TYPE, espeak_Initialize(), espeak_SetVoiceByName(), espeak_Synth(), espeak_EVENT, main(), test_espeak_initialize() (+17 more)

### Community 58 - "Phoneme Features and the International Phonetic Alphabet"
Cohesion: 0.10
Nodes (20): Backness, Consonants, Gemination, Height, Intonation, Length, Manner of Articulation, Other Symbols (+12 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.12
Nodes (22): adapterview, android.app.Activity, android.util.Pair, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter, android.widget.TextView (+14 more)

### Community 60 - "spect.c"
Cohesion: 0.14
Nodes (17): ieee80, osbyteorder, SpectFrame, SpectSeq, ieee_extended_to_double(), espeak_ng_STATUS, FILE, GetFrameLength() (+9 more)

### Community 61 - "ucd.h"
Cohesion: 0.18
Nodes (18): category, property, script, DecodePhonemes(), get_script_string(), codepoint_t, isalpha(), isblank() (+10 more)

### Community 62 - "LoadVoice"
Cohesion: 0.09
Nodes (34): GetFileLength(), LookupEnvelopeName(), DecodeRule(), print_dictionary_flags(), MNEM_TAB, Translator, CheckTranslator(), LoadLanguageOptions() (+26 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (26): JavaVM, jboolean, jclass, jstring, espeak_EVENT, jint, JNICALL, JNIEnv (+18 more)

### Community 65 - "BeatPlayerPanel"
Cohesion: 0.16
Nodes (9): BeatComponentsTest, BeatPlaybackControls(), BeatPlayerPanel(), CenteredPlayerContent(), IntArray, Modifier, IntArray, Modifier (+1 more)

### Community 66 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.31
Nodes (10): ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage() (+2 more)

### Community 68 - "GhostwriterTheme"
Cohesion: 0.14
Nodes (15): LyricsNotepadTest, DictionaryScreenTest, HomeScreenTest, DictionaryMatch, DictionarySearchMode, ASSONANCE, RHYME, WORD_PREFIX (+7 more)

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "klatt.c"
Cohesion: 0.10
Nodes (39): klatt_frame_ptr, resonator_ptr, antiresonator(), frame_t, voice_t, WGEN_DATA, DBtoLIN(), flutter() (+31 more)

### Community 71 - "SelectPhonemeTable"
Cohesion: 0.11
Nodes (29): PHONEME_DATA, PHONEME_LIST, PHONEME_LIST2, PHONEME_TAB, Translator, WORD_PH_DATA, MakePhonemeList(), ReInterpretPhoneme() (+21 more)

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 73 - ".createSeekBarPreference"
Cohesion: 0.15
Nodes (5): Parameter, UnitType, Percentage, Punctuation, WordsPerMinute

### Community 74 - "BeatPlayerPanel.kt"
Cohesion: 0.08
Nodes (27): alignment, WaveformZoomButton(), arrangement, box, card, columnscope, delay, icons (+19 more)

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.14
Nodes (14): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+6 more)

### Community 76 - "SpeechSynthesis"
Cohesion: 0.08
Nodes (3): SpeechSynthesisTest, SpeechSynthesis, SynthReadyCallback

### Community 77 - "espeak_ng_CompileDictionary"
Cohesion: 0.19
Nodes (17): isspace2(), CompileContext, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context(), compile_dictlist_end() (+9 more)

### Community 78 - "TtsService"
Cohesion: 0.12
Nodes (8): android.speech.tts.SynthesisCallback, android.speech.tts.SynthesisRequest, android.speech.tts.TextToSpeechService, CheckVoiceData, Override, Override, SuppressWarnings, TtsService

### Community 79 - "tr_languages.c"
Cohesion: 0.06
Nodes (54): LANGUAGE_OPTIONS, Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation (+46 more)

### Community 80 - "index.md"
Cohesion: 0.22
Nodes (4): eSpeak NG online documentation, Languages, Language Options, Numbers

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - "BeatPlaybackService"
Cohesion: 0.08
Nodes (18): BeatPlaybackService, Callback, Bundle, Context, IBinder, Intent, LocalBinder, AudioFocusRequest (+10 more)

### Community 83 - "ucd_lookup_category"
Cohesion: 0.33
Nodes (15): ucd_lookup_category(), codepoint_t, ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl(), ucd_isdigit(), ucd_isgraph() (+7 more)

### Community 84 - "ProcessSsmlTag"
Cohesion: 0.23
Nodes (19): PARAM_STACK, SSML_STACK, AddNameData(), attr_prosody_value(), attrcmp(), attrcopy_utf8(), attrlookup(), attrnumber() (+11 more)

### Community 85 - "Lookup"
Cohesion: 0.29
Nodes (17): IsDigit09(), Lookup(), Translator, WORD_TAB, CheckDotOrdinal(), CheckThousandsGroup(), hu_number_e(), LookupAccentedLetter() (+9 more)

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.22
Nodes (4): java.util.regex.Pattern, VoiceVariantTest, Override, VoiceVariant

### Community 88 - "utf8_in"
Cohesion: 0.11
Nodes (32): MatchRecord, 1.49.1 - 2017-01-21, Translator, is_str_totally_null(), towlower2(), utf8_in(), utf8_in2(), compile_line() (+24 more)

### Community 89 - "ImportVoicePreference.java"
Cohesion: 0.10
Nodes (20): accessibilityevent, android.os.AsyncTask, android.widget.ProgressBar, arrays, bufferedinputstream, downloadmanager, environment, filefilter (+12 more)

### Community 90 - "LoadDataFile"
Cohesion: 0.11
Nodes (20): FILE, Read4Bytes(), CalculateSample(), Hash8(), LoadDataFile(), LoadEnvelope2(), LoadWavefile(), basename() (+12 more)

### Community 91 - "intonation.c"
Cohesion: 0.27
Nodes (16): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+8 more)

### Community 92 - ".isTtsLangCode"
Cohesion: 0.27
Nodes (3): Override, TextToSpeechServiceTest, TtsServiceTest

### Community 93 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.33
Nodes (10): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), jbyteArray (+2 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.12
Nodes (17): CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, FMT, IfNextVowelAppend (+9 more)

### Community 96 - "DictionaryArchiveSource"
Cohesion: 0.26
Nodes (5): DictionaryInstallerInstrumentedTest, DictionaryArchiveSource, SQLiteDatabase, PronunciationResult, IpaGenerator

### Community 97 - ".excludedWords"
Cohesion: 0.18
Nodes (6): AssonanceFormFilter, Entry, PrefixNode, Pronounced, AssonanceFormFilterTest, Pronounced

### Community 98 - "TtsService.java"
Cohesion: 0.08
Nodes (32): android.annotation.SuppressLint, android.test.AndroidTestCase, anyof, arraylist, assertthat, audiotrack, bundle, checksum (+24 more)

### Community 99 - "espeak_ng_CompilePhonemeDataPath"
Cohesion: 0.25
Nodes (17): espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context(), espeak_ng_CompileIntonation(), espeak_ng_CompileIntonationPath(), espeak_ng_CompilePhonemeData(), espeak_ng_CompilePhonemeDataPath() (+9 more)

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
Cohesion: 0.15
Nodes (11): FrameManager, getCurrentFrame, getLastIndex, queueFrame, SpeechWaveGeneratorImpl, cascade, frameManager, fricGenerator (+3 more)

### Community 106 - "ContextWrapper"
Cohesion: 0.40
Nodes (3): ContextWrapper, Context, ContextWrapper

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

### Community 111 - "Row"
Cohesion: 0.47
Nodes (4): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations, DictionarySearch

### Community 112 - "BeatPlayer.kt"
Cohesion: 0.40
Nodes (4): audioattributes, context, MediaPlayer, powermanager

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 115 - "espeak-ng.c"
Cohesion: 0.17
Nodes (14): fcntl, getopt, gcd(), getopt(), getopt_internal(), getopt_long(), permute_args(), espeak_EVENT (+6 more)

### Community 116 - "SeekBarPreference"
Cohesion: 0.09
Nodes (17): android.content.DialogInterface, android.content.SharedPreferences, android.preference.DialogPreference, android.widget.EditText, android.widget.RadioButton, android.widget.SeekBar, button, editable (+9 more)

### Community 117 - "Resonator"
Cohesion: 0.17
Nodes (11): Resonator, a, anti, b, bandwidth, c, frequency, p1 (+3 more)

### Community 118 - "Change Log"
Cohesion: 0.18
Nodes (10): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06, 9.0.0 - 2016-12-28 (+2 more)

### Community 120 - "ProjectStorage"
Cohesion: 0.20
Nodes (3): MainActivityTest, Context, ProjectStorage

### Community 121 - "uprintf"
Cohesion: 0.23
Nodes (15): get_category_string(), ucd_category, ucd_category_group, ucd_script, ucd_get_category_group_string(), ucd_get_category_string(), ucd_get_script_string(), codepoint_t (+7 more)

### Community 122 - "lb.md"
Cohesion: 0.18
Nodes (10): Build and use the project, Integration, Introduction, lb_emoji, lb_list, lb_rules, Luxembourgish customization, Phoneme inventory (+2 more)

### Community 123 - "MBROLA Voices"
Cohesion: 0.17
Nodes (11): 1. Add MBROLA voice definition file, 2. Add MBROLA phoneme translation file, 3. Compile voice and update Makefile.am file, Adding new MBROLA voice entry to eSpeak NG, Installation of MBROLA package from source, Installation of standard packages, Linux Installation, MBROLA Voices (+3 more)

### Community 124 - "Kirshenbaum (ASCII-IPA) Transcription Scheme"
Cohesion: 0.18
Nodes (11): Consonants, Diacritics, Kirshenbaum (ASCII-IPA) Transcription Scheme, Length, Other Symbols, Other Symbols, Phoneme Transcription Schemes, References (+3 more)

### Community 125 - "CascadeFormantGenerator"
Cohesion: 0.18
Nodes (10): CascadeFormantGenerator, r1, r2, r3, r4, r5, r6, rN0 (+2 more)

### Community 126 - "test"
Cohesion: 0.15
Nodes (15): shouldWarnBeforeWaveformExtraction(), parsePositiveBpm(), ProjectInfoBpmTest, WaveformWarningTest, assertequals, assertfalse, assertnotequals, assertnotnull (+7 more)

### Community 127 - "documentation/README.md"
Cohesion: 0.16
Nodes (9): Architecture, Current limits, Ghostwriter documentation, Build setup, Open and build, Requirements, Coverage, Regular checks (+1 more)

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 129 - "Phoneme Tables"
Cohesion: 0.12
Nodes (16): Attributes, Conditional Statements, Conditions, Customization of sound source files, endtype, lengthmod, Phoneme Definitions, Phoneme Files (+8 more)

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "main"
Cohesion: 0.10
Nodes (28): espeak_ng_OUTPUT_MODE, main(), PrintVersion(), espeak_PARAMETER, espeak_SetParameter(), espeak_PARAMETER, create_espeak_parameter(), t_espeak_callback (+20 more)

### Community 135 - "ParallelFormantGenerator"
Cohesion: 0.22
Nodes (8): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - "EspeakIpaInstrumentedTest"
Cohesion: 0.25
Nodes (6): EspeakIpaInstrumentedTest, API and verification, Clean checkout and build, eSpeak NG offline IPA fallback, Regenerate the packaged data, Source and licensing

### Community 138 - "DictionaryInstaller.kt"
Cohesion: 0.13
Nodes (17): DictionaryAsset, DictionaryDownloadProgress, DictionaryInstaller, FilterInputStream, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK (+9 more)

### Community 139 - "DictionaryHeadword"
Cohesion: 0.19
Nodes (3): DictionaryHeadword, DictionaryHeadwordTest, normalizer

### Community 140 - ".excludedWords"
Cohesion: 0.33
Nodes (4): CompoundRhymeFilter, Entry, Pronounced, CompoundRhymeFilterTest

### Community 141 - "Unicode Character Database Tools"
Cohesion: 0.25
Nodes (7): Bugs, Build Dependencies, Building, Debian, License Information, Unicode Character Database Tools, Updating the UCD Data

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 144 - "Feature Roadmap"
Cohesion: 0.40
Nodes (5): Feature Roadmap, Guiding rules, Non-goals, Phase 1 — Barebones notepad (MVP), Phase 2 — Songwriting environment

### Community 145 - "create_dict_corpus_file.py"
Cohesion: 0.40
Nodes (3): argparse, mmap, shutil

### Community 146 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 147 - "utf8_out"
Cohesion: 0.60
Nodes (6): utf8_out(), PHONEME_LIST, PHONEME_TAB, GetTranslatedPhonemeString(), WritePhMnemonic(), WritePhMnemonicWithStress()

### Community 148 - "Diacritics"
Cohesion: 0.20
Nodes (10): Air Flow, Articulation, Co-articulation, Consonant Release, Diacritics, Fortis and Lenis, Phonation, Rounding and Labialization (+2 more)

### Community 149 - "ucd_lookup_category_group"
Cohesion: 0.31
Nodes (8): category_group, codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category_group(), get_category_group_string(), lookup_category_group()

### Community 150 - "phoneme_add_feature"
Cohesion: 0.40
Nodes (5): phoneme_feature_t, espeak_ng_STATUS, PHONEME_TAB, phoneme_add_feature(), phoneme_feature_from_string()

### Community 151 - "android/gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 153 - "Using eSpeak NG as a library"
Cohesion: 0.50
Nodes (4): Additional info, Error solutions, Simple steps, Using eSpeak NG as a library

### Community 154 - "espeak-ng"
Cohesion: 0.67
Nodes (4): data, espeak-ng, installer, libespeak-ng

### Community 156 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 171 - "composable"
Cohesion: 0.11
Nodes (14): LongBeatWarningDialog(), WaveformMarkerDialog(), formatPlaybackTime(), EditorMarkerDialogs(), composable, darkcolorscheme, issystemindarktheme, keyboardcapitalization (+6 more)

### Community 173 - "ucd_tolower"
Cohesion: 0.18
Nodes (11): stddef, 6.2.0 - 2013-10-16, codepoint_t, ucd_tolower(), ucd_totitle(), ucd_toupper(), totitle(), toupper() (+3 more)

### Community 174 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 175 - "CheckVoiceDataTest"
Cohesion: 0.33
Nodes (3): android.test.ActivityUnitTestCase, java.lang.reflect.Field, CheckVoiceDataTest

### Community 176 - "BeatPlaybackServiceTest"
Cohesion: 0.28
Nodes (6): BeatPlaybackServiceTest, ServiceConnection, ComponentName, IBinder, ServiceConnection, T

## Knowledge Gaps
- **456 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+451 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 907 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **36 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Text to Phoneme Translation` connect `tr_languages.c` to `index.md`?**
  _High betweenness centrality (0.064) - this node is a cross-community bridge._
- **Why does `SpeechSynthesis` connect `SpeechSynthesis` to `TtsService.java`, `VoiceSettingsTest`, `android.content.Context`, `.createSeekBarPreference`, `TtsService`, `SeekBarPreference`?**
  _High betweenness centrality (0.052) - this node is a cross-community bridge._
- **Are the 22 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 22 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _456 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.10695187165775401 - nodes in this community are weakly interconnected._
- **Should `Android` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._
- **Should `synthesize.c` be split into smaller, more focused modules?**
  _Cohesion score 0.09098039215686274 - nodes in this community are weakly interconnected._