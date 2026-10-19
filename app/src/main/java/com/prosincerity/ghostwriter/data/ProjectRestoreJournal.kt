package com.prosincerity.ghostwriter.data

import java.io.File
import java.io.IOException
import java.security.MessageDigest
import org.json.JSONArray
import org.json.JSONObject

/** Write-ahead file states distinguish interrupted recovery from subsequent local edits. */
internal class ProjectRestoreJournal(private val project: File, private val archive: String) {
    private val file = File(project, ".lyric-restore.json")
    val exists: Boolean get() = file.isFile

    private fun trackedFiles(): List<File> = project.listFiles().orEmpty().filter {
        it.isFile && (it.extension.equals("txt", ignoreCase = true) || it.name == "project.json")
    }

    private fun signature(bytes: ByteArray, modifiedAt: Long?): String {
        val hash = MessageDigest.getInstance("SHA-256").digest(bytes)
            .joinToString("") { "%02x".format(it) }
        return if (modifiedAt == null) hash else "$hash:$modifiedAt"
    }

    private fun signature(file: File): String? = if (!file.isFile) null else signature(
        file.readBytes(), file.lastModified().takeIf { file.extension.equals("txt", ignoreCase = true) },
    )

    /** Every file must still be in its original state or its planned restored state. */
    fun matches(): Boolean {
        val json = JSONObject(file.readText())
        if (json.getString("archive") != archive) return false
        val states = json.getJSONObject("files")
        if (trackedFiles().any { !states.has(it.name) }) return false
        for (name in states.keys()) {
            val allowed = states.getJSONArray(name)
            val actual = signature(File(project, name))
            if ((0 until allowed.length()).none {
                actual == if (allowed.isNull(it)) null else allowed.getString(it)
            }) return false
        }
        return true
    }

    fun prepare(replacements: Map<String, Pair<String, Long?>>, olderThanRestored: Long) {
        val originals = trackedFiles().associateBy { it.name }
        val states = JSONObject()
        for (name in originals.keys + replacements.keys) {
            val original = originals[name]
            val before = original?.let(::signature)
            val replacement = replacements[name]
            val after = if (replacement != null) {
                signature(replacement.first.toByteArray(Charsets.UTF_8), replacement.second)
            } else {
                signature(original!!.readBytes(), original.lastModified().let {
                    if (original.extension.equals("txt", ignoreCase = true)) minOf(it, olderThanRestored) else it
                })
            }
            states.put(name, JSONArray().put(before ?: JSONObject.NULL).put(after))
        }
        StagedFileWriter.writeText(file, JSONObject().put("archive", archive).put("files", states).toString())
    }

    fun clear() {
        if (file.exists() && !file.delete()) throw IOException("Couldn't finish lyric recovery")
    }
}
