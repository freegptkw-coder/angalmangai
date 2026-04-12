package com.example.mangabanglalive.translation

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OpenRouterApi {
    @GET("models")
    suspend fun listModels(): OpenRouterModelsResponse

    @POST("chat/completions")
    suspend fun chatCompletions(@Body request: OpenRouterChatRequest): OpenRouterChatResponse
}