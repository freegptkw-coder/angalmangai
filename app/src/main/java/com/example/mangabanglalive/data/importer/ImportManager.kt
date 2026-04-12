package com.example.mangabanglalive.data.importer

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.mangabanglalive.domain.model.MangaPage
import com.example.mangabanglalive.domain.model.MangaProject
import com.example.mangabanglalive.domain.model.SourceType
import com.example.mangabanglalive.domain.repository.ProjectRepository
import com.example.mangabanglalive.util.IdGenerator
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.zip.ZipInputStream

class ImportManager(
    private val context: Context,
    private val projectRepository: ProjectRepository
) {
    suspend fun importImage(uri: Uri): MangaProject {
        return createProject(
            title = "Image ${System.currentTimeMillis()}",
            sourceType = SourceType.IMAGE,
            sourceUri = uri.toString(),
            pages = listOf(createPage(0, uri.toString()))
        )
    }

    suspend fun importFolder(treeUri: Uri): MangaProject {
        val folder = DocumentFile.fromTreeUri(context, treeUri)
        val imageUris = folder?.listFiles()?.filter { it.isFile && it.type?.startsWith("image/") == true }
            ?.sortedBy { it.name?.lowercase(Locale.getDefault()) }
            ?.map { it.uri.toString() }
            .orEmpty()

        val pages = imageUris.mapIndexed { index, uri -> createPage(index, uri) }
        return createProject(
            title = folder?.name ?: "Folder ${System.currentTimeMillis()}",
            sourceType = SourceType.FOLDER,
            sourceUri = treeUri.toString(),
            pages = pages
        )
    }

    suspend fun importArchive(uri: Uri): MangaProject {
        val projectId = IdGenerator.newId()
        val projectDir = File(context.cacheDir, "archives/$projectId").apply { mkdirs() }

        val pages = mutableListOf<MangaPage>()
        context.contentResolver.openInputStream(uri)?.use { input ->
            ZipInputStream(input).use { zip ->
                var entry = zip.nextEntry
                var index = 0
                while (entry != null) {
                    val name = entry.name.lowercase(Locale.getDefault())
                    val isImage = name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp")
                    if (!entry.isDirectory && isImage) {
                        val outFile = File(projectDir, "page_${index}_${File(name).name}")
                        FileOutputStream(outFile).use { output ->
                            zip.copyTo(output)
                        }
                        pages.add(createPage(index, outFile.toURI().toString()))
                        index++
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        }

        return createProject(
            id = projectId,
            title = "Archive ${System.currentTimeMillis()}",
            sourceType = SourceType.ARCHIVE,
            sourceUri = uri.toString(),
            pages = pages
        )
    }

    suspend fun importPdf(uri: Uri): MangaProject {
        return createProject(
            title = "PDF ${System.currentTimeMillis()}",
            sourceType = SourceType.PDF,
            sourceUri = uri.toString(),
            pages = listOf(createPage(0, null, pdfPageIndex = 0))
        )
    }

    private fun createPage(index: Int, imageUri: String?, pdfPageIndex: Int? = null): MangaPage {
        return MangaPage(
            id = IdGenerator.newId(),
            projectId = "",
            index = index,
            imageUri = imageUri,
            pdfPageIndex = pdfPageIndex
        )
    }

    private suspend fun createProject(
        title: String,
        sourceType: SourceType,
        sourceUri: String?,
        pages: List<MangaPage>,
        id: String = IdGenerator.newId()
    ): MangaProject {
        val now = System.currentTimeMillis()
        val project = MangaProject(
            id = id,
            title = title,
            createdAt = now,
            updatedAt = now,
            sourceType = sourceType,
            sourceUri = sourceUri
        )

        val mappedPages = pages.map { it.copy(projectId = project.id) }
        projectRepository.createProject(project, mappedPages)
        return project
    }
}