package com.notesbible.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.notesbible.app.data.local.entities.BibleVersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleVersionDao {
    @Query("SELECT * FROM bible_versions ORDER BY language, name")
    fun observeVersions(): Flow<List<BibleVersionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BibleVersionEntity)

    @Query("SELECT * FROM bible_versions WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): BibleVersionEntity?

    @Query("DELETE FROM bible_versions WHERE id = :id")
    suspend fun deleteById(id: String)
}
