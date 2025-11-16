package com.notesbible.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.notesbible.app.data.local.dao.BibleVersionDao
import com.notesbible.app.data.local.dao.VerseDao
import com.notesbible.app.data.local.entities.BibleVersionEntity
import com.notesbible.app.data.local.entities.VerseEntity

@Database(
    entities = [BibleVersionEntity::class, VerseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BibleDatabase : RoomDatabase() {
    abstract fun bibleVersionDao(): BibleVersionDao
    abstract fun verseDao(): VerseDao
}
