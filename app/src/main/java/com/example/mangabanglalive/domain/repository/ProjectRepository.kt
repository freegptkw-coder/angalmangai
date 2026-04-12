package com.example.mangabanglalive.domain.repository

import com.example.mangabanglalive.domain.model.MangaPage
import com.example.mangabanglalive.domain.model.MangaProject
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun observeRecentProjects(limit: Int = 20): Flow<List<MangaProject>>
    suspend fun getProject(projectId: String): MangaProject?
    suspend fun getPages(projectId: String): List<MangaPage>
    suspend fun createProject(project: MangaProject, pages: List<MangaPage>): MangaProject
    suspend fun updateProject(project: MangaProject)
}