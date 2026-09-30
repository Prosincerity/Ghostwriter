# Graph Report - Gh0stwrit3r  (2026-09-30)

## Corpus Check
- 336 files · ~557,952 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2058 file(s) not represented in the graph (top: (none) 1892, .xml 82, .test 17)

## Summary
- 3706 nodes · 9428 edges · 193 communities (150 shown, 43 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 1362 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `3bc4c189`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- .configure
- synthesize.c
- ProjectStorage
- ProjectStorageTest
- WaveformViewport
- SettingsFormatTest.kt
- EditorScreen
- WaveformExtractor
- SystemFontFile
- VoiceSettingsTest
- gradlew
- ucd_lookup_category
- WaveformView.kt
- What You Must Do When Invoked
- android.content.Context
- event.c
- BeatPlaybackService.kt
- graphify reference: extra exports and benchmark
- SeekBarPreference
- graphify reference: query, path, explain
- graphify reference: add a URL and watch a folder
- graphify reference: commit hook and native CLAUDE.md integration
- graphify reference: incremental update and cluster-only
- graphify reference: GitHub clone and cross-repo merge
- graphify reference: transcribe video and audio
- extraction-spec.md
- Android
- EditorScreenTest
- compile_line
- compiledata.c
- .fromIpa
- PcmRingBuffer
- synthdata.c
- DictionaryInstaller.kt
- TextToSpeechTestCase
- WaveformMarker
- SpeechSynthesis
- speech.c
- tests/encoding.c
- espeak_command.c
- TestConnection
- HomeScreen.kt
- Text to Phoneme Translation
- wavegen.c
- demo.js
- eSpeakActivity
- .excludedWords
- ttsengine.cpp
- LyricsNotepadTest.kt
- ucd_properties
- mbrowrap.c
- EditorScreenTest.kt
- tests/readclause.c
- espeak_Initialize
- Diacritics
- VoiceVariantPreference.java
- Configuration Files
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- tr_languages.c
- Phoneme Model
- eSpeakService.c
- EditorScreen.kt
- Language Attributes
- CheckVoiceDataTest
- PlaybackFocusState
- FrameManagerImpl
- parwave
- LyricTextSettings
- Diacritics
- composable
- DictionaryPronunciations.kt
- speechPlayer.cpp
- TtsService
- MarkerLoopRole
- .isTtsLangCode
- DictionaryArchive.kt
- index.md
- SSML (Speech Synthesis Markup Language)
- ContextWrapper
- DictionarySearch
- numbers.md
- .parse
- speechWaveGenerator.cpp
- VoiceVariant
- main
- sPlayer.c
- status_to_espeak_error
- intonation.c
- PlaybackNotificationUpdater
- dictionary.c
- Phoneme Instructions
- CodePoint
- .createSeekBarPreference
- LoadLanguageOptions
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
- BeatPlaybackControls.kt
- ProjectMetadata
- uprintf
- Resonator
- Change Log
- PcmSource
- MainActivityTest
- DictionaryHeadword
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- test
- BeatPlayerPanel.kt
- Third-party software and dictionary data
- WaveformCache
- ucd_lookup_category_group
- printucddata_cpp.cpp
- Vowels
- DictionaryArchiveSource
- Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability.
- BeatPlayerPanel
- common
- Voice Attributes
- ProcessSsmlTag
- AssonanceData
- .excludedWords
- GhostwriterTheme
- .setMarkers
- eSpeak NG user guide
- ContextWrapper
- create_dict_corpus_file.py
- PcmLoopRenderer
- MainActivity.kt
- ESPEAK_API
- translate.c
- eSpeakNGWorker
- android/gradlew
- EspeakIpa
- Using eSpeak NG as a library
- espeak-ng
- Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?
- DictionaryScreen
- ProjectLyricsStorage
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- InputStream
- DictionaryDownloads.kt
- Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?
- Translation fuzzers
- SmoothLoopPlayback
- Contribution Guide
- BeatPlaybackServiceTest
- waveformMarkerHitBounds
- DictionarySearchMode
- Callback
- row
- PlaybackFrameLedger
- Voice and Language Files
- httpurlconnection
- .load
- Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?
- Dictionary data license and attribution
- espeak_ng_PrintStatusCodeMessage
- Pronunciation Dictionary List
- rgroup_sorter
- documentation/README.md
- espeak_SetUriCallback

## God Nodes (most connected - your core abstractions)
1. `BeatPlayer` - 60 edges
2. `GhostwriterTheme()` - 53 edges
3. `SpeechSynthesis` - 49 edges
4. `WaveformMarker` - 43 edges
5. `ucd_properties()` - 38 edges
6. `ProjectStorage` - 33 edges
7. `BeatPlaybackService` - 33 edges
8. `LyricTextSettings` - 32 edges
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

## Communities (193 total, 43 thin omitted)

### Community 0 - "BeatPlayer"
Cohesion: 0.14
Nodes (4): BeatPlayer, BeatPlayerTest, MediaPlayer, PowerManager

### Community 1 - ".configure"
Cohesion: 0.16
Nodes (13): Building, Cross Compilation, eSpeak NG Feature Configuration, Extended Dictionary Configuration, LLVM Fuzzer Support, Sanitizer Flag Configuration, Bugs, Build Dependencies (+5 more)

### Community 2 - "synthesize.c"
Cohesion: 0.10
Nodes (48): SPEED_FACTORS, voice_t, SetSpeed(), SetSpeedFactors(), SetSpeedMods(), SetSpeedMultiplier(), FILE, PHONEME_LIST (+40 more)

### Community 6 - "SettingsFormatTest.kt"
Cohesion: 0.25
Nodes (4): formatPlaybackTime(), formatInterval(), formatTypographyNumber(), SettingsFormatTest

### Community 7 - "EditorScreen"
Cohesion: 0.12
Nodes (6): EspeakIpaInstrumentedTest, IntArray, StagedFileWriter, EditorScreen(), PendingBeatPreparation, ProjectStorageBeatTest

### Community 8 - "WaveformExtractor"
Cohesion: 0.09
Nodes (10): WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest, audioformat, cancellationexception, MediaCodec (+2 more)

### Community 9 - "SystemFontFile"
Cohesion: 0.14
Nodes (13): SystemFontCatalogTest, LyricFont, lyricFontFromPreference(), SystemFontFile, systemFontOptions(), SystemFontCatalog, LyricFontTest, assertnotequals (+5 more)

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "ucd_lookup_category"
Cohesion: 0.33
Nodes (15): ucd_lookup_category(), codepoint_t, ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl(), ucd_isdigit(), ucd_isgraph() (+7 more)

### Community 14 - "WaveformView.kt"
Cohesion: 0.07
Nodes (27): ceil, cliptobounds, detectdraggestures, detecttapgestures, detecttransformgestures, drawtext, fillmaxheight, floor (+19 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "android.content.Context"
Cohesion: 0.06
Nodes (35): accessibilityevent, android.app.Application, android.content.Context, android.os.AsyncTask, android.os.Bundle, android.preference.Preference, android.preference.Preference.OnPreferenceChangeListener, android.preference.PreferenceActivity (+27 more)

### Community 19 - "event.c"
Cohesion: 0.08
Nodes (40): assert, espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, getopt, pthread, gcd(), getopt() (+32 more)

### Community 20 - "BeatPlaybackService.kt"
Cohesion: 0.09
Nodes (21): Context, IBinder, Intent, LocalBinder, audioattributes, AudioFocusRequest, AudioManager, audiotrack (+13 more)

### Community 21 - "graphify reference: extra exports and benchmark"
Cohesion: 0.22
Nodes (8): graphify reference: extra exports and benchmark, Step 6b - Wiki (only if --wiki flag), Step 7 - Neo4j export (only if --neo4j or --neo4j-push flag), Step 7a - FalkorDB export (only if --falkordb or --falkordb-push flag), Step 7b - SVG export (only if --svg flag), Step 7c - GraphML export (only if --graphml flag), Step 7d - MCP server (only if --mcp flag), Step 8 - Token reduction benchmark (only if total_words > 5000)

### Community 22 - "SeekBarPreference"
Cohesion: 0.09
Nodes (17): android.content.DialogInterface, android.content.SharedPreferences, android.preference.DialogPreference, android.util.AttributeSet, android.widget.EditText, android.widget.RadioButton, android.widget.SeekBar, editable (+9 more)

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
Cohesion: 0.35
Nodes (3): EditorScreenTest, IntArray, MutableState

### Community 32 - "compile_line"
Cohesion: 0.11
Nodes (26): isspace2(), LookupEnvelopeName(), CompileContext, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context() (+18 more)

### Community 33 - "compiledata.c"
Cohesion: 0.06
Nodes (88): phoneme_feature_t, PHONEME_TAB_LIST, SpectFrame, SpectSeq, FILE, Read4Bytes(), StringToWord(), CompileContext (+80 more)

### Community 34 - ".fromIpa"
Cohesion: 0.15
Nodes (3): IpaSearchKeys, Keys, IpaSearchKeysTest

### Community 35 - "PcmRingBuffer"
Cohesion: 0.17
Nodes (8): PcmBeat, ShortArray, Loop, Page, PcmRingBuffer, Region, ShortArray, PcmRingBufferTest

### Community 36 - "synthdata.c"
Cohesion: 0.10
Nodes (35): PHONEME_DATA, PHONEME_LIST, PHONEME_LIST2, PHONEME_TAB, Translator, WORD_PH_DATA, MakePhonemeList(), ReInterpretPhoneme() (+27 more)

### Community 37 - "DictionaryInstaller.kt"
Cohesion: 0.15
Nodes (13): DictionaryAsset, DictionaryInstaller, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK, WIKTIONARY, Context (+5 more)

### Community 38 - "TextToSpeechTestCase"
Cohesion: 0.16
Nodes (9): android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, TextToSpeechTest, Override, SuppressWarnings, TextToSpeechTestCase, Exception, Voice (+1 more)

### Community 39 - "WaveformMarker"
Cohesion: 0.26
Nodes (5): WaveformMarker, MarkerLoopRange, replaceLoopMarker(), EditorMarkerDialogs(), MarkerLoopTest

### Community 40 - "SpeechSynthesis"
Cohesion: 0.07
Nodes (5): SpeechSynthesisTest, GetSampleText, Override, SpeechSynthesis, SynthReadyCallback

### Community 41 - "speech.c"
Cohesion: 0.09
Nodes (28): audio, common, dirent, emscripten, encoding, errno, espeak_ng, fcntl (+20 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.19
Nodes (51): espeak_ng_ENCODING, espeak_ng_TEXT_DECODER, espeak_ng_STATUS, wchar_t, create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc() (+43 more)

### Community 43 - "espeak_command.c"
Cohesion: 0.08
Nodes (50): espeak_ng_OUTPUT_MODE, espeak_PARAMETER, espeak_POSITION_TYPE, espeak_VOICE, t_espeak_command, wchar_t, create_espeak_char(), create_espeak_key() (+42 more)

### Community 44 - "TestConnection"
Cohesion: 0.12
Nodes (11): FilterInputStream, openDictionaryArchive(), FilterInputStream, DictionaryArchiveSourceTest, ByteArrayInputStream, InputStream, ByteArrayInputStream, ByteArrayInputStream (+3 more)

### Community 45 - "HomeScreen.kt"
Cohesion: 0.12
Nodes (14): add, HomeScreenTest, HomeScreen(), NewProjectDialog(), RenameProjectDialog(), clickable, delete, imeaction (+6 more)

### Community 46 - "Text to Phoneme Translation"
Cohesion: 0.12
Nodes (16): Character Substitution, Conditional Rules, Letter groups, Letter names, Numbers, Numbers and Character Names, Phoneme names, Pronunciation Rules (+8 more)

### Community 47 - "wavegen.c"
Cohesion: 0.08
Nodes (43): espeak_ng_OUTPUT_HOOKS, sonic, ESPEAK_NG_API, espeak_ng_SetRandSeed(), espeak_rand(), espeak_srand(), GetFrameRms(), MarkerEvent() (+35 more)

### Community 48 - "demo.js"
Cohesion: 0.06
Nodes (28): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+20 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.09
Nodes (21): android.content.BroadcastReceiver, android.content.Intent, android.os.Handler, android.os.Message, android.view.Menu, android.view.MenuItem, intentfilter, java.lang.ref.WeakReference (+13 more)

### Community 50 - ".excludedWords"
Cohesion: 0.15
Nodes (7): AssonanceFormFilter, Entry, IntArray, PrefixNode, Pronounced, AssonanceFormFilterTest, Pronounced

### Community 51 - "ttsengine.cpp"
Cohesion: 0.10
Nodes (37): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+29 more)

### Community 52 - "LyricsNotepadTest.kt"
Cohesion: 0.10
Nodes (26): LyricFontFamily, CURSIVE, MONOSPACE, SANS_SERIF, SERIF, SYSTEM_DEFAULT, LyricTextAlignment, CENTER (+18 more)

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (27): poll, procfs, signal, stdarg, BOOL, close_mbrola(), close_pipes(), create_pipes() (+19 more)

### Community 55 - "EditorScreenTest.kt"
Cohesion: 0.06
Nodes (63): abs, activitynotfoundexception, after, androidjunit4, ExampleInstrumentedTest, PcmSources, assertisenabled, assertisnotenabled (+55 more)

### Community 56 - "tests/readclause.c"
Cohesion: 0.11
Nodes (43): phoneme, readclause, speech, synthesize, AnnouncePunctuation(), Translator, CheckPhonemeMode(), clause_type_from_codepoint() (+35 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.14
Nodes (32): espeak_AUDIO_OUTPUT, espeak_POSITION_TYPE, espeak_VOICE, espeak_Initialize(), espeak_SetVoiceByName(), espeak_SetVoiceByProperties(), espeak_Synth(), t_espeak_callback (+24 more)

### Community 58 - "Diacritics"
Cohesion: 0.07
Nodes (30): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Consonants, Diacritics, Fortis and Lenis (+22 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.10
Nodes (24): adapterview, android.app.Activity, android.util.Pair, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter, android.widget.Spinner (+16 more)

### Community 60 - "Configuration Files"
Cohesion: 0.12
Nodes (16): Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation, Dictionary Files (+8 more)

### Community 61 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.33
Nodes (10): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), jbyteArray (+2 more)

### Community 62 - "tr_languages.c"
Cohesion: 0.40
Nodes (15): (Re)definition of character groups, SetLengthMods(), Translator, NewTranslator(), ResetLetterBits(), SelectTranslator(), SetArabicLetters(), SetCyrillicLetters() (+7 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (26): JavaVM, jboolean, jclass, jstring, espeak_EVENT, jint, JNICALL, JNIEnv (+18 more)

### Community 65 - "EditorScreen.kt"
Cohesion: 0.08
Nodes (32): activityresultcontracts, ComponentName, IBinder, ServiceConnection, rememberBeatPlayback(), ServiceConnection, displayNameFor(), Context (+24 more)

### Community 66 - "Language Attributes"
Cohesion: 0.12
Nodes (16): brackets, bracketsAnnounced, dictionary, dictmin, dictrules, intonation, Language Attributes, lowercaseSentence (+8 more)

### Community 67 - "CheckVoiceDataTest"
Cohesion: 0.33
Nodes (3): android.test.ActivityUnitTestCase, java.lang.reflect.Field, CheckVoiceDataTest

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "parwave"
Cohesion: 0.16
Nodes (21): klatt_frame_ptr, resonator_ptr, antiresonator(), frame_t, voice_t, WGEN_DATA, DBtoLIN(), flutter() (+13 more)

### Community 71 - "LyricTextSettings"
Cohesion: 0.17
Nodes (5): LyricTextSettings, Context, Settings, LyricTextSettingsTest, LyricTextStyleTest

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 73 - "composable"
Cohesion: 0.14
Nodes (12): Modifier, composable, darkcolorscheme, fillmaxwidth, issystemindarktheme, keyboardcapitalization, keyboardoptions, lightcolorscheme (+4 more)

### Community 74 - "DictionaryPronunciations.kt"
Cohesion: 0.11
Nodes (10): SQLiteDatabase, PronunciationResult, PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, DictionarySearchData, DictionarySearchRows (+2 more)

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.14
Nodes (14): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+6 more)

### Community 76 - "TtsService"
Cohesion: 0.14
Nodes (8): android.speech.tts.SynthesisCallback, android.speech.tts.SynthesisRequest, android.speech.tts.TextToSpeechService, Override, SuppressWarnings, TtsService, Override, Voice

### Community 77 - "MarkerLoopRole"
Cohesion: 0.21
Nodes (7): MutableState, WaveformViewTest, MarkerLoopRole, END, NONE, START, jsonarray

### Community 78 - ".isTtsLangCode"
Cohesion: 0.27
Nodes (3): Override, TextToSpeechServiceTest, TtsServiceTest

### Community 79 - "DictionaryArchive.kt"
Cohesion: 0.33
Nodes (4): coroutinecontext, cursor, ensureactive, gzipinputstream

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - "ContextWrapper"
Cohesion: 0.33
Nodes (4): ContextWrapper, Context, ContextWrapper, AssetManager

### Community 83 - "DictionarySearch"
Cohesion: 0.44
Nodes (4): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations, DictionarySearch

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.18
Nodes (5): java.util.regex.Pattern, VoiceVariantTest, VariantData, Override, VoiceVariant

### Community 88 - "main"
Cohesion: 0.08
Nodes (53): espeak_EVENT, FILE, CloseWavFile(), DisplayVoices(), main(), OpenWavFile(), SynthCallback(), Write4Bytes() (+45 more)

### Community 89 - "sPlayer.c"
Cohesion: 0.18
Nodes (18): KlattFini(), KlattInit(), frame_t, sample, speechPlayer_frame_t, voice_t, WGEN_DATA, fillSpeechPlayerFrame() (+10 more)

### Community 90 - "status_to_espeak_error"
Cohesion: 0.17
Nodes (16): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, ESPEAK_API, espeak_ng_STATUS, espeak_PARAMETER, wchar_t (+8 more)

### Community 91 - "intonation.c"
Cohesion: 0.27
Nodes (16): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+8 more)

### Community 92 - "PlaybackNotificationUpdater"
Cohesion: 0.33
Nodes (3): PlaybackNotificationUpdater, PlaybackNotificationUpdaterTest, TestQueue

### Community 93 - "dictionary.c"
Cohesion: 0.08
Nodes (78): limits, MatchRecord, IsAlpha(), IsBracket(), IsDigit(), IsDigit09(), IsSpace(), utf8_in() (+70 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.06
Nodes (33): Attributes, CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, Conditional Statements (+25 more)

### Community 96 - ".createSeekBarPreference"
Cohesion: 0.15
Nodes (5): Parameter, UnitType, Percentage, Punctuation, WordsPerMinute

### Community 97 - "LoadLanguageOptions"
Cohesion: 0.22
Nodes (10): LANGUAGE_OPTIONS, MNEM_TAB, Translator, CheckTranslator(), LoadLanguageOptions(), LookupTune(), ProcessLanguageOptions(), MNEM_TAB (+2 more)

### Community 98 - "TtsService.java"
Cohesion: 0.11
Nodes (27): android.annotation.SuppressLint, android.test.AndroidTestCase, anyof, arraylist, assertthat, bundle, checksum, configuration (+19 more)

### Community 99 - "withDictionaryTestContext"
Cohesion: 0.20
Nodes (9): DictionaryInstallerInstrumentedTest, ByteArrayInputStream, ByteArrayInputStream, dictionaryArchive(), ByteArray, T, withDictionaryTestContext(), DictionaryDownloadsTest (+1 more)

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
Cohesion: 0.10
Nodes (39): alertdialog, LongBeatWarningDialog(), ReassignBeatDialog(), arrangement, arrowback, backhandler, column, contentdescription (+31 more)

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 114 - "BeatPlaybackControls.kt"
Cohesion: 0.14
Nodes (13): box, icon, loop, mutablefloatstateof, paddingvalues, pause, playarrow, size (+5 more)

### Community 115 - "ProjectMetadata"
Cohesion: 0.13
Nodes (6): ProjectInfoDialogTest, JSONObject, ProjectMetadata, ProjectInfoDialog(), trimmedOrNull(), ProjectMetadataTest

### Community 116 - "uprintf"
Cohesion: 0.17
Nodes (19): stddef, 6.2.0 - 2013-10-16, codepoint_t, ucd_tolower(), ucd_totitle(), ucd_toupper(), totitle(), toupper() (+11 more)

### Community 117 - "Resonator"
Cohesion: 0.10
Nodes (19): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate (+11 more)

### Community 118 - "Change Log"
Cohesion: 0.18
Nodes (10): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06, 9.0.0 - 2016-12-28 (+2 more)

### Community 119 - "PcmSource"
Cohesion: 0.25
Nodes (3): ShortArray, MemoryPcmSource, PcmSource

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
Cohesion: 0.09
Nodes (31): shouldWarnBeforeWaveformExtraction(), parsePositiveBpm(), ProjectInfoBpmTest, WaveformWarningTest, assertarrayequals, assertequals, assertfalse, assertnotnull (+23 more)

### Community 127 - "BeatPlayerPanel.kt"
Cohesion: 0.15
Nodes (13): WaveformZoomButton(), button, card, columnscope, delay, fillmaxsize, ImageVector, launchedeffect (+5 more)

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.33
Nodes (5): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Roboto UI fonts, Third-party software and dictionary data

### Community 130 - "ucd_lookup_category_group"
Cohesion: 0.18
Nodes (12): category_group, codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category_group(), get_category_group_string(), lookup_category_group() (+4 more)

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "DictionaryArchiveSource"
Cohesion: 0.24
Nodes (10): ByteArray, FilterInputStream, unpackDictionaryArchive(), FilterInputStream, DictionaryArchiveSource, DictionaryDownloadProgress, DictionaryArchiveTest, ByteArray (+2 more)

### Community 134 - "Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability."
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability., Source Nodes

### Community 135 - "BeatPlayerPanel"
Cohesion: 0.15
Nodes (9): BeatComponentsTest, BeatPlaybackControls(), BeatPlayerPanel(), CenteredPlayerContent(), IntArray, Modifier, IntArray, Modifier (+1 more)

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - "Voice Attributes"
Cohesion: 0.14
Nodes (14): breath, breathw, consonants, echo, flutter, formant, freq\_add, pitch (+6 more)

### Community 138 - "ProcessSsmlTag"
Cohesion: 0.23
Nodes (19): PARAM_STACK, SSML_STACK, AddNameData(), attr_prosody_value(), attrcmp(), attrcopy_utf8(), attrlookup(), attrnumber() (+11 more)

### Community 140 - ".excludedWords"
Cohesion: 0.28
Nodes (4): CompoundRhymeFilter, Entry, Pronounced, CompoundRhymeFilterTest

### Community 141 - "GhostwriterTheme"
Cohesion: 0.14
Nodes (10): LyricsNotepadTest, SettingsScreenTest, WaveformMarkerDialog(), LyricsNotepad(), ClosedFloatingPointRange, T, SettingsDropdownRow(), SettingsScreen() (+2 more)

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
Cohesion: 0.19
Nodes (8): FramePositionEvents, ShortArray, PcmLoopBounds, PcmLoopRenderer, PcmLoopRendererTest, assumetrue, atomicreference, managementfactory

### Community 147 - "MainActivity.kt"
Cohesion: 0.10
Nodes (23): AboutScreenTest, About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, Bundle (+15 more)

### Community 148 - "ESPEAK_API"
Cohesion: 0.29
Nodes (7): PrintVersion(), ESPEAK_API, espeak_PARAMETER, FILE, espeak_GetParameter(), espeak_Info(), espeak_SetPhonemeTrace()

### Community 149 - "translate.c"
Cohesion: 0.14
Nodes (26): Translator, is_str_totally_null(), towlower2(), LookupPhonemeTable(), SelectPhonemeTableName(), PHONEME_LIST2, Translator, WORD_TAB (+18 more)

### Community 150 - "eSpeakNGWorker"
Cohesion: 0.22
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

### Community 156 - "DictionaryScreen"
Cohesion: 0.30
Nodes (5): DictionaryScreenTest, DictionaryMatch, DictionarySearchResult, dictionaryLanguageLabel(), DictionaryScreen()

### Community 170 - "InputStream"
Cohesion: 0.50
Nodes (3): InputStream, ByteArray, InputStream

### Community 171 - "DictionaryDownloads.kt"
Cohesion: 0.24
Nodes (10): alignment, DictionaryDownloads(), DictionaryDownloadsScreen(), downloadSourceLabel(), Modifier, check, download, iconbutton (+2 more)

### Community 172 - "Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?, Source Nodes

### Community 173 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 175 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 176 - "BeatPlaybackServiceTest"
Cohesion: 0.24
Nodes (7): BeatPlaybackServiceTest, ServiceConnection, ComponentName, IBinder, Notification, ServiceConnection, T

### Community 177 - "waveformMarkerHitBounds"
Cohesion: 0.39
Nodes (3): ClosedFloatingPointRange, waveformMarkerHitBounds(), WaveformMarkerHitBoundsTest

### Community 178 - "DictionarySearchMode"
Cohesion: 0.15
Nodes (9): DictionarySearchMode, ASSONANCE, RHYME, WORD_PREFIX, WORD_SUFFIX, DictionarySearchPlan, Prefix, modeLabel() (+1 more)

### Community 181 - "PlaybackFrameLedger"
Cohesion: 0.15
Nodes (8): PlaybackFrameLedger, PlaybackFrameLedgerTest, Architecture, Dictionaries, Ghostwriter documentation, Known limitations, Playback and waveforms, Projects and settings

### Community 182 - "Voice and Language Files"
Cohesion: 0.25
Nodes (8): gender, Identification Attributes, language, maintainer, Maintenance Attributes, name, status, Voice and Language Files

### Community 185 - "Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?, Source Nodes

### Community 186 - "Dictionary data license and attribution"
Cohesion: 0.33
Nodes (5): Conditions when redistributing, Dictionary data license and attribution, Modifications, Source attribution, Warranty

### Community 187 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.27
Nodes (11): ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage() (+3 more)

### Community 189 - "Pronunciation Dictionary List"
Cohesion: 0.40
Nodes (5): Flags, Multiple Words, Pronunciation Dictionary List, Special characters in \<phoneme string\>:, Translating a Word to Another Word

### Community 191 - "documentation/README.md"
Cohesion: 0.05
Nodes (41): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints, Dictionaries, Pronunciation lookup, Release inputs and installation (+33 more)

## Knowledge Gaps
- **479 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+474 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 971 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **43 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Work-memory lessons

**Preferred sources** — corroborated by past sessions; start here.
- `BeatPlaybackService` (3× useful, score=2.983110178)
- `DictionarySearch` (2× useful, score=1.964073488)

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `eSpeakActivity` connect `eSpeakActivity` to `VoiceVariantPreference.java`, `Android`, `TextToSpeechTestCase`, `SeekBarPreference`?**
  _High betweenness centrality (0.050) - this node is a cross-community bridge._
- **Why does `Building eSpeak NG` connect `Android` to `index.md`?**
  _High betweenness centrality (0.048) - this node is a cross-community bridge._
- **Why does `BeatPlayer` connect `BeatPlayer` to `EditorScreen.kt`, `PcmRingBuffer`, `BeatPlayerPanel`, `WaveformMarker`, `BeatPlaybackService`, `.setMarkers`, `BeatPlayerInstrumentedTest`, `SmoothLoopPlayback`, `BeatPlaybackControls.kt`, `PlaybackFrameLedger`, `EditorScreenTest.kt`, `.load`, `BeatPlayerPanel.kt`?**
  _High betweenness centrality (0.044) - this node is a cross-community bridge._
- **Are the 24 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 24 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _479 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.13675213675213677 - nodes in this community are weakly interconnected._
- **Should `synthesize.c` be split into smaller, more focused modules?**
  _Cohesion score 0.09608843537414966 - nodes in this community are weakly interconnected._