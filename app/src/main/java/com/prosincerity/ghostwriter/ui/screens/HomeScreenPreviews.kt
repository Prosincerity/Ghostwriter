package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.prosincerity.ghostwriter.data.ProjectSummary
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme

@Preview(name = "Recent projects", widthDp = 390, heightDp = 844)
@Preview(name = "Projects with larger text", widthDp = 360, heightDp = 800, fontScale = 1.5f)
@Composable
private fun RecentProjectsPreview() {
    val projects = listOf(
        ProjectSummary("Night shift", 1790881200000L, 92, "C minor"),
        ProjectSummary("Loose lines", 1790794800000L),
        ProjectSummary("Side B", 1790708400000L, 120, "F minor"),
    )
    GhostwriterTheme {
        HomeScreen(
            existingProjects = projects.map { it.title },
            projectSummaries = projects.associateBy { it.title },
            onCreateProject = {}, onOpenProject = {}, onDeleteProject = {},
            onRenameProject = { _, _ -> }, onOpenSettings = {},
        )
    }
}

@Preview(name = "First project", widthDp = 390, heightDp = 844)
@Composable
private fun EmptyProjectsPreview() {
    GhostwriterTheme {
        HomeScreen(
            existingProjects = emptyList(),
            onCreateProject = {}, onOpenProject = {}, onDeleteProject = {},
            onRenameProject = { _, _ -> }, onOpenSettings = {},
        )
    }
}
