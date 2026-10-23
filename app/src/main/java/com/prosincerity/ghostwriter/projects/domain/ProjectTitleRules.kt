package com.prosincerity.ghostwriter.projects.domain

internal sealed interface ProjectTitleState {
    val title: String

    data class Available(override val title: String) : ProjectTitleState
    data class Existing(override val title: String) : ProjectTitleState
}

/** Display titles are independent of filenames and keep punctuation and Unicode. */
internal object ProjectTitleRules {
    fun normalize(title: String): String = title.trim().ifBlank { "Untitled" }

    fun resolve(title: String, existingProjects: List<String>): String {
        val normalized = normalize(title)
        return existingProjects.firstOrNull { it.equals(normalized, ignoreCase = true) } ?: normalized
    }

    fun forCreation(input: String, existingProjects: List<String>): ProjectTitleState {
        val title = resolve(input, existingProjects)
        return if (title in existingProjects) ProjectTitleState.Existing(title)
        else ProjectTitleState.Available(title)
    }

    fun forRename(input: String, currentTitle: String, existingProjects: List<String>): ProjectTitleState {
        val title = normalize(input)
        // Exclude only the exact current title; a different case variant can belong to another project.
        val alreadyExists = existingProjects.any { it != currentTitle && it.equals(title, ignoreCase = true) }
        return if (alreadyExists) ProjectTitleState.Existing(title) else ProjectTitleState.Available(title)
    }
}
