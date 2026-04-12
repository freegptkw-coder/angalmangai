package com.example.mangabanglalive.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val sourceType: String,
    val sourceUri: String?
)

@Entity(tableName = "pages")
data class PageEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val pageIndex: Int,
    val imageUri: String?,
    val pdfPageIndex: Int?
)

@Entity(tableName = "ocr_segments")
data class OcrSegmentEntity(
    @PrimaryKey val id: String,
    val pageId: String,
    val text: String,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val confidence: Float?,
    val language: String?
)

@Entity(tableName = "translation_segments")
data class TranslationSegmentEntity(
    @PrimaryKey val id: String,
    val pageId: String,
    val sourceText: String,
    val translatedText: String,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val provider: String?,
    val model: String?,
    val style: String
)