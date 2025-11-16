package com.notesbible.app.data.models

data class BibleVersion(
    val id: String,
    val name: String,
    val language: String,
    val abbreviation: String,
    val isDownloaded: Boolean,
    val description: String,
    val totalVerses: Int,
    val lastDownloadedAt: Long?
)

data class BookSummary(
    val name: String,
    val bookIndex: Int,
    val chapterCount: Int
)

data class Verse(
    val verseNumber: Int,
    val text: String
)
