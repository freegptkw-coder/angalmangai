package com.example.mangabanglalive.app

import android.content.Context
import androidx.room.Room
import com.example.mangabanglalive.data.local.MangaDatabase
import com.example.mangabanglalive.data.repository.OcrRepositoryImpl
import com.example.mangabanglalive.data.repository.ProjectRepositoryImpl
import com.example.mangabanglalive.data.repository.TranslationRepositoryImpl
import com.example.mangabanglalive.domain.repository.OcrRepository
import com.example.mangabanglalive.domain.repository.ProjectRepository
import com.example.mangabanglalive.domain.repository.SettingsRepository
import com.example.mangabanglalive.domain.repository.TranslationRepository
import com.example.mangabanglalive.ocr.MlKitOcrEngine
import com.example.mangabanglalive.ocr.OcrEngine
import com.example.mangabanglalive.storage.SettingsRepositoryImpl
import com.example.mangabanglalive.translation.OpenRouterClient
import com.example.mangabanglalive.translation.OpenRouterTranslationEngine
import com.example.mangabanglalive.translation.TranslationEngine

class AppContainer(context: Context) {
    private val database: MangaDatabase = Room.databaseBuilder(
        context,
        MangaDatabase::class.java,
        "manga_bangla_live.db"
    ).build()

    val settingsRepository: SettingsRepository = SettingsRepositoryImpl(context)
    val projectRepository: ProjectRepository = ProjectRepositoryImpl(database)
    val ocrRepository: OcrRepository = OcrRepositoryImpl(database)
    val translationRepository: TranslationRepository = TranslationRepositoryImpl(database)

    val ocrEngine: OcrEngine = MlKitOcrEngine()
    val openRouterClient = OpenRouterClient()
    val translationEngine: TranslationEngine = OpenRouterTranslationEngine(
        settingsRepository = settingsRepository,
        client = openRouterClient
    )
}