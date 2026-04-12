package com.example.mangabanglalive.domain.repository

import com.example.mangabanglalive.domain.model.TranslationSegment
import kotlinx.coroutines.flow.Flow

interface TranslationRepository {
    fun observeTranslations(pageId: String): Flow<List<TranslationSegment>>
    suspend fun upsertTranslations(pageId: String, translations: List<TranslationSegment>)
}