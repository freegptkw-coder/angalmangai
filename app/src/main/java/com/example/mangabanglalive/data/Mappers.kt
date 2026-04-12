package com.example.mangabanglalive.data

import com.example.mangabanglalive.data.local.OcrSegmentEntity
import com.example.mangabanglalive.data.local.PageEntity
import com.example.mangabanglalive.data.local.ProjectEntity
import com.example.mangabanglalive.data.local.TranslationSegmentEntity
import com.example.mangabanglalive.domain.model.BoundingBox
import com.example.mangabanglalive.domain.model.MangaPage
import com.example.mangabanglalive.domain.model.MangaProject
import com.example.mangabanglalive.domain.model.OcrSegment
import com.example.mangabanglalive.domain.model.SourceType
import com.example.mangabanglalive.domain.model.TranslationSegment
import com.example.mangabanglalive.domain.model.TranslationStyle

fun ProjectEntity.toDomain() = MangaProject(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    sourceType = SourceType.valueOf(sourceType),
    sourceUri = sourceUri
)

fun MangaProject.toEntity() = ProjectEntity(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    sourceType = sourceType.name,
    sourceUri = sourceUri
)

fun PageEntity.toDomain() = MangaPage(
    id = id,
    projectId = projectId,
    index = pageIndex,
    imageUri = imageUri,
    pdfPageIndex = pdfPageIndex
)

fun MangaPage.toEntity() = PageEntity(
    id = id,
    projectId = projectId,
    pageIndex = index,
    imageUri = imageUri,
    pdfPageIndex = pdfPageIndex
)

fun OcrSegmentEntity.toDomain() = OcrSegment(
    id = id,
    pageId = pageId,
    text = text,
    boundingBox = BoundingBox(left, top, right, bottom),
    confidence = confidence,
    language = language
)

fun OcrSegment.toEntity() = OcrSegmentEntity(
    id = id,
    pageId = pageId,
    text = text,
    left = boundingBox.left,
    top = boundingBox.top,
    right = boundingBox.right,
    bottom = boundingBox.bottom,
    confidence = confidence,
    language = language
)

fun TranslationSegmentEntity.toDomain() = TranslationSegment(
    id = id,
    pageId = pageId,
    sourceText = sourceText,
    translatedText = translatedText,
    boundingBox = BoundingBox(left, top, right, bottom),
    provider = provider,
    model = model,
    style = TranslationStyle.valueOf(style)
)

fun TranslationSegment.toEntity() = TranslationSegmentEntity(
    id = id,
    pageId = pageId,
    sourceText = sourceText,
    translatedText = translatedText,
    left = boundingBox.left,
    top = boundingBox.top,
    right = boundingBox.right,
    bottom = boundingBox.bottom,
    provider = provider,
    model = model,
    style = style.name
)