package com.example.mangabanglalive.browser

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BrowserViewModel : ViewModel() {
    private val _url = MutableStateFlow("https://")
    val url: StateFlow<String> = _url

    fun updateUrl(value: String) {
        val trimmed = value.trim()
        _url.value = when {
            trimmed.isBlank() -> ""
            "://" in trimmed -> trimmed
            else -> "https://$trimmed"
        }
    }
}