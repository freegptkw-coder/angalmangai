package com.example.mangabanglalive.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.mangabanglalive.domain.model.TranslationSegment
import kotlin.math.roundToInt

@Composable
fun TranslationOverlay(
    segments: List<TranslationSegment>,
    onEditSegment: (TranslationSegment) -> Unit,
    modifier: Modifier = Modifier
) {
    if (segments.isEmpty()) return

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        segments.forEach { segment ->
            val left = segment.boundingBox.left * widthPx
            val top = segment.boundingBox.top * heightPx
            val boxWidth = segment.boundingBox.width * widthPx
            val boxHeight = segment.boundingBox.height * heightPx

            Box(
                modifier = Modifier
                    .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                    .size(
                        with(density) { boxWidth.toDp() },
                        with(density) { boxHeight.toDp() }
                    )
                    .background(Color(0xB3000000))
                    .clickable { onEditSegment(segment) }
            ) {
                Text(
                    text = segment.translatedText,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}