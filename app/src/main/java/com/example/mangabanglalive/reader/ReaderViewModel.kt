package com.example.mangabanglalive.reader

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangabanglalive.domain.model.MangaPage
import com.example.mangabanglalive.domain.model.MangaProject
import com.example.mangabanglalive.domain.model.TranslationSegment
import com.example.mangabanglalive.domain.repository.OcrRepository
import com.example.mangabanglalive.domain.repository.ProjectRepository
import com.example.mangabanglalive.domain.repository.TranslationRepository
import com.example.mangabanglalive.ocr.OcrEngine
import com.example.mangabanglalive.translation.TranslationEngine
import com.example.mangabanglalive.util.BitmapLoader
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReaderUiState(
    val project: MangaProject? = null,
    val pages: List<MangaPage> = emptyList(),
    val currentPageIndex: Int = 0,
    val translations: List<TranslationSegment> = emptyList(),
    val overlayEnabled: Boolean = true,
    val isBusy: Boolean = false,
    val message: String? = null
)

class ReaderViewModel(
    private val projectRepository: ProjectRepository,
    private val ocrRepository: OcrRepository,
    private val translationRepository: TranslationRepository,
    private val ocrEngine: OcrEngine,
    private val translationEngine: TranslationEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState
    private var translationJob: Job? = null

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            val project = projectRepository.getProject(projectId)
            val pages = projectRepository.getPages(projectId)
            _uiState.update { it.copy(project = project, pages = pages, currentPageIndex = 0) }
            observeTranslations()
        }
    }

    fun updateTranslation(segmentId: String, newText: String) {
        val page = currentPage() ?: return
        val updated = _uiState.value.translations.map { segment ->
            if (segment.id == segmentId) segment.copy(translatedText = newText) else segment
        }
        viewModelScope.launch {
            translationRepository.upsertTranslations(page.id, updated)
        }
    }

    fun toggleOverlay() {
        _uiState.update { it.copy(overlayEnabled = !it.overlayEnabled) }
    }

    fun nextPage() {
        _uiState.update { state ->
            if (state.pages.isEmpty()) return@update state
            val nextIndex = (state.currentPageIndex + 1).coerceAtMost(state.pages.lastIndex)
            state.copy(currentPageIndex = nextIndex)
        }
        observeTranslations()
    }

    fun previousPage() {
        _uiState.update { state ->
            if (state.pages.isEmpty()) return@update state
            val prevIndex = (state.currentPageIndex - 1).coerceAtLeast(0)
            state.copy(currentPageIndex = prevIndex)
        }
        observeTranslations()
    }

    fun runOcrAndTranslate(context: Context) {
        val page = currentPage() ?: return
        val imageUri = page.imageUri ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true, message = null) }
            runCatching {
                val bitmap = BitmapLoader.load(context, Uri.parse(imageUri))
                val ocrSegments = ocrEngine.recognize(bitmap).map { it.copy(pageId = page.id) }
                ocrRepository.upsertSegments(page.id, ocrSegments)
                val translations = translationEngine.translate(ocrSegments).map { it.copy(pageId = page.id) }
                translationRepository.upsertTranslations(page.id, translations)
            }.onSuccess {
                _uiState.update { it.copy(isBusy = false, message = "Translation complete") }
            }.onFailure { error ->
                _uiState.update { it.copy(isBusy = false, message = error.message ?: "Translation failed") }
            }
        }
    }

    private fun observeTranslations() {
        val page = currentPage() ?: return
        translationJob?.cancel()
        translationJob = viewModelScope.launch {
            translationRepository.observeTranslations(page.id).collect { segments ->
                _uiState.update { it.copy(translations = segments) }
            }
        }
    }

    private fun currentPage(): MangaPage? {
        val state = _uiState.value
        return state.pages.getOrNull(state.currentPageIndex)
    }
}