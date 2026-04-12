package com.example.mangabanglalive.domain.model

data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top

    fun clamp(): BoundingBox = BoundingBox(
        left.coerceIn(0f, 1f),
        top.coerceIn(0f, 1f),
        right.coerceIn(0f, 1f),
        bottom.coerceIn(0f, 1f)
    )
}