package com.example.mangabanglalive.browser

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BrowserViewModel : ViewModel() {
    private val _url = MutableStateFlow("https://")
    val url: StateFlow<String> = _url

    fun updateUrl(value: String) {
        _url.value = value
    }
}