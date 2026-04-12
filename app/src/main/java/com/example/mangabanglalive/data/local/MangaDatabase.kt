package com.example.mangabanglalive.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProjectEntity::class,
        PageEntity::class,
        OcrSegmentEntity::class,
        TranslationSegmentEntity::class
    ],
    version = 1
)
abstract class MangaDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun pageDao(): PageDao
    abstract fun ocrSegmentDao(): OcrSegmentDao
    abstract fun translationSegmentDao(): TranslationSegmentDao
}