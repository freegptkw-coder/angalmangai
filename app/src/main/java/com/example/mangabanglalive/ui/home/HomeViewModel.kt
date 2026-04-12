package com.example.mangabanglalive.ui.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangabanglalive.data.importer.ImportManager
import com.example.mangabanglalive.domain.model.MangaProject
import com.example.mangabanglalive.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val recentProjects: List<MangaProject> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastOpenedProjectId: String? = null
)

class HomeViewModel(private val projectRepository: ProjectRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            projectRepository.observeRecentProjects().collect { projects ->
                _uiState.update { it.copy(recentProjects = projects) }
            }
        }
    }

    fun importImage(context: Context, uri: Uri) = importWith(context) { it.importImage(uri) }

    fun importFolder(context: Context, uri: Uri) = importWith(context) { it.importFolder(uri) }

    fun importArchive(context: Context, uri: Uri) = importWith(context) { it.importArchive(uri) }

    fun importPdf(context: Context, uri: Uri) = importWith(context) { it.importPdf(uri) }

    fun clearNavigation() {
        _uiState.update { it.copy(lastOpenedProjectId = null) }
    }

    private fun importWith(context: Context, action: suspend (ImportManager) -> MangaProject) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val importer = ImportManager(context, projectRepository)
                action(importer)
            }.onSuccess { project ->
                _uiState.update { it.copy(isLoading = false, lastOpenedProjectId = project.id) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message ?: "Import failed") }
            }
        }
    }
}