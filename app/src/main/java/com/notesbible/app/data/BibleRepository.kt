package com.notesbible.app.data

import com.notesbible.app.data.models.BibleVersion
import com.notesbible.app.data.models.BookSummary
import com.notesbible.app.data.models.HandwrittenNote
import com.notesbible.app.data.models.Verse
import kotlinx.coroutines.flow.Flow

interface BibleRepository {
    fun observeAvailableVersions(): Flow<List<BibleVersion>>
    fun downloadVersion(versionId: String): Flow<DownloadStatus>
    fun observeBooks(versionId: String): Flow<List<BookSummary>>
    fun observeChapter(versionId: String, book: String, chapter: Int): Flow<List<Verse>>
    fun observeHandwrittenNote(
        versionId: String,
        book: String,
        chapter: Int,
        verse: Int = 0
    ): Flow<HandwrittenNote?>

    suspend fun saveHandwrittenNote(note: HandwrittenNote)
}

sealed interface DownloadStatus {
    data class Idle(val message: String = "") : DownloadStatus
    data class InProgress(val progress: Float) : DownloadStatus
    data class Success(val versesInserted: Int) : DownloadStatus
    data class Error(val throwable: Throwable) : DownloadStatus
}
