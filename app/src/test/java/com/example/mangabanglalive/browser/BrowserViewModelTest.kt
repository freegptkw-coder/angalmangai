package com.example.mangabanglalive.browser

import com.example.mangabanglalive.domain.model.OpenRouterSettings
import com.example.mangabanglalive.domain.repository.SettingsRepository
import com.example.mangabanglalive.overlay.CaptionLine
import com.example.mangabanglalive.translation.TranslationEngine
import com.example.mangabanglalive.domain.model.OcrSegment
import com.example.mangabanglalive.domain.model.TranslationSegment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BrowserViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadUrl_movesDraftUrlToActiveUrl() = runTest(dispatcher) {
        val viewModel = BrowserViewModel(FakeSettingsRepository(), FakeTranslationEngine())

        viewModel.updateUrl("example.com")
        viewModel.loadUrl()
        advanceUntilIdle()

        assertEquals("https://example.com", viewModel.uiState.value.activeUrl)
    }

    @Test
    fun togglingLiveTranslateOff_clearsCaptionsAndTranslationMap() = runTest(dispatcher) {
        val repository = FakeSettingsRepository(
            OpenRouterSettings(liveTranslateEnabled = true, model = "m", apiKey = "k")
        )
        val viewModel = BrowserViewModel(repository, FakeTranslationEngine())
        advanceUntilIdle()

        viewModel.submitExtractedTexts(listOf("hello"))
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.captions.isNotEmpty())

        viewModel.toggleLiveTranslate()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.liveTranslateEnabled)
        assertTrue(viewModel.uiState.value.captions.isEmpty())
        assertTrue(viewModel.translationMap.value.isEmpty())
    }

    private class FakeSettingsRepository(initial: OpenRouterSettings = OpenRouterSettings()) : SettingsRepository {
        private val state = MutableStateFlow(initial)
        override val settingsFlow: Flow<OpenRouterSettings> = state

        override suspend fun updateSettings(settings: OpenRouterSettings) {
            state.value = settings
        }
    }

    private class FakeTranslationEngine : TranslationEngine {
        override suspend fun translate(segments: List<OcrSegment>): List<TranslationSegment> = emptyList()

        override suspend fun translateTextBatch(texts: List<String>): List<String> {
            return texts.map { "bn:$it" }
        }
    }
}
