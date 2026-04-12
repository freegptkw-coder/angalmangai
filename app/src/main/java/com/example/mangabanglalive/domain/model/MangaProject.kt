package com.example.mangabanglalive.domain.model

data class MangaProject(
    val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val sourceType: SourceType,
    val sourceUri: String?
)