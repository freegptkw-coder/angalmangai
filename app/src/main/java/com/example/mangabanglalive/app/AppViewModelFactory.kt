package com.example.mangabanglalive.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mangabanglalive.browser.BrowserViewModel
import com.example.mangabanglalive.reader.ReaderViewModel
import com.example.mangabanglalive.settings.SettingsViewModel
import com.example.mangabanglalive.ui.home.HomeViewModel
import com.example.mangabanglalive.ui.projects.ProjectsViewModel

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(container.projectRepository)
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(container.settingsRepository, container.openRouterClient)
            }
            modelClass.isAssignableFrom(ReaderViewModel::class.java) -> {
                ReaderViewModel(
                    projectRepository = container.projectRepository,
                    ocrRepository = container.ocrRepository,
                    translationRepository = container.translationRepository,
                    ocrEngine = container.ocrEngine,
                    translationEngine = container.translationEngine
                )
            }
            modelClass.isAssignableFrom(ProjectsViewModel::class.java) -> {
                ProjectsViewModel(container.projectRepository)
            }
            modelClass.isAssignableFrom(BrowserViewModel::class.java) -> {
                BrowserViewModel()
            }
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        } as T
    }
}