package com.notesbible.app.data.local.entities

import androidx.room.Entity

@Entity(
    tableName = "verses",
    primaryKeys = ["versionId", "bookIndex", "chapterNumber", "verseNumber"]
)
data class VerseEntity(
    val versionId: String,
    val book: String,
    val bookIndex: Int,
    val chapterNumber: Int,
    val verseNumber: Int,
    val text: String
)
