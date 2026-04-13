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
    val liveTranslateEnabled: Boolean = false,
    val showOriginalText: Boolean = false,
    val captionLimit: Int = 500,
    val glossary: String = "",
    val testStatus: String? = null,
    val saveStatus: String? = null
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
                        style = settings.style,
                        liveTranslateEnabled = settings.liveTranslateEnabled,
                        showOriginalText = settings.showOriginalText,
                        captionLimit = settings.captionLimit,
                        glossary = settings.glossary,
                        saveStatus = null
                    )
                }
            }
        }
    }

    fun updateApiKey(value: String) = _uiState.update { it.copy(apiKey = value, saveStatus = null, testStatus = null) }
    fun updateModel(value: String) = _uiState.update { it.copy(model = value, saveStatus = null, testStatus = null) }
    fun updateProvider(value: String) = _uiState.update { it.copy(provider = value, saveStatus = null, testStatus = null) }
    fun updateBaseUrl(value: String) = _uiState.update { it.copy(baseUrl = value, saveStatus = null, testStatus = null) }
    fun updateStyle(value: TranslationStyle) = _uiState.update { it.copy(style = value, saveStatus = null) }
    fun updateLiveTranslateEnabled(value: Boolean) = _uiState.update { it.copy(liveTranslateEnabled = value, saveStatus = null) }
    fun updateShowOriginalText(value: Boolean) = _uiState.update { it.copy(showOriginalText = value, saveStatus = null) }
    fun updateCaptionLimit(value: Int) = _uiState.update { it.copy(captionLimit = value.coerceIn(50, 500), saveStatus = null) }
    fun updateGlossary(value: String) = _uiState.update { it.copy(glossary = value, saveStatus = null) }

    fun saveSettings() {
        viewModelScope.launch {
            val normalized = normalizeSettings(_uiState.value)
            val validationError = validateForSave(normalized)
            if (validationError != null) {
                _uiState.update { it.copy(saveStatus = validationError) }
                return@launch
            }

            settingsRepository.updateSettings(
                OpenRouterSettings(
                    apiKey = normalized.apiKey,
                    model = normalized.model,
                    provider = normalized.provider,
                    baseUrl = normalized.baseUrl,
                    style = normalized.style,
                    liveTranslateEnabled = normalized.liveTranslateEnabled,
                    showOriginalText = normalized.showOriginalText,
                    captionLimit = normalized.captionLimit,
                    glossary = normalized.glossary
                )
            )
            _uiState.value = normalized.copy(saveStatus = "Saved", testStatus = normalized.testStatus)
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            val normalized = normalizeSettings(_uiState.value)
            val validationError = validateForConnection(normalized)
            if (validationError != null) {
                _uiState.update { it.copy(testStatus = validationError) }
                return@launch
            }

            val result = runCatching {
                val api = openRouterClient.create(normalized.baseUrl, normalized.apiKey)
                api.listModels()
            }
            val status = if (result.isSuccess) {
                "Connection OK"
            } else {
                val message = result.exceptionOrNull()?.message ?: "Unknown error"
                "Connection failed: $message"
            }
            _uiState.value = normalized.copy(testStatus = status, saveStatus = normalized.saveStatus)
        }
    }

    private fun normalizeSettings(state: SettingsUiState): SettingsUiState {
        val normalizedBaseUrl = state.baseUrl.trim().trimEnd('/')
        return state.copy(
            apiKey = state.apiKey.trim(),
            model = state.model.trim(),
            provider = state.provider.trim(),
            baseUrl = if (normalizedBaseUrl.isBlank()) "" else normalizedBaseUrl,
            glossary = state.glossary.lines().map { it.trim() }.filter { it.isNotBlank() }.joinToString("\n"),
            captionLimit = state.captionLimit.coerceIn(50, 500)
        )
    }

    private fun validateForSave(state: SettingsUiState): String? {
        if (state.liveTranslateEnabled && state.apiKey.isBlank()) return "Add an API key before enabling live translate"
        if (state.liveTranslateEnabled && state.model.isBlank()) return "Add a model before enabling live translate"
        if (state.baseUrl.isBlank()) return "Base URL is required"
        if (!state.baseUrl.startsWith("http://") && !state.baseUrl.startsWith("https://")) {
            return "Base URL must start with http:// or https://"
        }
        return null
    }

    private fun validateForConnection(state: SettingsUiState): String? {
        if (state.apiKey.isBlank()) return "Missing API key"
        if (state.model.isBlank()) return "Missing model"
        return validateForSave(state)
    }
}
