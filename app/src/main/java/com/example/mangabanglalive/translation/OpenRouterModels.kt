package com.example.mangabanglalive.translation

import com.squareup.moshi.Json

data class OpenRouterModelsResponse(
    val data: List<OpenRouterModelInfo> = emptyList()
)

data class OpenRouterModelInfo(
    val id: String = "",
    val name: String? = null
)

data class OpenRouterChatRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val provider: OpenRouterProvider? = null,
    val temperature: Double? = 0.2
)

data class OpenRouterProvider(
    val order: List<String>
)

data class OpenRouterChatResponse(
    val choices: List<OpenRouterChoice> = emptyList()
)

data class OpenRouterChoice(
    val message: OpenRouterMessage? = null
)

data class OpenRouterMessage(
    val role: String,
    val content: String
)