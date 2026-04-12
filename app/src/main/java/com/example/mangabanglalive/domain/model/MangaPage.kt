package com.example.mangabanglalive.domain.model

data class MangaPage(
    val id: String,
    val projectId: String,
    val index: Int,
    val imageUri: String?,
    val pdfPageIndex: Int? = null
)