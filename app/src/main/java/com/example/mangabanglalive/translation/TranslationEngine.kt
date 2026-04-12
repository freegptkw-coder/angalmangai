package com.example.mangabanglalive.translation

import com.example.mangabanglalive.domain.model.OcrSegment
import com.example.mangabanglalive.domain.model.TranslationSegment

interface TranslationEngine {
    suspend fun translate(segments: List<OcrSegment>): List<TranslationSegment>
}