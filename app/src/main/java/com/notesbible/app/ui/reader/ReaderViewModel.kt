package com.notesbible.app.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notesbible.app.data.BibleRepository
import com.notesbible.app.ui.model.ReaderUiState
import com.notesbible.app.ui.model.VerseUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReaderViewModel(
    private val repository: BibleRepository,
    private val versionId: String
) : ViewModel() {

    private val selectedBook = MutableStateFlow<String?>(null)
    private val selectedChapter = MutableStateFlow(1)

    private val booksState = repository.observeBooks(versionId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val versesFlow = combine(selectedBook, selectedChapter) { book, chapter ->
        book to chapter
    }.flatMapLatest { (book, chapter) ->
        if (book == null) {
            flowOf(emptyList())
        } else {
            repository.observeChapter(versionId, book, chapter)
        }
    }

    val uiState: StateFlow<ReaderUiState> = combine(
        booksState,
        selectedBook,
        selectedChapter,
        versesFlow
    ) { books, bookName, chapter, verses ->
        val currentBook = bookName ?: books.firstOrNull()?.name
        if (bookName == null && currentBook != null) {
            selectedBook.value = currentBook
        }
        ReaderUiState(
            versionId = versionId,
            bookNames = books.map { it.name },
            selectedBook = currentBook,
            chapterCount = books.firstOrNull { it.name == currentBook }?.chapterCount ?: 0,
            selectedChapter = chapter,
            verses = verses.map { VerseUi(it.verseNumber, it.text) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ReaderUiState(versionId = versionId)
    )

    init {
        viewModelScope.launch {
            booksState.collect { books ->
                if (books.isNotEmpty() && selectedBook.value !in books.map { it.name }) {
                    selectedBook.value = books.first().name
                    selectedChapter.value = 1
                }
            }
        }
    }

    fun selectBook(book: String) {
        selectedBook.value = book
        selectedChapter.value = 1
    }

    fun selectChapter(chapter: Int) {
        selectedChapter.value = chapter
    }

    companion object {
        fun provideFactory(
            repository: BibleRepository,
            versionId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReaderViewModel(repository, versionId) as T
            }
        }
    }
}
