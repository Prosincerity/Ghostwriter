package com.prosincerity.ghostwriter.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.Normalizer
import kotlin.coroutines.coroutineContext

internal fun interface IpaGenerator {
    suspend fun ipa(word: String, language: String): String?
}

/** Offline IPA fallback backed by the pinned eSpeak NG 1.52 native core. */
internal class EspeakIpa(context: Context) : IpaGenerator {
    private val appContext = context.applicationContext

    override suspend fun ipa(word: String, language: String): String? = withContext(Dispatchers.IO) {
        val voice = when (language) {
            "en" -> 0
            "de" -> 1
            "tr" -> 2
            else -> return@withContext null
        }
        val normalized = Normalizer.normalize(word, Normalizer.Form.NFC)
        if (normalized.isBlank() || normalized.length > 128 || normalized.any { it.isWhitespace() || it == '\u0000' }) {
            return@withContext null
        }
        initialization.withLock {
            coroutineContext.ensureActive()
            if (!ready) {
                System.loadLibrary("ghostwriter_ipa")
                val parent = installData()
                check(nativeInitialize(parent.absolutePath.toByteArray(StandardCharsets.UTF_8))) {
                    "Could not initialize eSpeak NG data"
                }
                ready = true
            }
        }
        coroutineContext.ensureActive()
        nativePhonemize(normalized.toByteArray(StandardCharsets.UTF_8), voice)
            ?.toString(StandardCharsets.UTF_8)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }

    internal fun installData(): File {
        val root = File(appContext.filesDir, "espeak-ng")
        check(root.mkdirs() || root.isDirectory) { "Cannot create eSpeak NG data directory" }
        val version = File(root, VERSION)
        if (hasCompleteData(version)) return version
        val staging = File(root, ".$VERSION-${System.nanoTime()}.part")
        check(staging.mkdir()) { "Cannot create temporary eSpeak NG data directory" }
        try {
            for (relativePath in DATA_FILES) {
                val destination = File(staging, "espeak-ng-data/$relativePath")
                check(destination.parentFile!!.mkdirs() || destination.parentFile!!.isDirectory)
                appContext.assets.open("espeak-ng-1.52/espeak-ng-data/$relativePath").use { input ->
                    destination.outputStream().use { output -> input.copyTo(output) }
                }
            }
            check(hasCompleteData(staging)) { "Incomplete eSpeak NG data" }
            if (version.exists()) check(version.deleteRecursively()) { "Cannot replace incomplete eSpeak NG data" }
            check(staging.renameTo(version)) { "Cannot activate eSpeak NG data" }
        } finally {
            staging.deleteRecursively()
        }
        return version
    }

    private fun hasCompleteData(parent: File): Boolean = DATA_FILES.all {
        File(parent, "espeak-ng-data/$it").let { file -> file.isFile && file.length() > 0 }
    }

    private external fun nativeInitialize(path: ByteArray): Boolean
    private external fun nativePhonemize(word: ByteArray, voice: Int): ByteArray?

    private companion object {
        const val VERSION = "1.52.0"
        val initialization = Mutex()
        var ready = false
        val DATA_FILES = listOf(
            "intonations", "phondata", "phonindex", "phontab",
            "en_dict", "de_dict", "tr_dict",
            "lang/gmw/en", "lang/gmw/de", "lang/trk/tr",
        )
    }
}
