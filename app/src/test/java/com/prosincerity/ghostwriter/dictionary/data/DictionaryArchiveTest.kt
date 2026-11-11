package com.prosincerity.ghostwriter.dictionary.data

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Random
import java.security.MessageDigest
import java.util.zip.GZIPOutputStream

class DictionaryArchiveTest {
    @get:Rule
    val temporary = TemporaryFolder()

    @Test
    fun copiesPayloadAndReportsCompressedBytes() {
        val payload = randomPayload()
        val compressed = gzip(payload)
        val input = TrackedInput(compressed)
        val asset = asset(compressed.size.toLong(), sha256(compressed))
        val progress = mutableListOf<DictionaryDownloadProgress>()
        val staging = temporary.newFile()
        var openedUrl: String? = null

        runBlocking {
            unpackDictionaryArchive(DictionaryArchiveSource { url ->
                openedUrl = url
                input
            }, asset, staging) { progress += it }
        }

        assertEquals(asset.url, openedUrl)
        assertArrayEquals(payload, staging.readBytes())
        assertTrue(input.closed)
        assertTrue(progress.isNotEmpty())
        assertTrue(progress.all { it.totalBytes == asset.sizeBytes && it.downloadedBytes in 1..asset.sizeBytes })
        assertTrue(progress.zipWithNext().all { (before, after) -> after.downloadedBytes > before.downloadedBytes })
    }

    @Test
    fun incorrectArchiveSizeIsRejectedEvenWhenGzipIsValid() {
        val bytes = gzip(randomPayload())
        val input = TrackedInput(bytes)
        val progress = mutableListOf<DictionaryDownloadProgress>()

        assertThrows(IllegalStateException::class.java) {
            runBlocking {
                unpackDictionaryArchive(DictionaryArchiveSource { input }, asset(1, sha256(bytes)), temporary.newFile()) {
                    progress += it
                }
            }
        }

        assertTrue(progress.isNotEmpty())
        assertTrue(progress.all { it == DictionaryDownloadProgress(1, 1) })
    }

    @Test
    fun invalidGzipHeaderClosesInputWithoutCreatingStaging() {
        val input = TrackedInput("not a gzip archive".toByteArray())
        val staging = temporary.root.resolve("dictionary.part")

        assertThrows(IOException::class.java) {
            runBlocking { unpackDictionaryArchive(DictionaryArchiveSource { input }, asset(100), staging) {} }
        }

        assertTrue(input.closed)
        assertFalse(staging.exists())
    }

    @Test
    fun outputOpenFailureStillClosesInput() {
        val input = TrackedInput(gzip(byteArrayOf(1, 2, 3)))
        val staging = temporary.newFolder()

        assertThrows(FileNotFoundException::class.java) {
            runBlocking { unpackDictionaryArchive(DictionaryArchiveSource { input }, asset(100), staging) {} }
        }

        assertTrue(input.closed)
    }

    @Test
    fun progressFailurePreservesCauseAndClosesStreams() {
        val input = TrackedInput(gzip(randomPayload()))
        val failure = IOException("progress delivery failed")
        val staging = temporary.newFile()

        assertSame(failure, assertThrows(IOException::class.java) {
            runBlocking {
                unpackDictionaryArchive(DictionaryArchiveSource { input }, asset(1), staging) { throw failure }
            }
        })

        assertTrue(input.closed)
        assertTrue(staging.length() > 0)
    }

    @Test
    fun cancellationDuringSuspendedProgressClosesInputAndStopsCopying() = runBlocking {
        withTimeout(5_000) {
            val payload = randomPayload()
            val input = TrackedInput(gzip(payload))
            val staging = temporary.newFile()
            val progressStarted = CompletableDeferred<Unit>()
            val finishDelivery = CompletableDeferred<Unit>()
            val copy = async {
                unpackDictionaryArchive(DictionaryArchiveSource { input }, asset(1), staging) {
                    progressStarted.complete(Unit)
                    finishDelivery.await()
                }
            }

            progressStarted.await()
            assertFalse(copy.isCompleted)
            copy.cancel()
            copy.join()

            assertTrue(copy.isCancelled)
            assertTrue(input.closed)
            assertTrue(staging.length() < payload.size)
        }
    }

    private fun randomPayload(): ByteArray = ByteArray(800_000).also { Random(42).nextBytes(it) }

    private fun gzip(payload: ByteArray): ByteArray = ByteArrayOutputStream().use { output ->
        GZIPOutputStream(output).use { it.write(payload) }
        output.toByteArray()
    }

    @Test
    fun mismatchingHashIsRejectedEvenWhenSizeAndGzipAreValid() {
        val bytes = gzip(byteArrayOf(1, 2, 3))
        val input = TrackedInput(bytes)

        val failure = assertThrows(IllegalStateException::class.java) {
            runBlocking {
                unpackDictionaryArchive(DictionaryArchiveSource { input }, asset(bytes.size.toLong()), temporary.newFile()) {}
            }
        }

        assertTrue(failure.message!!.contains("SHA-256"))
        assertTrue(input.closed)
    }

    @Test
    fun verifiesTheWholeCompressedFileIncludingTrailingBytes() = runBlocking {
        val bytes = gzip(byteArrayOf(1, 2, 3)) + byteArrayOf(4, 5, 6)
        val staging = temporary.newFile()

        unpackDictionaryArchive(DictionaryArchiveSource { ByteArrayInputStream(bytes) },
            asset(bytes.size.toLong(), sha256(bytes)), staging) {}

        assertArrayEquals(byteArrayOf(1, 2, 3), staging.readBytes())
    }

    private fun sha256(bytes: ByteArray) = MessageDigest.getInstance("SHA-256")
        .digest(bytes).joinToString("") { "%02x".format(it) }

    private fun asset(sizeBytes: Long, sha256: String = "0".repeat(64)) =
        DictionaryAsset("dictionary.db.gz", sizeBytes, "https://example.invalid/archive", sha256)

    private class TrackedInput(bytes: ByteArray) : ByteArrayInputStream(bytes) {
        var closed = false
            private set

        override fun close() {
            closed = true
            super.close()
        }
    }
}
