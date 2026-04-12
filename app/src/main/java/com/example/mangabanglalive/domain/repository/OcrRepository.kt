package com.example.mangabanglalive.domain.repository

import com.example.mangabanglalive.domain.model.OcrSegment
import kotlinx.coroutines.flow.Flow

interface OcrRepository {
    fun observeSegments(pageId: String): Flow<List<OcrSegment>>
    suspend fun upsertSegments(pageId: String, segments: List<OcrSegment>)
}