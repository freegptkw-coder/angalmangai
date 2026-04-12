package com.example.mangabanglalive.data.repository

import com.example.mangabanglalive.data.local.MangaDatabase
import com.example.mangabanglalive.data.toDomain
import com.example.mangabanglalive.data.toEntity
import com.example.mangabanglalive.domain.model.OcrSegment
import com.example.mangabanglalive.domain.repository.OcrRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OcrRepositoryImpl(private val database: MangaDatabase) : OcrRepository {
    override fun observeSegments(pageId: String): Flow<List<OcrSegment>> {
        return database.ocrSegmentDao().observeSegments(pageId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun upsertSegments(pageId: String, segments: List<OcrSegment>) {
        database.ocrSegmentDao().insertAll(segments.map { it.copy(pageId = pageId).toEntity() })
    }
}