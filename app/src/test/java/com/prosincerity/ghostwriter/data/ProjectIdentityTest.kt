package com.prosincerity.ghostwriter.data

import java.io.File
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ProjectIdentityTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test fun collidingAndDuplicateDisplayTitlesHaveIndependentIds() {
        val root = temporaryFolder.root
        val projects = listOf("verse/chorus", "verse_chorus", "verse/chorus", "字".repeat(300))
            .map { ProjectStorage.createProjectDirectory(root, it) }
        assertEquals(4, projects.map { it.name }.distinct().size)
        projects.forEach { assertTrue(ProjectStorage.isProjectId(it.name)) }
        assertEquals("verse/chorus", ProjectStorage.loadMetadata(projects[0], "").title)
        assertEquals("字".repeat(300), ProjectStorage.loadMetadata(projects[3], "").title)
    }

    @Test fun legacyMigrationKeepsContentMetadataAndSnapshotAgesAndIsIdempotent() {
        val root = temporaryFolder.root
        val legacy = temporaryFolder.newFolder("My Track")
        File(legacy, "My Track.txt").apply { writeText("manual"); setLastModified(1000L) }
        File(legacy, "autosave2.txt").apply { writeText("latest"); setLastModified(2000L) }
        File(legacy, "beat.wav").writeText("beat")
        File(legacy, "project.json").writeText("""{"title":"My Track","updatedAt":500,"notes":"keep me","unknownLegacyField":"preserve"}""")
        ProjectStorage.migrateLegacyProjects(root)
        val migrated = root.listFiles()!!.single()
        assertTrue(ProjectStorage.isProjectId(migrated.name))
        assertFalse(legacy.exists())
        assertEquals("latest", ProjectStorage.loadLatest(migrated))
        assertEquals(2000L, File(migrated, "autosave2.txt").lastModified())
        assertEquals("beat", File(migrated, "beat.wav").readText())
        val metadata = ProjectStorage.loadMetadata(migrated, "fallback")
        assertEquals("My Track", metadata.title)
        assertEquals("keep me", metadata.notes)
        assertEquals(500L, metadata.updatedAt)
        assertEquals("preserve", org.json.JSONObject(File(migrated, "project.json").readText()).getString("unknownLegacyField"))
        ProjectStorage.migrateLegacyProjects(root)
        assertEquals(listOf(migrated.name), root.list()!!.toList())
        assertEquals(migrated, ProjectStorage.renameProjectDirectory(migrated, "Renamed / song"))
        assertEquals("latest", ProjectStorage.loadLatest(migrated))
    }

    @Test fun missingLegacyTimestampsBecomeStableWithoutDroppingUnknownFields() {
        val legacy = temporaryFolder.newFolder("Older")
        File(legacy, "Older.txt").apply { writeText("lyrics"); setLastModified(1000L) }
        File(legacy, "project.json").apply {
            writeText("""{"title":"Older","unknown":"keep"}""")
            setLastModified(1500L)
        }
        ProjectStorage.migrateLegacyProjects(temporaryFolder.root)
        val project = temporaryFolder.root.listFiles()!!.single()
        val metadata = ProjectStorage.loadMetadata(project, project.name)
        assertEquals(1500L, metadata.createdAt)
        assertEquals(1500L, metadata.updatedAt)
        assertEquals(metadata, ProjectStorage.loadMetadata(project, project.name))
    }

    @Test fun failedLegacyMigrationLeavesAllOriginalFilesForRetry() {
        val legacy = temporaryFolder.newFolder("Original")
        File(legacy, "Original.txt").writeText("lyrics")
        File(legacy, "project.json").mkdir()
        File(legacy, "project.json/keep").writeText("metadata")
        assertThrows(Exception::class.java) { ProjectStorage.migrateLegacyProjects(temporaryFolder.root) }
        assertTrue(legacy.isDirectory)
        assertEquals("lyrics", File(legacy, "Original.txt").readText())
        assertEquals(listOf("Original"), temporaryFolder.root.list()!!.toList())
    }
}
