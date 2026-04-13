package com.example.mangabanglalive.accessibility

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.accessibilityservice.AccessibilityService
import com.example.mangabanglalive.domain.model.OpenRouterSettings
import com.example.mangabanglalive.overlay.CaptionLine
import com.example.mangabanglalive.overlay.CaptionOverlayController
import com.example.mangabanglalive.domain.repository.SettingsRepository
import com.example.mangabanglalive.storage.SettingsRepositoryImpl
import com.example.mangabanglalive.translation.OpenRouterClient
import com.example.mangabanglalive.translation.OpenRouterTranslationEngine
import com.example.mangabanglalive.translation.TranslationEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TranslationAccessibilityService : AccessibilityService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var translationEngine: TranslationEngine
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var overlayController: CaptionOverlayController

    private val pendingTexts = LinkedHashSet<String>()
    private val inputSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private var latestSettings: OpenRouterSettings? = null
    private var captions: List<CaptionLine> = emptyList()
    private var isPaused = false
    private var translationSessionId = 0

    override fun onServiceConnected() {
        super.onServiceConnected()
        settingsRepository = SettingsRepositoryImpl(this)
        translationEngine = OpenRouterTranslationEngine(settingsRepository, OpenRouterClient())
        overlayController = CaptionOverlayController(this)

        scope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                latestSettings = settings
                if (settings.liveTranslateEnabled) {
                    overlayController.show()
                } else {
                    overlayController.hide()
                    resetTranslations(resetPause = true)
                }
                updateOverlay(settings)
            }
        }

        scope.launch {
            inputSignal.collectLatest {
                delay(700)
                flushPending()
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val settings = latestSettings ?: return
        if (!settings.liveTranslateEnabled || isPaused) return
        if (event == null) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg == packageName) return

        val root = rootInActiveWindow ?: return
        val texts = collectTexts(root)
        if (texts.isEmpty()) return
        var added = false
        texts.forEach { text ->
            if (pendingTexts.add(text)) {
                added = true
            }
        }
        if (added) {
            inputSignal.tryEmit(Unit)
        }
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        overlayController.hide()
        scope.cancel()
        super.onDestroy()
    }

    private suspend fun flushPending() {
        val sessionId = translationSessionId
        val settings = latestSettings ?: return
        if (!settings.liveTranslateEnabled || isPaused) {
            pendingTexts.clear()
            return
        }
        if (pendingTexts.isEmpty()) return
        val batch = pendingTexts.toList()
        pendingTexts.clear()

        val translated = withContext(Dispatchers.IO) {
            translationEngine.translateTextBatch(batch)
        }
        val latestSettings = latestSettings
        if (sessionId != translationSessionId || latestSettings == null || !latestSettings.liveTranslateEnabled || isPaused) {
            return
        }

        val newLines = batch.mapIndexed { index, original ->
            CaptionLine(original = original, translated = translated.getOrNull(index) ?: original)
        }
        captions = (captions + newLines).takeLast(latestSettings.captionLimit)
        updateOverlay(latestSettings)
    }

    private fun updateOverlay(settings: OpenRouterSettings) {
        if (!settings.liveTranslateEnabled) return
        overlayController.update(
            captions = captions,
            showOriginal = settings.showOriginalText,
            isPaused = isPaused,
            onToggleShowOriginal = { toggleShowOriginal(settings) },
            onTogglePause = { togglePause() },
            onClear = { clearCaptions() }
        )
    }

    private fun toggleShowOriginal(settings: OpenRouterSettings) {
        val updated = settings.copy(showOriginalText = !settings.showOriginalText)
        scope.launch { settingsRepository.updateSettings(updated) }
    }

    private fun togglePause() {
        isPaused = !isPaused
        latestSettings?.let { updateOverlay(it) }
    }

    private fun clearCaptions() {
        resetTranslations(resetPause = false)
        latestSettings?.let { updateOverlay(it) }
    }

    private fun resetTranslations(resetPause: Boolean) {
        translationSessionId += 1
        pendingTexts.clear()
        captions = emptyList()
        if (resetPause) {
            isPaused = false
        }
    }

    private fun collectTexts(root: AccessibilityNodeInfo): List<String> {
        val results = ArrayList<String>(64)
        fun walk(node: AccessibilityNodeInfo?) {
            if (node == null) return
            val text = node.text?.toString()?.trim().orEmpty()
            if (text.length > 1) {
                results.add(text)
                if (results.size >= 200) return
            }
            val desc = node.contentDescription?.toString()?.trim().orEmpty()
            if (desc.length > 1) {
                results.add(desc)
                if (results.size >= 200) return
            }
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                walk(child)
                child?.recycle()
                if (results.size >= 200) return
            }
        }
        walk(root)
        return results
    }
}
