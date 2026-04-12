package com.example.mangabanglalive.domain.model

data class TranslationSegment(
    val id: String,
    val pageId: String,
    val sourceText: String,
    val translatedText: String,
    val boundingBox: BoundingBox,
    val provider: String?,
    val model: String?,
    val style: TranslationStyle
)