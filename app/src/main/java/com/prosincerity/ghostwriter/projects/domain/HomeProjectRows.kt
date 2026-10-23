package com.prosincerity.ghostwriter.projects.domain

import com.prosincerity.ghostwriter.projects.data.ProjectSummary
import java.util.Locale

/** A rendered title and details always retain the original project identity for actions. */
internal data class HomeProjectRow(val projectId: String, val title: String, val details: String?)

internal fun homeProjectRows(
    existingProjects: List<String>,
    projectSummaries: Map<String, ProjectSummary>,
    projectTitles: Map<String, String>,
    formatEditedAt: (Long) -> String,
): List<HomeProjectRow> = existingProjects.sortedWith(
    compareByDescending<String> { projectSummaries[it]?.lastEditedAt ?: 0L }
        .thenBy { (projectTitles[it] ?: it).lowercase(Locale.ROOT) },
).map { projectId ->
    HomeProjectRow(
        projectId = projectId,
        title = projectTitles[projectId] ?: projectId,
        details = projectSummaries[projectId]?.let { summary ->
            projectDetails(summary, formatEditedAt)
        },
    )
}

private fun projectDetails(summary: ProjectSummary, formatEditedAt: (Long) -> String): String? =
    buildList {
        if (summary.lastEditedAt > 0) add("Edited ${formatEditedAt(summary.lastEditedAt)}")
        summary.bpm?.let { add("$it BPM") }
        summary.key?.let { add(it) }
    }.takeIf { it.isNotEmpty() }?.joinToString(" · ")
