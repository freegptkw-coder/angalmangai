package com.example.mangabanglalive.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangabanglalive.domain.model.OpenRouterSettings
import com.example.mangabanglalive.domain.repository.SettingsRepository
import com.example.mangabanglalive.overlay.CaptionLine
import com.example.mangabanglalive.translation.TranslationEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class WebViewMode { OVERLAY, REPLACE }

data class BrowserUiState(
    val url: String = "https://",
    val activeUrl: String = "",
    val captions: List<CaptionLine> = emptyList(),
    val liveTranslateEnabled: Boolean = false,
    val showOriginalText: Boolean = false,
    val captionLimit: Int = 500,
    val mode: WebViewMode = WebViewMode.OVERLAY,
    val isPaused: Boolean = false
)

class BrowserViewModel(
    private val settingsRepository: SettingsRepository,
    private val translationEngine: TranslationEngine
) : ViewModel() {
    private val _uiState = MutableStateFlow(BrowserUiState())
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    private val _translationMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val translationMap: StateFlow<Map<String, String>> = _translationMap.asStateFlow()

    private val pendingTexts = LinkedHashSet<String>()
    private val inputSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private var latestSettings: OpenRouterSettings? = null
    private var translationSessionId = 0

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                latestSettings = settings
                _uiState.update {
                    it.copy(
                        liveTranslateEnabled = settings.liveTranslateEnabled,
                        showOriginalText = settings.showOriginalText,
                        captionLimit = settings.captionLimit
                    )
                }
            }
        }

        viewModelScope.launch {
            inputSignal.collectLatest {
                delay(700)
                flushPending()
            }
        }
    }

    fun updateUrl(value: String) {
        val trimmed = value.trim()
        _uiState.update {
            it.copy(
                url = when {
                    trimmed.isBlank() -> ""
                    "://" in trimmed -> trimmed
                    else -> "https://$trimmed"
                }
            )
        }
    }

    fun updateMode(mode: WebViewMode) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun loadUrl() {
        val nextUrl = _uiState.value.url
        if (nextUrl.startsWith("http") && nextUrl.length > 8) {
            _uiState.update { it.copy(activeUrl = nextUrl) }
        }
    }

    fun togglePause() {
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun toggleShowOriginal() {
        val next = !_uiState.value.showOriginalText
        _uiState.update { it.copy(showOriginalText = next) }
        persistSettings { it.copy(showOriginalText = next) }
    }

    fun toggleLiveTranslate() {
        val next = !_uiState.value.liveTranslateEnabled
        _uiState.update { it.copy(liveTranslateEnabled = next) }
        if (!next) {
            resetTranslations(resetPause = true)
        }
        persistSettings { it.copy(liveTranslateEnabled = next) }
    }

    fun clearCaptions() {
        resetTranslations(resetPause = false)
    }

    fun submitExtractedTexts(texts: List<String>) {
        if (texts.isEmpty()) return
        val state = _uiState.value
        if (!state.liveTranslateEnabled || state.isPaused) return

        var added = false
        texts.asSequence()
            .map { it.trim() }
            .filter { it.length > 1 }
            .forEach { text ->
                if (pendingTexts.add(text)) {
                    added = true
                }
            }
        if (added) {
            inputSignal.tryEmit(Unit)
        }
    }

    private suspend fun flushPending() {
        val sessionId = translationSessionId
        val state = _uiState.value
        if (!state.liveTranslateEnabled || state.isPaused) {
            pendingTexts.clear()
            return
        }
        if (pendingTexts.isEmpty()) return
        val batch = pendingTexts.toList()
        pendingTexts.clear()

        val translated = translationEngine.translateTextBatch(batch)
        val latestState = _uiState.value
        if (sessionId != translationSessionId || !latestState.liveTranslateEnabled || latestState.isPaused) {
            return
        }
        val newLines = batch.mapIndexed { index, original ->
            CaptionLine(original = original, translated = translated.getOrNull(index) ?: original)
        }

        _uiState.update { current ->
            val merged = (current.captions + newLines).takeLast(current.captionLimit)
            current.copy(captions = merged)
        }

        val updatedMap = _translationMap.value.toMutableMap()
        newLines.forEach { line ->
            updatedMap[line.original] = line.translated
        }
        _translationMap.value = updatedMap
    }

    private fun resetTranslations(resetPause: Boolean) {
        translationSessionId += 1
        pendingTexts.clear()
        _translationMap.value = emptyMap()
        _uiState.update {
            it.copy(
                captions = emptyList(),
                isPaused = if (resetPause) false else it.isPaused
            )
        }
    }

    private fun persistSettings(update: (OpenRouterSettings) -> OpenRouterSettings) {
        val current = latestSettings ?: return
        viewModelScope.launch {
            settingsRepository.updateSettings(update(current))
        }
    }
}
