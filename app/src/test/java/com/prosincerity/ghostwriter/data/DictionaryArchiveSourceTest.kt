package com.prosincerity.ghostwriter.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/** Exercises the production HTTP lifecycle with an offline connection double. */
class DictionaryArchiveSourceTest {
    @Test
    fun successfulDownloadHasBoundedTimeoutsAndDisconnectsWhenClosed() {
        var bodyClosed = false
        val body = object : ByteArrayInputStream(byteArrayOf(1, 2, 3)) {
            override fun close() { bodyClosed = true; super.close() }
        }
        val connection = TestConnection(body)

        val archive = openDictionaryArchive(connection)
        assertTrue("Connection attempts must have a finite timeout", connection.connectTimeout > 0)
        assertTrue("Archive reads must have a finite timeout", connection.readTimeout > 0)
        assertTrue(connection.instanceFollowRedirects)
        assertEquals(0, connection.disconnects)
        assertFalse(bodyClosed)
        archive.use { assertEquals(listOf<Byte>(1, 2, 3), it.readBytes().toList()) }
        assertTrue(bodyClosed)
        assertEquals(1, connection.disconnects)
    }

    @Test
    fun unsuccessfulHttpResponseDisconnectsWithoutOpeningBody() {
        val connection = TestConnection().apply { status = 503 }

        val failure = assertThrows(IllegalStateException::class.java) { openDictionaryArchive(connection) }

        assertEquals("Dictionary download failed: HTTP 503", failure.message)
        assertEquals(0, connection.bodyOpens)
        assertEquals(1, connection.disconnects)
    }

    @Test
    fun failedResponseLookupDisconnectsAndPreservesCause() {
        val error = IOException("connection lost")
        val connection = TestConnection().apply { responseFailure = error }

        assertSame(error, assertThrows(IOException::class.java) { openDictionaryArchive(connection) })
        assertEquals(0, connection.bodyOpens)
        assertEquals(1, connection.disconnects)
    }

    @Test
    fun failedBodyOpenDisconnectsAndPreservesCause() {
        val error = IOException("could not open archive")
        val connection = TestConnection().apply { bodyFailure = error }

        assertSame(error, assertThrows(IOException::class.java) { openDictionaryArchive(connection) })
        assertEquals(1, connection.bodyOpens)
        assertEquals(1, connection.disconnects)
    }

    @Test
    fun failingStreamCloseStillDisconnects() {
        val error = IOException("close failed")
        val body = object : ByteArrayInputStream(byteArrayOf(1)) {
            override fun close() { throw error }
        }
        val connection = TestConnection(body)
        val archive = openDictionaryArchive(connection)

        assertSame(error, assertThrows(IOException::class.java) { archive.close() })
        assertEquals(1, connection.disconnects)
    }

    @Test
    fun interruptedReadClosesBodyAndDisconnects() {
        val error = IOException("read failed")
        var bodyClosed = false
        val body = object : InputStream() {
            override fun read(): Int = throw error
            override fun close() { bodyClosed = true }
        }
        val connection = TestConnection(body)

        assertSame(error, assertThrows(IOException::class.java) {
            openDictionaryArchive(connection).use { it.read() }
        })
        assertTrue(bodyClosed)
        assertEquals(1, connection.disconnects)
    }

    private class TestConnection(
        private val body: InputStream = ByteArrayInputStream(byteArrayOf()),
    ) : HttpURLConnection(URL("https://example.invalid/dictionary.db.gz")) {
        var status = HTTP_OK
        var responseFailure: IOException? = null
        var bodyFailure: IOException? = null
        var bodyOpens = 0
        var disconnects = 0

        init { instanceFollowRedirects = false }

        override fun getResponseCode(): Int {
            responseFailure?.let { throw it }
            return status
        }

        override fun getInputStream(): InputStream {
            bodyOpens++
            bodyFailure?.let { throw it }
            return body
        }

        override fun disconnect() { disconnects++ }
        override fun usingProxy(): Boolean = false
        override fun connect() = Unit
    }
}
