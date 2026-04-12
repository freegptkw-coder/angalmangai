package com.example.mangabanglalive.ui.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangabanglalive.domain.model.MangaProject
import com.example.mangabanglalive.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProjectsUiState(
    val projects: List<MangaProject> = emptyList()
)

class ProjectsViewModel(private val projectRepository: ProjectRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState

    init {
        viewModelScope.launch {
            projectRepository.observeRecentProjects(50).collect { projects ->
                _uiState.update { it.copy(projects = projects) }
            }
        }
    }
}