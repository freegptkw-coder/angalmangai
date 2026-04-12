package com.example.mangabanglalive.domain.model

data class OpenRouterSettings(
    val apiKey: String = "",
    val model: String = "",
    val provider: String = "",
    val baseUrl: String = "https://openrouter.ai/api/v1",
    val style: TranslationStyle = TranslationStyle.NATURAL_BANGLA
)