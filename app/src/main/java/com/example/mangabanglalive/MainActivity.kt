package com.example.mangabanglalive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mangabanglalive.app.MangaBanglaLiveApp
import com.example.mangabanglalive.ui.navigation.AppNavHost
import com.example.mangabanglalive.ui.theme.MangaBanglaLiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as MangaBanglaLiveApp).container
        setContent {
            MangaBanglaLiveTheme {
                AppNavHost(appContainer)
            }
        }
    }
}