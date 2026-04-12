package com.example.mangabanglalive.ocr

import android.graphics.Bitmap
import com.example.mangabanglalive.domain.model.BoundingBox
import com.example.mangabanglalive.domain.model.OcrSegment
import com.example.mangabanglalive.util.IdGenerator
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

class MlKitOcrEngine : OcrEngine {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun recognize(bitmap: Bitmap): List<OcrSegment> {
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = recognizer.process(image).await()
        return result.textBlocks.flatMap { block ->
            block.lines.mapNotNull { line ->
                line.boundingBox?.let { rect ->
                    val bbox = BoundingBox(
                        left = rect.left.toFloat() / bitmap.width,
                        top = rect.top.toFloat() / bitmap.height,
                        right = rect.right.toFloat() / bitmap.width,
                        bottom = rect.bottom.toFloat() / bitmap.height
                    ).clamp()
                    OcrSegment(
                        id = IdGenerator.newId(),
                        pageId = "",
                        text = line.text,
                        boundingBox = bbox,
                        confidence = null,
                        language = null
                    )
                }
            }
        }
    }
}