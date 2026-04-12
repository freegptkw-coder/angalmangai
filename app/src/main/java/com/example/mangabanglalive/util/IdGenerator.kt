package com.example.mangabanglalive.util

import java.util.UUID

object IdGenerator {
    fun newId(): String = UUID.randomUUID().toString()
}