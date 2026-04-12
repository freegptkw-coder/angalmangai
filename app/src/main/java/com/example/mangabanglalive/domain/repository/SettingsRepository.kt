package com.example.mangabanglalive.domain.repository

import com.example.mangabanglalive.domain.model.OpenRouterSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settingsFlow: Flow<OpenRouterSettings>
    suspend fun updateSettings(settings: OpenRouterSettings)
}