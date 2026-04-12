package com.example.mangabanglalive.ocr

import android.graphics.Bitmap
import com.example.mangabanglalive.domain.model.OcrSegment

interface OcrEngine {
    suspend fun recognize(bitmap: Bitmap): List<OcrSegment>
}