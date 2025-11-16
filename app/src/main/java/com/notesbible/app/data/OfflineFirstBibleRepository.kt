package com.notesbible.app.data

import androidx.room.withTransaction
import com.notesbible.app.data.local.BibleDatabase
import com.notesbible.app.data.local.dao.BibleVersionDao
import com.notesbible.app.data.local.dao.HandwrittenNoteDao
import com.notesbible.app.data.local.dao.VerseDao
import com.notesbible.app.data.local.entities.BibleVersionEntity
import com.notesbible.app.data.local.entities.HandwrittenNoteEntity
import com.notesbible.app.data.local.entities.VerseEntity
import com.notesbible.app.data.models.BibleVersion
import com.notesbible.app.data.models.BibleVersionDefinition
import com.notesbible.app.data.models.BookSummary
import com.notesbible.app.data.models.HandwrittenNote
import com.notesbible.app.data.models.Stroke
import com.notesbible.app.data.models.Verse
import com.notesbible.app.data.remote.BibleApi
import com.notesbible.app.data.remote.BibleJsonParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class OfflineFirstBibleRepository(
    private val bibleApi: BibleApi,
    private val parser: BibleJsonParser,
    private val versionDao: BibleVersionDao,
    private val verseDao: VerseDao,
    private val handwrittenNoteDao: HandwrittenNoteDao,
    private val availableVersions: List<BibleVersionDefinition>,
    private val database: BibleDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BibleRepository {

    private val json = Json { encodeDefaults = true }

    override fun observeAvailableVersions(): Flow<List<BibleVersion>> {
        return versionDao.observeVersions().map { localVersions ->
            val localMap = localVersions.associateBy { it.id }
            availableVersions.map { definition ->
                val local = localMap[definition.id]
                BibleVersion(
                    id = definition.id,
                    name = definition.name,
                    language = definition.language,
                    abbreviation = definition.abbreviation,
                    isDownloaded = local != null,
                    description = definition.description,
                    totalVerses = local?.totalVerses ?: 0,
                    lastDownloadedAt = local?.lastDownloaded
                )
            }
        }
    }

    override fun downloadVersion(versionId: String): Flow<DownloadStatus> {
        val definition = availableVersions.firstOrNull { it.id == versionId }
            ?: throw IllegalArgumentException("Versión no soportada: $versionId")

        return flow {
            emit(DownloadStatus.InProgress(0f))
            val rawJson = bibleApi.downloadVersion(definition.downloadPath)
            emit(DownloadStatus.InProgress(0.45f))
            val parsed = parser.parse(versionId, rawJson)
            emit(DownloadStatus.InProgress(0.75f))
            withContext(ioDispatcher) {
                database.withTransaction {
                    verseDao.deleteByVersion(versionId)
                    val entities = parsed.map {
                        VerseEntity(
                            versionId = it.versionId,
                            book = it.book,
                            bookIndex = it.bookIndex,
                            chapterNumber = it.chapterNumber,
                            verseNumber = it.verseNumber,
                            text = it.text
                        )
                    }
                    entities.chunked(500).forEach { batch ->
                        verseDao.insertVerses(batch)
                    }
                    versionDao.upsert(
                        BibleVersionEntity(
                            id = definition.id,
                            name = definition.name,
                            language = definition.language,
                            abbreviation = definition.abbreviation,
                            description = definition.description,
                            lastDownloaded = System.currentTimeMillis(),
                            totalVerses = entities.size
                        )
                    )
                }
            }
            emit(DownloadStatus.InProgress(1f))
            emit(DownloadStatus.Success(parsed.size))
        }.catch { throwable ->
            emit(DownloadStatus.Error(throwable))
        }
    }

    override fun observeBooks(versionId: String): Flow<List<BookSummary>> {
        return verseDao.observeBooks(versionId).map { projections ->
            projections.map { BookSummary(it.name, it.bookIndex, it.chapterCount) }
        }
    }

    override fun observeChapter(versionId: String, book: String, chapter: Int): Flow<List<Verse>> {
        return verseDao.observeChapter(versionId, book, chapter).map { entities ->
            entities.map { Verse(it.verseNumber, it.text) }
        }
    }

    override fun observeHandwrittenNote(
        versionId: String,
        book: String,
        chapter: Int,
        verse: Int
    ): Flow<HandwrittenNote?> {
        return handwrittenNoteDao.observeNote(versionId, book, chapter, verse).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun saveHandwrittenNote(note: HandwrittenNote) {
        withContext(ioDispatcher) {
            handwrittenNoteDao.upsert(note.toEntity())
        }
    }

    private fun HandwrittenNoteEntity.toDomain(): HandwrittenNote {
        val strokesList: List<Stroke> = if (strokes.isEmpty()) {
            emptyList()
        } else {
            json.decodeFromString(strokes)
        }
        return HandwrittenNote(
            versionId = versionId,
            book = book,
            chapter = chapter,
            verse = verse,
            strokes = strokesList,
            updatedAt = updatedAt
        )
    }

    private fun HandwrittenNote.toEntity(): HandwrittenNoteEntity {
        return HandwrittenNoteEntity(
            versionId = versionId,
            book = book,
            chapter = chapter,
            verse = verse,
            strokes = json.encodeToString(strokes),
            updatedAt = System.currentTimeMillis()
        )
    }
}
