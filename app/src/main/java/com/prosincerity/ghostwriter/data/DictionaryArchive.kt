package com.prosincerity.ghostwriter.data

import kotlinx.coroutines.ensureActive
import java.io.File
import java.io.FilterInputStream
import java.util.zip.GZIPInputStream
import kotlin.coroutines.coroutineContext

/**
 * Copies an archive into staging on the caller's IO context and closes its streams.
 * Progress counts compressed download bytes; activation and staging cleanup belong to the installer.
 */
internal suspend fun unpackDictionaryArchive(
    source: DictionaryArchiveSource,
    asset: DictionaryAsset,
    staging: File,
    onProgress: suspend (DictionaryDownloadProgress) -> Unit,
) {
    var compressedBytes = 0L
    source.open(asset.url).use { input ->
        val counted = object : FilterInputStream(input) {
            override fun read(): Int {
                val value = super.read()
                if (value >= 0) compressedBytes++
                return value
            }

            override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
                val count = super.read(buffer, offset, length)
                if (count > 0) compressedBytes += count
                return count
            }
        }
        GZIPInputStream(counted).use { archive ->
            staging.outputStream().buffered().use { output ->
                val buffer = ByteArray(64 * 1024)
                var lastReported = 0L
                while (true) {
                    coroutineContext.ensureActive()
                    val count = archive.read(buffer)
                    if (count < 0) break
                    output.write(buffer, 0, count)
                    if (compressedBytes - lastReported >= 256 * 1024) {
                        onProgress(DictionaryDownloadProgress(
                            compressedBytes.coerceAtMost(asset.sizeBytes), asset.sizeBytes,
                        ))
                        lastReported = compressedBytes
                    }
                }
            }
        }
    }
}
