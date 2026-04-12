package com.example.mangabanglalive.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProject(projectId: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: ProjectEntity)

    @Update
    suspend fun update(project: ProjectEntity)
}

@Dao
interface PageDao {
    @Query("SELECT * FROM pages WHERE projectId = :projectId ORDER BY pageIndex ASC")
    suspend fun getPages(projectId: String): List<PageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pages: List<PageEntity>)
}

@Dao
interface OcrSegmentDao {
    @Query("SELECT * FROM ocr_segments WHERE pageId = :pageId")
    fun observeSegments(pageId: String): Flow<List<OcrSegmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(segments: List<OcrSegmentEntity>)
}

@Dao
interface TranslationSegmentDao {
    @Query("SELECT * FROM translation_segments WHERE pageId = :pageId")
    fun observeSegments(pageId: String): Flow<List<TranslationSegmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(segments: List<TranslationSegmentEntity>)
}