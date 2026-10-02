package com.prosincerity.ghostwriter.data

import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Reads standard OpenType metadata from Font.getBuffer(), including individual TTC faces.
 * Android exposes files and language tags, but no public font-family display-name API.
 * Spec: https://learn.microsoft.com/en-us/typography/opentype/spec/name
 */
internal class OpenTypeFontMetadata(buffer: ByteBuffer, private val ttcIndex: Int) {
    private val bytes = buffer.duplicate().order(ByteOrder.BIG_ENDIAN).apply { position(0) }

    private fun u16(offset: Int): Int = bytes.getShort(offset).toInt() and 0xffff
    private fun u32(offset: Int): Int {
        val value = bytes.getInt(offset).toLong() and 0xffffffffL
        require(value <= Int.MAX_VALUE)
        return value.toInt()
    }

    private fun region(offset: Int, length: Int): ByteBuffer {
        require(offset >= 0 && length >= 0 && offset.toLong() + length <= bytes.limit())
        return bytes.duplicate().apply { position(offset); limit(offset + length) }
            .slice().order(ByteOrder.BIG_ENDIAN)
    }

    private fun table(tag: Int): ByteBuffer? {
        val collection = bytes.getInt(0) == 0x74746366 // ttcf
        val directory = if (collection) {
            require(ttcIndex >= 0 && ttcIndex < u32(8))
            u32(12 + ttcIndex * 4)
        } else {
            require(ttcIndex == 0)
            0
        }
        val records = region(directory + 12, u16(directory + 4) * 16)
        for (index in 0 until records.limit() / 16) {
            val record = index * 16
            if (records.getInt(record) == tag) {
                // Table offsets are relative to the entire file, also inside a TTC.
                return region(u32(directory + 12 + record + 8), u32(directory + 12 + record + 12))
            }
        }
        return null
    }

    fun familyName(): String? = safely {
        val names = table(0x6e616d65) ?: return@safely null // name
        require(names.getShort(0).toInt() in 0..1)
        val count = names.getShort(2).toInt() and 0xffff
        val storage = names.getShort(4).toInt() and 0xffff
        require(6L + count * 12L <= names.limit())
        val candidates = mutableListOf<Pair<Int, String>>()
        for (index in 0 until count) {
            val record = 6 + index * 12
            fun field(offset: Int) = names.getShort(record + offset).toInt() and 0xffff
            val platform = field(0)
            val encoding = field(2)
            val language = field(4)
            val id = field(6)
            if (id != 16 && id != 1) continue
            val charset = when {
                platform == 0 || (platform == 3 && encoding in listOf(0, 1, 10)) -> Charsets.UTF_16BE
                platform == 1 && encoding == 0 && java.nio.charset.Charset.isSupported("x-MacRoman") ->
                    java.nio.charset.Charset.forName("x-MacRoman")
                else -> continue
            }
            val length = field(8)
            val offset = storage + field(10)
            if (length == 0 || offset.toLong() + length > names.limit()) continue
            val encoded = ByteArray(length)
            names.duplicate().apply { position(offset); get(encoded) }
            val name = String(encoded, charset).trim()
            if (name.isEmpty() || name.any { it.isISOControl() || it == '\uFFFD' }) continue
            // Use one stable English/Unicode name across styles, rather than full names
            // (ID 4) or PostScript identifiers (ID 6). Prefer typographic family (ID 16).
            val rank = (if (id == 16) 0 else 100) + when {
                platform == 3 && language == 0x0409 -> 0
                platform == 0 -> 1
                platform == 1 && language == 0 -> 2
                else -> 3
            }
            candidates += rank to name
        }
        candidates.sortedWith(compareBy<Pair<Int, String>> { it.first }.thenBy { it.second })
            .firstOrNull()?.second
    }

    /** Direct coverage check: Paint.hasGlyph would also consult Android fallback fonts. */
    fun covers(codePoints: List<Int>): Boolean = safely {
        val cmap = table(0x636d6170) ?: return@safely false
        val count = cmap.getShort(2).toInt() and 0xffff
        require(4L + count * 8L <= cmap.limit())
        val subtables = (0 until count).mapNotNull { index ->
            val record = 4 + index * 8
            val platform = cmap.getShort(record).toInt() and 0xffff
            val encoding = cmap.getShort(record + 2).toInt() and 0xffff
            if (platform != 0 && !(platform == 3 && encoding in listOf(1, 10))) return@mapNotNull null
            val offset = cmap.getInt(record + 4)
            if (offset < 0 || offset.toLong() + 2 > cmap.limit()) return@mapNotNull null
            val format = cmap.getShort(offset).toInt() and 0xffff
            if (format == 4 || format == 12) offset else null
        }
        codePoints.all { cp -> subtables.any { offset ->
            val format = cmap.getShort(offset).toInt()
            val length = if (format == 4) cmap.getShort(offset + 2).toInt() and 0xffff
                else cmap.getInt(offset + 4)
            require(length >= 16 && offset.toLong() + length <= cmap.limit())
            val subtable = cmap.duplicate().apply { position(offset); limit(offset + length) }
                .slice().order(ByteOrder.BIG_ENDIAN)
            hasGlyph(subtable, 0, cp)
        } }
    } ?: false

    private fun hasGlyph(cmap: ByteBuffer, offset: Int, cp: Int): Boolean {
        return when (cmap.getShort(offset).toInt()) {
            12 -> {
                val count = cmap.getInt(offset + 12)
                require(count >= 0 && offset + 16L + count * 12L <= cmap.limit())
                var low = 0
                var high = count - 1
                while (low <= high) {
                    val mid = (low + high) ushr 1
                    val group = offset + 16 + mid * 12
                    val start = cmap.getInt(group)
                    val end = cmap.getInt(group + 4)
                    when {
                        cp < start -> high = mid - 1
                        cp > end -> low = mid + 1
                        else -> return cmap.getInt(group + 8).toLong() + cp - start != 0L
                    }
                }
                false
            }
            4 -> {
                if (cp > 0xffff) return false
                val count = (cmap.getShort(offset + 6).toInt() and 0xffff) / 2
                require(offset + 16L + count * 8L <= cmap.limit())
                for (index in 0 until count) {
                    fun word(at: Int) = cmap.getShort(at).toInt() and 0xffff
                    val end = word(offset + 14 + index * 2)
                    val start = word(offset + 16 + count * 2 + index * 2)
                    if (cp !in start..end) continue
                    val delta = word(offset + 16 + count * 4 + index * 2)
                    val rangePosition = offset + 16 + count * 6 + index * 2
                    val range = word(rangePosition)
                    if (range == 0) return (cp + delta) and 0xffff != 0
                    val glyphPosition = rangePosition + range + (cp - start) * 2
                    val glyph = word(glyphPosition)
                    return glyph != 0 && (glyph + delta) and 0xffff != 0
                }
                false
            }
            else -> false
        }
    }

    private fun <T> safely(read: () -> T?): T? = try {
        read()
    } catch (_: IllegalArgumentException) {
        null
    } catch (_: IndexOutOfBoundsException) {
        null
    }
}
