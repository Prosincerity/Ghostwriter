# Graph Report - Gh0stwrit3r  (2026-09-30)

## Corpus Check
- 339 files · ~561,146 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2058 file(s) not represented in the graph (top: (none) 1892, .xml 82, .test 17)

## Summary
- 3788 nodes · 9688 edges · 195 communities (156 shown, 39 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 1397 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b3f6113f`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- .configure
- Generate
- ProjectStorageBeatTest
- ProjectStorageTest
- WaveformViewport
- HomeScreen.kt
- file
- WaveformExtractor
- TextToSpeechTestCase
- VoiceSettingsTest
- gradlew
- ucd_lookup_category
- WaveformView.kt
- What You Must Do When Invoked
- android.content.Context
- event.c
- BeatPlaybackService.kt
- graphify reference: extra exports and benchmark
- SetSpeed
- graphify reference: query, path, explain
- graphify reference: add a URL and watch a folder
- graphify reference: commit hook and native CLAUDE.md integration
- graphify reference: incremental update and cluster-only
- graphify reference: GitHub clone and cross-repo merge
- graphify reference: transcribe video and audio
- extraction-spec.md
- Android
- EditorScreenTest
- compiledict.c
- compiledata.c
- .fromIpa
- PcmRingBuffer
- TranslateClauseWithTerminator
- DictionaryInstaller.kt
- tests/readclause.c
- WaveformMarker
- SpeechSynthesis
- speech.c
- tests/encoding.c
- Synthesize
- TestConnection
- MainActivity.kt
- tr_languages.c
- wavegen.c
- demo.js
- eSpeakActivity
- .excludedWords
- ttsengine.cpp
- SystemFontCatalogTest.kt
- ucd_properties
- mbrowrap.c
- EditorScreenTest.kt
- WaveformViewTest.kt
- espeak_Initialize
- Diacritics
- VoiceVariantPreference.java
- MarkerLoopFrames
- dictionary.c
- .writeText
- Phoneme Model
- eSpeakService.c
- EditorScreen.kt
- Language Attributes
- espeak
- PlaybackFocusState
- FrameManagerImpl
- EditorScreen
- LyricTextSettings
- Diacritics
- SettingsFormatTest.kt
- PronunciationResult
- speechPlayer.cpp
- TtsService
- .excludedWords
- .isTtsLangCode
- DictionarySearch.kt
- index.md
- SSML (Speech Synthesis Markup Language)
- composable
- Row
- numbers.md
- .parse
- speechWaveGenerator.cpp
- VoiceVariant
- voices.c
- ucd_lookup_category_group
- espeak_api.c
- intonation.c
- PlaybackNotificationUpdater
- main
- Phoneme Instructions
- CodePoint
- SeekBarPreference
- .renderWaveform
- TtsService.java
- withDictionaryTestContext
- printdata.py
- ucd.py
- ucd.h
- Tune Definitions
- Diacritics
- SpeechWaveGeneratorImpl
- BeatPlaybackService
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- BeatPlayerInstrumentedTest
- SettingsScreen.kt
- emoji
- CheckVoiceDataTest
- espeak-ng.c
- uprintf
- Resonator
- Change Log
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- MainActivityTest
- DictionaryHeadword
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- test
- BeatPlayerPanel.kt
- Third-party software and dictionary data
- MarkerLoopRole
- ProjectStorage
- printucddata_cpp.cpp
- Vowels
- unpackDictionaryArchive
- Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability.
- GhostwriterTheme
- common
- ParallelFormantGenerator
- ssml.c
- DictionarySearch
- WaveformCache
- LyricsNotepad
- waveformMarkerHitBounds
- eSpeak NG user guide
- ContextWrapper
- create_dict_corpus_file.py
- PcmLoopRenderer
- eSpeak NG Text-to-Speech
- Contribution Guide
- Dictionaries
- eSpeakNGWorker
- android/gradlew
- EspeakIpa
- Using eSpeak NG as a library
- espeak-ng
- Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?
- Ghostwriter agent instructions
- Native pronunciation engine
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- InputStream
- Testing
- Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?
- Translation fuzzers
- SmoothLoopPlayback
- Third-party software and dictionary data
- BeatPlaybackServiceTest
- Features and roadmap
- DictionarySearchMode
- Callback
- row
- PlaybackFrameLedger
- Releasing
- httpurlconnection
- Ghostwriter
- Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?
- Dictionary data license and attribution
- espeak_ng_PrintStatusCodeMessage
- espeak_SetPhonemeCallback
- Settings
- espeak_callback
- documentation/README.md
- PcmLoopRendererTest.kt
- BeatPlaybackServiceTest.kt
- SynthCallback

## God Nodes (most connected - your core abstractions)
1. `BeatPlayer` - 60 edges
2. `WaveformMarker` - 55 edges
3. `GhostwriterTheme()` - 53 edges
4. `SpeechSynthesis` - 49 edges
5. `ucd_properties()` - 38 edges
6. `LyricTextSettings` - 33 edges
7. `ProjectStorage` - 33 edges
8. `BeatPlaybackService` - 33 edges
9. `create_text_decoder()` - 31 edges
10. `text_decoder_eof()` - 31 edges

## Surprising Connections (you probably didn't know these)
- `Projects and settings` --references--> `LyricTextSettings`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/data/LyricTextSettings.kt
- `Playback and waveforms` --references--> `BeatPlayer`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/BeatPlayer.kt
- `Playback and waveforms` --references--> `PcmLoopRenderer`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/PcmLoopRenderer.kt
- `Playback and waveforms` --references--> `PcmRingBuffer`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/PcmRingBuffer.kt
- `Playback and waveforms` --references--> `SmoothLoopPlayback`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/SmoothLoopPlayback.kt

## Import Cycles
- None detected.

## Communities (195 total, 39 thin omitted)

### Community 0 - "BeatPlayer"
Cohesion: 0.10
Nodes (4): BeatPlayer, BeatPlayerTest, MediaPlayer, PowerManager

### Community 1 - ".configure"
Cohesion: 0.16
Nodes (13): Building, Cross Compilation, eSpeak NG Feature Configuration, Extended Dictionary Configuration, LLVM Fuzzer Support, Sanitizer Flag Configuration, Bugs, Build Dependencies (+5 more)

### Community 2 - "Generate"
Cohesion: 0.07
Nodes (56): FILE, PHONEME_LIST, PHONEME_TAB, GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), WritePitch(), FMT_PARAMS (+48 more)

### Community 6 - "HomeScreen.kt"
Cohesion: 0.12
Nodes (15): add, HomeScreenTest, HomeScreen(), NewProjectDialog(), RenameProjectDialog(), clickable, delete, imeaction (+7 more)

### Community 7 - "file"
Cohesion: 0.09
Nodes (30): after, androidjunit4, ContextWrapper, Context, ContextWrapper, ExampleInstrumentedTest, assertarrayequals, assertnull (+22 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.11
Nodes (5): WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest

### Community 9 - "TextToSpeechTestCase"
Cohesion: 0.16
Nodes (9): android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, TextToSpeechTest, Override, SuppressWarnings, TextToSpeechTestCase, Exception, Voice (+1 more)

### Community 10 - "VoiceSettingsTest"
Cohesion: 0.22
Nodes (6): android.content.SharedPreferences, jsonexception, jsonobject, org.json.JSONObject, VoiceSettingsTest, VoiceSettings

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "ucd_lookup_category"
Cohesion: 0.33
Nodes (15): ucd_lookup_category(), codepoint_t, ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl(), ucd_isdigit(), ucd_isgraph() (+7 more)

### Community 14 - "WaveformView.kt"
Cohesion: 0.08
Nodes (24): ceil, cliptobounds, detectdraggestures, detecttapgestures, detecttransformgestures, drawtext, fillmaxheight, floor (+16 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "android.content.Context"
Cohesion: 0.05
Nodes (44): accessibilityevent, android.app.Application, android.content.Context, android.content.DialogInterface, android.os.AsyncTask, android.os.Bundle, android.preference.DialogPreference, android.preference.Preference (+36 more)

### Community 19 - "event.c"
Cohesion: 0.11
Nodes (31): espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, pthread, add_time_in_ms(), espeak_EVENT, espeak_ng_STATUS, clock_gettime2() (+23 more)

### Community 20 - "BeatPlaybackService.kt"
Cohesion: 0.09
Nodes (21): Context, IBinder, Intent, LocalBinder, audioattributes, AudioFocusRequest, audiotrack, Binder (+13 more)

### Community 21 - "graphify reference: extra exports and benchmark"
Cohesion: 0.22
Nodes (8): graphify reference: extra exports and benchmark, Step 6b - Wiki (only if --wiki flag), Step 7 - Neo4j export (only if --neo4j or --neo4j-push flag), Step 7a - FalkorDB export (only if --falkordb or --falkordb-push flag), Step 7b - SVG export (only if --svg flag), Step 7c - GraphML export (only if --graphml flag), Step 7d - MCP server (only if --mcp flag), Step 8 - Token reduction benchmark (only if total_words > 5000)

### Community 22 - "SetSpeed"
Cohesion: 0.18
Nodes (11): SPEED_FACTORS, Translator, voice_t, CalcLengths(), DoEmbedded2(), SetSpeed(), SetSpeedFactors(), SetSpeedMods() (+3 more)

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

### Community 30 - "Android"
Cohesion: 0.14
Nodes (14): Android, Building, Building eSpeak NG, Building with Gradle, Dependencies, Dependencies, Dependencies, Installing (+6 more)

### Community 31 - "EditorScreenTest"
Cohesion: 0.36
Nodes (3): EditorScreenTest, IntArray, MutableState

### Community 32 - "compiledict.c"
Cohesion: 0.14
Nodes (26): RGROUP, isspace2(), CompileContext, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context() (+18 more)

### Community 33 - "compiledata.c"
Cohesion: 0.07
Nodes (84): phoneme_feature_t, PHONEME_TAB_LIST, SpectFrame, SpectSeq, FILE, Read4Bytes(), StringToWord(), CompileContext (+76 more)

### Community 34 - ".fromIpa"
Cohesion: 0.15
Nodes (3): IpaSearchKeys, Keys, IpaSearchKeysTest

### Community 35 - "PcmRingBuffer"
Cohesion: 0.05
Nodes (24): ByteArray, ShortArray, PcmBeatDecoderTest, PcmSource, ShortArray, ObservedSource, SmoothLoopPlaybackTest, PcmSource (+16 more)

### Community 36 - "TranslateClauseWithTerminator"
Cohesion: 0.09
Nodes (40): Translator, is_str_totally_null(), IsDigit(), IsSpace(), towlower2(), utf8_in2(), PHONEME_DATA, PHONEME_LIST (+32 more)

### Community 37 - "DictionaryInstaller.kt"
Cohesion: 0.16
Nodes (12): DictionaryAsset, DictionaryInstaller, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK, WIKTIONARY, Context (+4 more)

### Community 38 - "tests/readclause.c"
Cohesion: 0.10
Nodes (45): phoneme, readclause, speech, synthesize, AnnouncePunctuation(), Translator, CheckPhonemeMode(), clause_type_from_codepoint() (+37 more)

### Community 39 - "WaveformMarker"
Cohesion: 0.12
Nodes (8): ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, ProjectInfoDialog(), trimmedOrNull(), ProjectMetadataTest, jsonarray

### Community 40 - "SpeechSynthesis"
Cohesion: 0.07
Nodes (7): SpeechSynthesisTest, CheckVoiceData, Override, GetSampleText, Override, SpeechSynthesis, SynthReadyCallback

### Community 41 - "speech.c"
Cohesion: 0.10
Nodes (23): audio, common, emscripten, encoding, errno, espeak_ng, glue, ieee80 (+15 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.20
Nodes (49): espeak_ng_ENCODING, espeak_ng_TEXT_DECODER, espeak_ng_STATUS, wchar_t, create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc() (+41 more)

### Community 43 - "Synthesize"
Cohesion: 0.09
Nodes (43): espeak_POSITION_TYPE, espeak_VOICE, t_espeak_command, wchar_t, create_espeak_char(), create_espeak_key(), create_espeak_mark(), create_espeak_punctuation_list() (+35 more)

### Community 44 - "TestConnection"
Cohesion: 0.11
Nodes (12): FilterInputStream, HttpURLConnection, openDictionaryArchive(), FilterInputStream, DictionaryArchiveSourceTest, ByteArrayInputStream, InputStream, ByteArrayInputStream (+4 more)

### Community 45 - "MainActivity.kt"
Cohesion: 0.10
Nodes (26): About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, Bundle, Context (+18 more)

### Community 46 - "tr_languages.c"
Cohesion: 0.06
Nodes (54): LANGUAGE_OPTIONS, Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation (+46 more)

### Community 47 - "wavegen.c"
Cohesion: 0.05
Nodes (76): espeak_ng_OUTPUT_HOOKS, klatt_frame_ptr, resonator_ptr, sonic, espeak_rand(), antiresonator(), frame_t, voice_t (+68 more)

### Community 48 - "demo.js"
Cohesion: 0.21
Nodes (5): ipa(), PushAudioNode(), speak(), speakAndIpa(), stop()

### Community 49 - "eSpeakActivity"
Cohesion: 0.09
Nodes (22): android.content.BroadcastReceiver, android.content.Intent, android.os.Handler, android.os.Message, android.preference.PreferenceActivity, android.view.Menu, android.view.MenuItem, intentfilter (+14 more)

### Community 50 - ".excludedWords"
Cohesion: 0.15
Nodes (7): AssonanceFormFilter, Entry, IntArray, PrefixNode, Pronounced, AssonanceFormFilterTest, Pronounced

### Community 51 - "ttsengine.cpp"
Cohesion: 0.09
Nodes (36): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+28 more)

### Community 52 - "SystemFontCatalogTest.kt"
Cohesion: 0.09
Nodes (29): LyricFontFamily, CURSIVE, MONOSPACE, SANS_SERIF, SERIF, SYSTEM_DEFAULT, LyricTextAlignment, CENTER (+21 more)

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (27): poll, procfs, signal, stdarg, BOOL, close_mbrola(), close_pipes(), create_pipes() (+19 more)

### Community 55 - "EditorScreenTest.kt"
Cohesion: 0.09
Nodes (41): activitynotfoundexception, assertisenabled, assertisnotenabled, asserttextcontains, before, bytearrayinputstream, compositionlocalprovider, contextwrapper (+33 more)

### Community 56 - "WaveformViewTest.kt"
Cohesion: 0.22
Nodes (8): MutableState, click, longclick, offset, onnodewithtag, performtouchinput, safedrawingpadding, swipe

### Community 57 - "espeak_Initialize"
Cohesion: 0.22
Nodes (25): espeak_AUDIO_OUTPUT, espeak_POSITION_TYPE, espeak_Initialize(), espeak_SetVoiceByName(), espeak_Synth(), espeak_EVENT, main(), test_espeak_initialize() (+17 more)

### Community 58 - "Diacritics"
Cohesion: 0.07
Nodes (30): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Consonants, Diacritics, Fortis and Lenis (+22 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.11
Nodes (22): adapterview, android.app.Activity, android.util.Pair, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter, android.widget.TextView (+14 more)

### Community 60 - "MarkerLoopFrames"
Cohesion: 0.25
Nodes (3): MarkerLoopFrames, MarkerLoopRange, MarkerLoopTest

### Community 61 - "dictionary.c"
Cohesion: 0.08
Nodes (72): MatchRecord, IsAlpha(), IsBracket(), IsDigit09(), utf8_in(), utf8_out(), AppendPhonemes(), PHONEME_LIST (+64 more)

### Community 62 - ".writeText"
Cohesion: 0.15
Nodes (3): EspeakIpaInstrumentedTest, ProjectLyricsStorage, StagedFileWriter

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (26): JavaVM, jboolean, jclass, jstring, espeak_EVENT, jint, JNICALL, JNIEnv (+18 more)

### Community 65 - "EditorScreen.kt"
Cohesion: 0.10
Nodes (25): activityresultcontracts, ComponentName, IBinder, ServiceConnection, rememberBeatPlayback(), ServiceConnection, displayNameFor(), Context (+17 more)

### Community 66 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 67 - "espeak"
Cohesion: 0.12
Nodes (16): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+8 more)

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "EditorScreen"
Cohesion: 0.22
Nodes (3): IntArray, EditorScreen(), PendingBeatPreparation

### Community 71 - "LyricTextSettings"
Cohesion: 0.08
Nodes (17): SystemFontCatalogTest, SettingsScreenTest, LyricFont, lyricFontFromPreference(), SystemFontFile, systemFontOptions(), LyricTextSettings, SystemFontCatalog (+9 more)

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 73 - "SettingsFormatTest.kt"
Cohesion: 0.25
Nodes (4): formatPlaybackTime(), formatInterval(), formatTypographyNumber(), SettingsFormatTest

### Community 74 - "PronunciationResult"
Cohesion: 0.15
Nodes (5): SQLiteDatabase, PronunciationResult, DictionarySearchData, DictionarySearchRows, InstalledDictionarySearchData

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.14
Nodes (14): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+6 more)

### Community 76 - "TtsService"
Cohesion: 0.12
Nodes (8): android.speech.tts.SynthesisCallback, android.speech.tts.SynthesisRequest, android.speech.tts.TextToSpeechService, Override, SuppressWarnings, TtsService, Override, Voice

### Community 77 - ".excludedWords"
Cohesion: 0.28
Nodes (4): CompoundRhymeFilter, Entry, Pronounced, CompoundRhymeFilterTest

### Community 78 - ".isTtsLangCode"
Cohesion: 0.27
Nodes (3): Override, TextToSpeechServiceTest, TtsServiceTest

### Community 79 - "DictionarySearch.kt"
Cohesion: 0.13
Nodes (15): PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, sourceLabel(), context, coroutinecontext, cursor (+7 more)

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - "composable"
Cohesion: 0.15
Nodes (11): composable, darkcolorscheme, fillmaxwidth, issystemindarktheme, keyboardcapitalization, keyboardoptions, lightcolorscheme, materialtheme (+3 more)

### Community 83 - "Row"
Cohesion: 0.42
Nodes (3): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.24
Nodes (4): java.util.regex.Pattern, VoiceVariantTest, Override, VoiceVariant

### Community 88 - "voices.c"
Cohesion: 0.08
Nodes (53): dirent, GetFileLength(), strncpy0(), MNEM_TAB, Translator, CheckTranslator(), LoadConfig(), LoadLanguageOptions() (+45 more)

### Community 89 - "ucd_lookup_category_group"
Cohesion: 0.18
Nodes (12): category_group, codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category_group(), get_category_group_string(), lookup_category_group() (+4 more)

### Community 90 - "espeak_api.c"
Cohesion: 0.16
Nodes (20): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, ESPEAK_API, espeak_ng_STATUS, espeak_PARAMETER, espeak_VOICE (+12 more)

### Community 91 - "intonation.c"
Cohesion: 0.25
Nodes (18): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+10 more)

### Community 92 - "PlaybackNotificationUpdater"
Cohesion: 0.33
Nodes (3): PlaybackNotificationUpdater, PlaybackNotificationUpdaterTest, TestQueue

### Community 93 - "main"
Cohesion: 0.09
Nodes (32): espeak_ng_OUTPUT_MODE, main(), PrintVersion(), ESPEAK_NG_API, espeak_ng_SetRandSeed(), espeak_srand(), espeak_PARAMETER, create_espeak_parameter() (+24 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.06
Nodes (33): Attributes, CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, Conditional Statements (+25 more)

### Community 96 - "SeekBarPreference"
Cohesion: 0.09
Nodes (9): android.widget.SeekBar, OnSeekBarChangeListener, Override, SeekBarPreference, Parameter, UnitType, Percentage, Punctuation (+1 more)

### Community 97 - ".renderWaveform"
Cohesion: 0.35
Nodes (4): WaveformViewTest, IntArray, Modifier, WaveformView()

### Community 98 - "TtsService.java"
Cohesion: 0.10
Nodes (28): android.annotation.SuppressLint, android.test.AndroidTestCase, anyof, arraylist, assertthat, bundle, checksum, configuration (+20 more)

### Community 99 - "withDictionaryTestContext"
Cohesion: 0.20
Nodes (10): DictionaryInstallerInstrumentedTest, ByteArrayInputStream, ByteArrayInputStream, dictionaryArchive(), ByteArray, T, withDictionaryTestContext(), DictionaryDownloadsTest (+2 more)

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 101 - "ucd.py"
Cohesion: 0.23
Nodes (4): os, sys, parse_property_mapping(), parse_ucd_data()

### Community 102 - "ucd.h"
Cohesion: 0.15
Nodes (22): category, property, script, get_category_string(), get_script_string(), codepoint_t, isalnum(), isalpha() (+14 more)

### Community 103 - "Tune Definitions"
Cohesion: 0.15
Nodes (13): Clauses, endtune, head, headenv, headextend, headlast, Intonation, nucleus (+5 more)

### Community 104 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Consonant Release, Consonants, Diacritics, Intonation, Length, Manner of Articulation (+14 more)

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

### Community 112 - "SettingsScreen.kt"
Cohesion: 0.09
Nodes (45): alertdialog, LongBeatWarningDialog(), ReassignBeatDialog(), arrangement, arrowback, backhandler, check, column (+37 more)

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 114 - "CheckVoiceDataTest"
Cohesion: 0.33
Nodes (3): android.test.ActivityUnitTestCase, java.lang.reflect.Field, CheckVoiceDataTest

### Community 115 - "espeak-ng.c"
Cohesion: 0.16
Nodes (15): assert, fcntl, getopt, gcd(), getopt(), getopt_internal(), getopt_long(), permute_args() (+7 more)

### Community 116 - "uprintf"
Cohesion: 0.17
Nodes (19): stddef, 6.2.0 - 2013-10-16, codepoint_t, ucd_tolower(), ucd_totitle(), ucd_toupper(), totitle(), toupper() (+11 more)

### Community 117 - "Resonator"
Cohesion: 0.17
Nodes (11): Resonator, a, anti, b, bandwidth, c, frequency, p1 (+3 more)

### Community 118 - "Change Log"
Cohesion: 0.18
Nodes (10): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06, 9.0.0 - 2016-12-28 (+2 more)

### Community 119 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.33
Nodes (10): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), jbyteArray (+2 more)

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
Nodes (12): shouldWarnBeforeWaveformExtraction(), parsePositiveBpm(), ProjectInfoBpmTest, WaveformWarningTest, assertequals, assertfalse, assertnotnull, assertsame (+4 more)

### Community 127 - "BeatPlayerPanel.kt"
Cohesion: 0.08
Nodes (27): alignment, WaveformZoomButton(), box, button, card, columnscope, delay, fillmaxsize (+19 more)

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.33
Nodes (5): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Roboto UI fonts, Third-party software and dictionary data

### Community 129 - "MarkerLoopRole"
Cohesion: 0.24
Nodes (7): MarkerLoopRole, END, NONE, START, replaceLoopMarker(), WaveformMarkerDialog(), EditorMarkerDialogs()

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "unpackDictionaryArchive"
Cohesion: 0.25
Nodes (9): ByteArray, FilterInputStream, unpackDictionaryArchive(), FilterInputStream, DictionaryDownloadProgress, DictionaryArchiveTest, ByteArray, ByteArrayInputStream (+1 more)

### Community 134 - "Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability."
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability., Source Nodes

### Community 135 - "GhostwriterTheme"
Cohesion: 0.12
Nodes (12): BeatComponentsTest, AboutScreenTest, DictionaryScreenTest, DictionarySearchResult, BeatPlaybackControls(), BeatPlayerPanel(), CenteredPlayerContent(), IntArray (+4 more)

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - "ParallelFormantGenerator"
Cohesion: 0.22
Nodes (8): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate

### Community 138 - "ssml.c"
Cohesion: 0.15
Nodes (29): PARAM_STACK, SSML_STACK, LookupMnem(), AddNameData(), espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, LoadSoundFile(), LoadSoundFile2() (+21 more)

### Community 139 - "DictionarySearch"
Cohesion: 0.37
Nodes (4): DictionaryMatch, DictionarySearch, AssonanceData, DictionarySearchTest

### Community 141 - "LyricsNotepad"
Cohesion: 0.43
Nodes (3): LyricsNotepadTest, Modifier, LyricsNotepad()

### Community 142 - "waveformMarkerHitBounds"
Cohesion: 0.39
Nodes (3): ClosedFloatingPointRange, waveformMarkerHitBounds(), WaveformMarkerHitBoundsTest

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 144 - "ContextWrapper"
Cohesion: 0.33
Nodes (3): Context, ContextWrapper, ContextWrapper

### Community 145 - "create_dict_corpus_file.py"
Cohesion: 0.40
Nodes (3): argparse, mmap, shutil

### Community 146 - "PcmLoopRenderer"
Cohesion: 0.25
Nodes (5): PcmLoopRenderer, PcmSource, ShortArray, PcmLoopRendererTest, UnavailableSource

### Community 147 - "eSpeak NG Text-to-Speech"
Cohesion: 0.29
Nodes (7): Acknowledgements, Documentation, eSpeak Compatibility, eSpeak NG Text-to-Speech, Features, History, License Information

### Community 148 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 149 - "Dictionaries"
Cohesion: 0.33
Nodes (6): Dictionaries, Pronunciation lookup, Release inputs and installation, Responsiveness and verification, Search behavior, Using dictionaries

### Community 150 - "eSpeakNGWorker"
Cohesion: 0.20
Nodes (7): espeak_VOICE, eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices

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

### Community 156 - "Ghostwriter agent instructions"
Cohesion: 0.40
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 157 - "Native pronunciation engine"
Cohesion: 0.40
Nodes (5): Native pronunciation engine, Regenerating language data, Runtime contract, Source and Android build, Verification and distribution

### Community 170 - "InputStream"
Cohesion: 0.50
Nodes (3): InputStream, ByteArray, InputStream

### Community 171 - "Testing"
Cohesion: 0.40
Nodes (5): Android tests, Checks without a device, Coverage and regression guidance, Coverage reports, Testing

### Community 172 - "Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?, Source Nodes

### Community 173 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 174 - "SmoothLoopPlayback"
Cohesion: 0.31
Nodes (3): PcmLoopBounds, SmoothLoopPlayback, Transport

### Community 175 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (5): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Roboto UI fonts, Third-party software and dictionary data

### Community 176 - "BeatPlaybackServiceTest"
Cohesion: 0.24
Nodes (7): BeatPlaybackServiceTest, ServiceConnection, ComponentName, IBinder, Notification, ServiceConnection, T

### Community 177 - "Features and roadmap"
Cohesion: 0.50
Nodes (4): Features and roadmap, Implemented, Non-goals, Planned

### Community 178 - "DictionarySearchMode"
Cohesion: 0.16
Nodes (9): DictionarySearchMode, ASSONANCE, RHYME, WORD_PREFIX, WORD_SUFFIX, DictionarySearchPlan, Prefix, modeLabel() (+1 more)

### Community 181 - "PlaybackFrameLedger"
Cohesion: 0.11
Nodes (10): FramePositionEvents, ShortArray, PlaybackFrameLedger, PlaybackFrameLedgerTest, Architecture, Dictionaries, Ghostwriter documentation, Known limitations (+2 more)

### Community 182 - "Releasing"
Cohesion: 0.50
Nodes (4): Build and verify, Maintainer runtime checks, Package matching source, Releasing

### Community 184 - "Ghostwriter"
Cohesion: 0.50
Nodes (4): Build and test, Documentation and contributions, Ghostwriter, License

### Community 185 - "Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?, Source Nodes

### Community 186 - "Dictionary data license and attribution"
Cohesion: 0.33
Nodes (5): Conditions when redistributing, Dictionary data license and attribution, Modifications, Source attribution, Warranty

### Community 187 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.39
Nodes (8): ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage()

### Community 191 - "documentation/README.md"
Cohesion: 0.38
Nodes (3): Build setup, Checkout and build, Requirements

### Community 197 - "PcmLoopRendererTest.kt"
Cohesion: 0.40
Nodes (3): assumetrue, atomicreference, managementfactory

### Community 199 - "BeatPlaybackServiceTest.kt"
Cohesion: 0.11
Nodes (21): abs, PcmSources, atomicinteger, atomiclong, audioformat, AudioManager, ByteBuffer, byteorder (+13 more)

## Knowledge Gaps
- **479 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+474 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 984 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **39 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Work-memory lessons

**Preferred sources** — corroborated by past sessions; start here.
- `BeatPlaybackService` (3× useful, score=2.983110178)
- `DictionarySearch` (2× useful, score=1.964073488)

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Text to Phoneme Translation` connect `tr_languages.c` to `index.md`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **Why does `WaveformMarker` connect `WaveformMarker` to `BeatPlayer`, `.renderWaveform`, `MarkerLoopRole`, `EditorScreen.kt`, `ProjectStorageBeatTest`, `file`, `BeatPlaybackServiceTest.kt`, `GhostwriterTheme`, `BeatPlayerPanel.kt`, `WaveformView.kt`, `BeatPlayerInstrumentedTest`, `BeatPlaybackServiceTest`, `EditorScreenTest.kt`, `WaveformViewTest.kt`, `MarkerLoopFrames`, `EditorScreenTest`?**
  _High betweenness centrality (0.045) - this node is a cross-community bridge._
- **Are the 24 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 24 INFERRED edges - model-reasoned connections that need verification._
- **Are the 13 inferred relationships involving `WaveformMarker` (e.g. with `.frameMarkers_persistExactSamplesWithoutMillisecondRounding()` and `.frameNormalization_repairsStaleMillisecondsWithoutChangingExactFrames()`) actually correct?**
  _`WaveformMarker` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _479 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.09986504723346828 - nodes in this community are weakly interconnected._
- **Should `Generate` be split into smaller, more focused modules?**
  _Cohesion score 0.07142857142857142 - nodes in this community are weakly interconnected._