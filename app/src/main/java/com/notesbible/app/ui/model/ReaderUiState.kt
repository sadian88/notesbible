package com.notesbible.app.ui.model

data class ReaderUiState(
    val versionId: String = "",
    val bookNames: List<String> = emptyList(),
    val selectedBook: String? = null,
    val chapterCount: Int = 0,
    val selectedChapter: Int = 1,
    val verses: List<VerseUi> = emptyList()
) {
    val hasContent: Boolean
        get() = bookNames.isNotEmpty()
}

data class VerseUi(
    val number: Int,
    val text: String
)
