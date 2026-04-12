package com.example.mangabanglalive.app

import android.app.Application

class MangaBanglaLiveApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}