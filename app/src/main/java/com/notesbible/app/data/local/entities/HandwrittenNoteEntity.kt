package com.notesbible.app.data.local.entities

import androidx.room.Entity

@Entity(
    tableName = "handwritten_notes",
    primaryKeys = ["versionId", "book", "chapter", "verse"]
)
data class HandwrittenNoteEntity(
    val versionId: String,
    val book: String,
    val chapter: Int,
    val verse: Int = 0,
    val strokes: String,
    val updatedAt: Long
)
