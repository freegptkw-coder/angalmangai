package com.example.mangabanglalive.domain.model

data class OcrSegment(
    val id: String,
    val pageId: String,
    val text: String,
    val boundingBox: BoundingBox,
    val confidence: Float?,
    val language: String?
)