package com.example.mangabanglalive.translation

import com.example.mangabanglalive.domain.model.OcrSegment
import com.example.mangabanglalive.domain.model.TranslationSegment
import com.example.mangabanglalive.domain.repository.SettingsRepository
import com.example.mangabanglalive.util.IdGenerator
import kotlinx.coroutines.flow.first

class OpenRouterTranslationEngine(
    private val settingsRepository: SettingsRepository,
    private val client: OpenRouterClient
) : TranslationEngine {

    override suspend fun translate(segments: List<OcrSegment>): List<TranslationSegment> {
        if (segments.isEmpty()) return emptyList()
        val settings = settingsRepository.settingsFlow.first()
        if (settings.apiKey.isBlank() || settings.model.isBlank()) return emptyList()

        val api = client.create(settings.baseUrl, settings.apiKey)

        return segments.map { segment ->
            val prompt = buildPrompt(segment.text, settings.style)
            val request = OpenRouterChatRequest(
                model = settings.model,
                messages = listOf(
                    OpenRouterMessage(role = "system", content = "You are a professional Bangla translator for manga dialogue."),
                    OpenRouterMessage(role = "user", content = prompt)
                ),
                provider = settings.provider.takeIf { it.isNotBlank() }?.let { OpenRouterProvider(order = listOf(it)) }
            )

            val response = runCatching { api.chatCompletions(request) }.getOrNull()
            val translated = response?.choices?.firstOrNull()?.message?.content?.trim().orEmpty()

            TranslationSegment(
                id = IdGenerator.newId(),
                pageId = segment.pageId,
                sourceText = segment.text,
                translatedText = if (translated.isBlank()) segment.text else translated,
                boundingBox = segment.boundingBox,
                provider = settings.provider.ifBlank { null },
                model = settings.model,
                style = settings.style
            )
        }
    }

    private fun buildPrompt(text: String, style: com.example.mangabanglalive.domain.model.TranslationStyle): String {
        val styleHint = when (style) {
            com.example.mangabanglalive.domain.model.TranslationStyle.NATURAL_BANGLA -> "Translate naturally in Bangla."
            com.example.mangabanglalive.domain.model.TranslationStyle.MANGA_BANGLA -> "Translate in manga-style Bangla with short, expressive phrasing."
        }
        return "$styleHint\n\nOriginal: $text\n\nBangla Translation:"
    }
}