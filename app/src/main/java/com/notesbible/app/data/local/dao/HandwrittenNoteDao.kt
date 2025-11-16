package com.notesbible.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.notesbible.app.data.local.entities.HandwrittenNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HandwrittenNoteDao {
    @Query(
        "SELECT * FROM handwritten_notes WHERE versionId = :versionId AND book = :book AND chapter = :chapter AND verse = :verse LIMIT 1"
    )
    fun observeNote(
        versionId: String,
        book: String,
        chapter: Int,
        verse: Int = 0
    ): Flow<HandwrittenNoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: HandwrittenNoteEntity)
}
