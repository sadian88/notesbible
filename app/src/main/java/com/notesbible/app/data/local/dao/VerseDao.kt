package com.notesbible.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.notesbible.app.data.local.entities.VerseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VerseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<VerseEntity>)

    @Query("DELETE FROM verses WHERE versionId = :versionId")
    suspend fun deleteByVersion(versionId: String)

    @Query(
        """
        SELECT book AS name, bookIndex AS bookIndex, MAX(chapterNumber) AS chapterCount
        FROM verses
        WHERE versionId = :versionId
        GROUP BY book, bookIndex
        ORDER BY bookIndex
        """
    )
    fun observeBooks(versionId: String): Flow<List<BookProjection>>

    @Query(
        """
        SELECT * FROM verses
        WHERE versionId = :versionId AND book = :book AND chapterNumber = :chapter
        ORDER BY verseNumber
        """
    )
    fun observeChapter(versionId: String, book: String, chapter: Int): Flow<List<VerseEntity>>
}

data class BookProjection(
    val name: String,
    val bookIndex: Int,
    val chapterCount: Int
)
