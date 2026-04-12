package com.example.mangabanglalive.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mangabanglalive.domain.model.OpenRouterSettings
import com.example.mangabanglalive.domain.model.TranslationStyle
import com.example.mangabanglalive.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("manga_settings")

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    private val apiKeyKey = stringPreferencesKey("openrouter_api_key")
    private val modelKey = stringPreferencesKey("openrouter_model")
    private val providerKey = stringPreferencesKey("openrouter_provider")
    private val baseUrlKey = stringPreferencesKey("openrouter_base_url")
    private val styleKey = stringPreferencesKey("translation_style")

    override val settingsFlow: Flow<OpenRouterSettings> = context.dataStore.data.map { prefs ->
        OpenRouterSettings(
            apiKey = prefs[apiKeyKey] ?: "",
            model = prefs[modelKey] ?: "",
            provider = prefs[providerKey] ?: "",
            baseUrl = prefs[baseUrlKey] ?: "https://openrouter.ai/api/v1",
            style = when (prefs[styleKey]) {
                TranslationStyle.MANGA_BANGLA.name -> TranslationStyle.MANGA_BANGLA
                else -> TranslationStyle.NATURAL_BANGLA
            }
        )
    }

    override suspend fun updateSettings(settings: OpenRouterSettings) {
        context.dataStore.edit { prefs ->
            prefs[apiKeyKey] = settings.apiKey
            prefs[modelKey] = settings.model
            prefs[providerKey] = settings.provider
            prefs[baseUrlKey] = settings.baseUrl
            prefs[styleKey] = settings.style.name
        }
    }
}