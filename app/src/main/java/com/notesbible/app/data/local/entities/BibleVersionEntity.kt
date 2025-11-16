package com.notesbible.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bible_versions")
data class BibleVersionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val language: String,
    val abbreviation: String,
    val description: String,
    val lastDownloaded: Long,
    val totalVerses: Int
)
