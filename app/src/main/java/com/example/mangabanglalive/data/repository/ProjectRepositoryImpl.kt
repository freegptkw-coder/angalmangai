package com.example.mangabanglalive.data.repository

import com.example.mangabanglalive.data.local.MangaDatabase
import com.example.mangabanglalive.data.toDomain
import com.example.mangabanglalive.data.toEntity
import com.example.mangabanglalive.domain.model.MangaPage
import com.example.mangabanglalive.domain.model.MangaProject
import com.example.mangabanglalive.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepositoryImpl(private val database: MangaDatabase) : ProjectRepository {
    override fun observeRecentProjects(limit: Int): Flow<List<MangaProject>> {
        return database.projectDao().observeRecent(limit).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getProject(projectId: String): MangaProject? {
        return database.projectDao().getProject(projectId)?.toDomain()
    }

    override suspend fun getPages(projectId: String): List<MangaPage> {
        return database.pageDao().getPages(projectId).map { it.toDomain() }
    }

    override suspend fun createProject(project: MangaProject, pages: List<MangaPage>): MangaProject {
        database.projectDao().insert(project.toEntity())
        database.pageDao().insertAll(pages.map { it.toEntity() })
        return project
    }

    override suspend fun updateProject(project: MangaProject) {
        database.projectDao().update(project.toEntity())
    }
}