package com.example.mangabanglalive.browser

import android.graphics.Color
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mangabanglalive.R
import com.example.mangabanglalive.overlay.CaptionOverlay
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(viewModel: BrowserViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val translationMap by viewModel.translationMap.collectAsState()
    var webView by remember { mutableStateOf<WebView?>(null) }
    var lastLoadedUrl by remember { mutableStateOf<String?>(null) }

    val bridge = remember {
        BrowserJsBridge { json ->
            val texts = parseJsonArray(json)
            viewModel.submitExtractedTexts(texts)
        }
    }

    LaunchedEffect(webView, uiState.liveTranslateEnabled, uiState.isPaused) {
        if (webView == null || !uiState.liveTranslateEnabled || uiState.isPaused) return@LaunchedEffect
        while (true) {
            webView?.evaluateJavascript(EXTRACT_TEXT_JS, null)
            delay(1200)
        }
    }

    LaunchedEffect(webView, uiState.mode, translationMap) {
        if (webView == null) return@LaunchedEffect
        if (uiState.mode == WebViewMode.REPLACE && translationMap.isNotEmpty()) {
            val script = buildReplaceJs(translationMap)
            webView?.evaluateJavascript(script, null)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.height(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Browser")
                    }
                },
                navigationIcon = {
                    androidx.compose.material3.TextButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = uiState.url,
                onValueChange = viewModel::updateUrl,
                label = { Text("URL") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(onClick = viewModel::loadUrl) { Text("Go") }
                if (uiState.liveTranslateEnabled) {
                    FilledTonalButton(onClick = viewModel::toggleLiveTranslate) { Text("Live: On") }
                } else {
                    OutlinedButton(onClick = viewModel::toggleLiveTranslate) { Text("Live: Off") }
                }
                if (uiState.mode == WebViewMode.OVERLAY) {
                    FilledTonalButton(onClick = { viewModel.updateMode(WebViewMode.OVERLAY) }) { Text("Overlay") }
                } else {
                    OutlinedButton(onClick = { viewModel.updateMode(WebViewMode.OVERLAY) }) { Text("Overlay") }
                }
                if (uiState.mode == WebViewMode.REPLACE) {
                    FilledTonalButton(onClick = { viewModel.updateMode(WebViewMode.REPLACE) }) { Text("Replace") }
                } else {
                    OutlinedButton(onClick = { viewModel.updateMode(WebViewMode.REPLACE) }) { Text("Replace") }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            setBackgroundColor(Color.WHITE)
                            webViewClient = WebViewClient()
                            webChromeClient = WebChromeClient()
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.databaseEnabled = true
                            settings.loadsImagesAutomatically = true
                            settings.useWideViewPort = true
                            settings.loadWithOverviewMode = true
                            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                            settings.userAgentString = settings.userAgentString + " MangaBanglaLive"
                            addJavascriptInterface(bridge, "MblBridge")

                            if (uiState.activeUrl.startsWith("http") && uiState.activeUrl.length > 8) {
                                lastLoadedUrl = uiState.activeUrl
                                loadUrl(uiState.activeUrl)
                            }
                        }.also { webView = it }
                    },
                    update = { view ->
                        if (uiState.activeUrl.startsWith("http") && uiState.activeUrl.length > 8 && uiState.activeUrl != lastLoadedUrl) {
                            lastLoadedUrl = uiState.activeUrl
                            view.loadUrl(uiState.activeUrl)
                        }
                    }
                )

                if (uiState.liveTranslateEnabled && uiState.mode == WebViewMode.OVERLAY) {
                    CaptionOverlay(
                        captions = uiState.captions,
                        showOriginal = uiState.showOriginalText,
                        isPaused = uiState.isPaused,
                        onToggleShowOriginal = viewModel::toggleShowOriginal,
                        onTogglePause = viewModel::togglePause,
                        onClear = viewModel::clearCaptions,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

private class BrowserJsBridge(private val onTexts: (String) -> Unit) {
    @JavascriptInterface
    fun onTexts(json: String) {
        onTexts(json)
    }
}

private fun parseJsonArray(json: String): List<String> {
    return runCatching {
        val array = JSONArray(json)
        List(array.length()) { index -> array.getString(index) }
    }.getOrDefault(emptyList())
}

private fun buildReplaceJs(map: Map<String, String>): String {
    val json = JSONObject(map).toString()
    return """
(function() {
  const map = $json;
  const walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, null, false);
  let node;
  while (node = walker.nextNode()) {
    if (!node.parentElement) continue;
    const tag = node.parentElement.tagName;
    if (tag === 'SCRIPT' || tag === 'STYLE' || tag === 'NOSCRIPT' || tag === 'TEXTAREA' || tag === 'INPUT') continue;
    if (node.parentElement.getAttribute('data-mbl-translated') === '1') continue;
    const original = node.textContent.trim();
    if (!original || original.length < 2) continue;
    const translated = map[original];
    if (translated) {
      node.textContent = translated;
      node.parentElement.setAttribute('data-mbl-translated', '1');
    }
  }
})();
""".trimIndent()
}

private const val EXTRACT_TEXT_JS = """
(function() {
  const texts = [];
  const walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, null, false);
  let node;
  while (node = walker.nextNode()) {
    if (!node.parentElement) continue;
    const tag = node.parentElement.tagName;
    if (tag === 'SCRIPT' || tag === 'STYLE' || tag === 'NOSCRIPT' || tag === 'TEXTAREA' || tag === 'INPUT') continue;
    if (node.parentElement.getAttribute('data-mbl-translated') === '1') continue;
    const text = node.textContent.trim();
    if (text.length > 1) {
      texts.push(text);
      if (texts.length >= 200) break;
    }
  }
  if (window.MblBridge && window.MblBridge.onTexts) {
    window.MblBridge.onTexts(JSON.stringify(texts));
  }
})();
"""
