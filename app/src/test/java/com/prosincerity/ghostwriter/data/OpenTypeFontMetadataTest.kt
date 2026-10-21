package com.prosincerity.ghostwriter.data

import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenTypeFontMetadataTest {
    @Test
    fun invalidDirectoryOffsetsAndCollectionIndices_areRejected() {
        val valid = font(0x6e616d65 to names(Triple(1, "Valid", 0x0409)))
        assertNull(OpenTypeFontMetadata(valid, 1).familyName())
        for (field in listOf(20, 24)) {
            val malformed = ByteBuffer.wrap(valid.array().copyOf()).putInt(field, -1)
            assertNull(OpenTypeFontMetadata(malformed, 0).familyName())
        }
        val collection = ByteBuffer.allocate(16).putInt(0x74746366).putInt(0x10000)
            .putInt(1).putInt(16)
        assertNull(OpenTypeFontMetadata(collection, -1).familyName())
    }

    @Test
    fun names_rejectInvalidHeadersAndUnusableStrings() {
        for (format in listOf(-1, 2)) {
            val table = names(Triple(1, "Valid", 0x0409))
            ByteBuffer.wrap(table).putShort(0, format.toShort())
            assertNull(OpenTypeFontMetadata(font(0x6e616d65 to table), 0).familyName())
        }
        val truncated = names(Triple(1, "Valid", 0x0409))
        ByteBuffer.wrap(truncated).putShort(2, 100)
        assertNull(OpenTypeFontMetadata(font(0x6e616d65 to truncated), 0).familyName())
        for (text in listOf("", "   ", "Bad\u0000Name", "Bad\uFFFDName")) {
            assertNull(OpenTypeFontMetadata(font(0x6e616d65 to names(Triple(1, text, 0x0409))), 0)
                .familyName())
        }
        val outOfBounds = names(Triple(1, "Valid", 0x0409))
        ByteBuffer.wrap(outOfBounds).putShort(16, 1000)
        assertNull(OpenTypeFontMetadata(font(0x6e616d65 to outOfBounds), 0).familyName())
    }

    @Test
    fun names_supportUnicodeAndMacRomanAndSkipUnsupportedEncodings() {
        for ((platform, encoding) in listOf(0 to 0, 3 to 0, 3 to 10, 1 to 0)) {
            val table = names(Triple(1, "Family", 0))
            val buffer = ByteBuffer.wrap(table).putShort(6, platform.toShort()).putShort(8, encoding.toShort())
            if (platform == 1) {
                val text = "Family".toByteArray(java.nio.charset.Charset.forName("x-MacRoman"))
                buffer.putShort(14, text.size.toShort())
                buffer.position(18)
                buffer.put(text)
            }
            assertEquals("Family", OpenTypeFontMetadata(font(0x6e616d65 to table), 0).familyName())
        }
        for ((platform, encoding) in listOf(2 to 0, 1 to 1, 3 to 2)) {
            val table = names(Triple(1, "Unsupported", 0))
            ByteBuffer.wrap(table).putShort(6, platform.toShort()).putShort(8, encoding.toShort())
            assertNull(OpenTypeFontMetadata(font(0x6e616d65 to table), 0).familyName())
        }
    }

    @Test
    fun coverage_rejectsMalformedSubtablesAndZeroGlyphs() {
        fun covers(table: ByteArray, cp: Int = 65) =
            OpenTypeFontMetadata(font(0x636d6170 to table), 0).covers(listOf(cp))
        assertFalse(covers(cmap12(65, 0)))
        assertFalse(covers(cmap4(65, 1, false), 0x12000))
        for (offset in listOf(-1, 1000)) {
            val table = cmap12(65, 1)
            ByteBuffer.wrap(table).putInt(8, offset)
            assertFalse(covers(table))
        }
        for (length in listOf(0, 1000)) {
            val table = cmap12(65, 1)
            ByteBuffer.wrap(table).putInt(16, length)
            assertFalse(covers(table))
        }
        for (count in listOf(-1, 100)) {
            val table = cmap12(65, 1)
            ByteBuffer.wrap(table).putInt(24, count)
            assertFalse(covers(table))
        }
        val truncated = cmap12(65, 1)
        ByteBuffer.wrap(truncated).putShort(2, 100)
        assertFalse(covers(truncated))
        val unsupported = cmap12(65, 1)
        ByteBuffer.wrap(unsupported).putShort(12, 6)
        assertFalse(covers(unsupported))
        val unicode = cmap12(65, 1)
        ByteBuffer.wrap(unicode).putShort(4, 0)
        assertTrue(covers(unicode))
        val invalidSegments = cmap4(65, 1, false)
        ByteBuffer.wrap(invalidSegments).putShort(18, 100)
        assertFalse(covers(invalidSegments))
        val wrappedGlyph = cmap4(65, 1, true)
        ByteBuffer.wrap(wrappedGlyph).putShort(36, -1)
        assertFalse(covers(wrappedGlyph))
    }

    @Test
    fun names_preferTypographicFamilyOverWeightFullAndPostScriptNames() {
        val font = font(0x6e616d65 to names(
            Triple(1, "Roboto Black", 0x0409), Triple(4, "Roboto Black Regular", 0x0409),
            Triple(6, "Roboto-Black", 0x0409), Triple(16, "Roboto", 0x0409),
            Triple(16, "Localized Roboto", 0x0411),
        ))
        font.position(5)
        font.order(ByteOrder.LITTLE_ENDIAN)
        assertEquals("Roboto", OpenTypeFontMetadata(font, 0).familyName())
        assertEquals(5, font.position())
        assertEquals(ByteOrder.LITTLE_ENDIAN, font.order())
    }

    @Test
    fun names_fallBackToLegacyFamilyAndDoNotInventNamesForMissingMetadata() {
        val font = font(0x6e616d65 to names(Triple(1, "Droid Sans Mono", 0x0409)))
        assertEquals("Droid Sans Mono", OpenTypeFontMetadata(font, 0).familyName())
        assertNull(OpenTypeFontMetadata(font(0x636d6170 to cmap12(65, 1)), 0).familyName())
    }

    @Test
    fun collections_readTheSelectedFaceUsingAbsoluteTableOffsets() {
        val first = font(0x6e616d65 to names(Triple(1, "Noto Sans CJK JP", 0x0409)))
        val second = font(0x6e616d65 to names(Triple(1, "Noto Sans CJK SC", 0x0409)))
        val collection = ByteBuffer.allocate(20 + first.limit() + second.limit())
        collection.putInt(0x74746366).putInt(0x00010000).putInt(2)
            .putInt(20).putInt(20 + first.limit())
        for (face in listOf(first, second)) {
            val start = collection.position()
            face.putInt(20, face.getInt(20) + start)
            collection.put(face.array())
        }
        assertEquals("Noto Sans CJK JP", OpenTypeFontMetadata(collection, 0).familyName())
        assertEquals("Noto Sans CJK SC", OpenTypeFontMetadata(collection, 1).familyName())
        assertNull(OpenTypeFontMetadata(collection, 2).familyName())
    }

    @Test
    fun malformedFontsAndTruncatedTables_failWithoutThrowing() {
        for (length in 0..40) {
            val malformed = ByteBuffer.allocate(length)
            val metadata = OpenTypeFontMetadata(malformed, 0)
            assertNull(metadata.familyName())
            assertFalse(metadata.covers(listOf(65)))
        }
        val font = font(0x6e616d65 to names(Triple(1, "Valid", 0x0409)))
        font.putInt(20, Int.MAX_VALUE)
        assertNull(OpenTypeFontMetadata(font, 0).familyName())
    }

    @Test
    fun coverage_checksBmpDeltaAndIndirectMappingsIncludingMissingGlyphZero() {
        for (indirect in listOf(false, true)) {
            val metadata = OpenTypeFontMetadata(font(0x636d6170 to cmap4(65, 3, indirect)), 0)
            assertTrue(metadata.covers(listOf(65)))
            assertFalse(metadata.covers(listOf(65, 66)))
            assertFalse(metadata.covers(listOf(0x0627)))
            assertFalse(metadata.covers(listOf(0xffff)))
            assertFalse(OpenTypeFontMetadata(font(0x636d6170 to cmap4(65, 0, indirect)), 0)
                .covers(listOf(65)))
        }
    }

    @Test
    fun coverage_checksSupplementaryCharactersAndRejectsSymbolOnlyCmaps() {
        val cmap = cmap12(0x12000, 1)
        val metadata = OpenTypeFontMetadata(font(0x636d6170 to cmap), 0)
        assertTrue(metadata.covers(listOf(0x12000)))
        assertFalse(metadata.covers(listOf(65)))
        ByteBuffer.wrap(cmap).putShort(6, 0) // Windows symbol encoding is not a Unicode cmap.
        assertFalse(OpenTypeFontMetadata(font(0x636d6170 to cmap), 0).covers(listOf(0x12000)))
    }

    private fun font(vararg tables: Pair<Int, ByteArray>): ByteBuffer {
        val directorySize = 12 + tables.size * 16
        val bytes = ByteBuffer.allocate(directorySize + tables.sumOf { it.second.size })
        bytes.putInt(0x00010000).putShort(tables.size.toShort())
        bytes.position(12)
        var offset = directorySize
        for ((tag, table) in tables) {
            bytes.putInt(tag).putInt(0).putInt(offset).putInt(table.size)
            offset += table.size
        }
        for ((_, table) in tables) bytes.put(table)
        return bytes
    }

    private fun names(vararg entries: Triple<Int, String, Int>): ByteArray {
        val strings = entries.map { it.second.toByteArray(Charsets.UTF_16BE) }
        val storage = 6 + entries.size * 12
        val bytes = ByteBuffer.allocate(storage + strings.sumOf { it.size })
        bytes.putShort(0).putShort(entries.size.toShort()).putShort(storage.toShort())
        var offset = 0
        for ((index, entry) in entries.withIndex()) {
            bytes.putShort(3).putShort(1).putShort(entry.third.toShort()).putShort(entry.first.toShort())
                .putShort(strings[index].size.toShort()).putShort(offset.toShort())
            offset += strings[index].size
        }
        strings.forEach { bytes.put(it) }
        return bytes.array()
    }

    private fun cmap4(cp: Int, glyph: Int, indirect: Boolean): ByteArray {
        val length = if (indirect) 34 else 32
        val bytes = ByteBuffer.allocate(12 + length)
        bytes.putShort(0).putShort(1).putShort(3).putShort(1).putInt(12)
        bytes.putShort(4).putShort(length.toShort()).putShort(0).putShort(4)
            .putShort(4).putShort(1).putShort(0)
        bytes.putShort(cp.toShort()).putShort(-1).putShort(0)
        bytes.putShort(cp.toShort()).putShort(-1)
        bytes.putShort(if (indirect) 0 else (glyph - cp).toShort()).putShort(1)
        bytes.putShort(if (indirect) 4 else 0).putShort(0)
        if (indirect) bytes.putShort(glyph.toShort())
        return bytes.array()
    }

    private fun cmap12(cp: Int, glyph: Int): ByteArray = ByteBuffer.allocate(40).apply {
        putShort(0).putShort(1).putShort(3).putShort(10).putInt(12)
        putShort(12).putShort(0).putInt(28).putInt(0).putInt(1)
        putInt(cp).putInt(cp).putInt(glyph)
    }.array()
}
