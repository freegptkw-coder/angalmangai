package com.example.mangabanglalive.data.repository

import com.example.mangabanglalive.data.local.MangaDatabase
import com.example.mangabanglalive.data.toDomain
import com.example.mangabanglalive.data.toEntity
import com.example.mangabanglalive.domain.model.TranslationSegment
import com.example.mangabanglalive.domain.repository.TranslationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TranslationRepositoryImpl(private val database: MangaDatabase) : TranslationRepository {
    override fun observeTranslations(pageId: String): Flow<List<TranslationSegment>> {
        return database.translationSegmentDao().observeSegments(pageId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun upsertTranslations(pageId: String, translations: List<TranslationSegment>) {
        database.translationSegmentDao().insertAll(translations.map { it.copy(pageId = pageId).toEntity() })
    }
}