package com.example.mangabanglalive.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangabanglalive.domain.model.OpenRouterSettings
import com.example.mangabanglalive.domain.model.TranslationStyle
import com.example.mangabanglalive.domain.repository.SettingsRepository
import com.example.mangabanglalive.translation.OpenRouterClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val apiKey: String = "",
    val model: String = "",
    val provider: String = "groq",
    val baseUrl: String = "https://openrouter.ai/api/v1",
    val style: TranslationStyle = TranslationStyle.NATURAL_BANGLA,
    val testStatus: String? = null
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val openRouterClient: OpenRouterClient
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.update {
                    it.copy(
                        apiKey = settings.apiKey,
                        model = settings.model,
                        provider = settings.provider,
                        baseUrl = settings.baseUrl,
                        style = settings.style
                    )
                }
            }
        }
    }

    fun updateApiKey(value: String) = _uiState.update { it.copy(apiKey = value) }
    fun updateModel(value: String) = _uiState.update { it.copy(model = value) }
    fun updateProvider(value: String) = _uiState.update { it.copy(provider = value) }
    fun updateBaseUrl(value: String) = _uiState.update { it.copy(baseUrl = value) }
    fun updateStyle(value: TranslationStyle) = _uiState.update { it.copy(style = value) }

    fun saveSettings() {
        viewModelScope.launch {
            val state = _uiState.value
            settingsRepository.updateSettings(
                OpenRouterSettings(
                    apiKey = state.apiKey,
                    model = state.model,
                    provider = state.provider,
                    baseUrl = state.baseUrl,
                    style = state.style
                )
            )
            _uiState.update { it.copy(testStatus = "Saved") }
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.apiKey.isBlank() || state.baseUrl.isBlank()) {
                _uiState.update { it.copy(testStatus = "Missing API key or base URL") }
                return@launch
            }
            val result = runCatching {
                val api = openRouterClient.create(state.baseUrl, state.apiKey)
                api.listModels()
            }
            val status = if (result.isSuccess) {
                "Connection OK"
            } else {
                val message = result.exceptionOrNull()?.message ?: "Unknown error"
                "Connection failed: $message"
            }
            _uiState.update { it.copy(testStatus = status) }
        }
    }
}