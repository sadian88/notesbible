package com.notesbible.app.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notesbible.app.data.BibleRepository
import com.notesbible.app.data.models.HandwrittenNote
import com.notesbible.app.data.models.Stroke
import com.notesbible.app.ui.model.HandwrittenNoteUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HandwrittenNoteViewModel(
    private val repository: BibleRepository,
    private val versionId: String,
    private val book: String,
    private val chapter: Int,
    private val verse: Int
) : ViewModel() {

    private val savingState = MutableStateFlow(false)

    private val noteFlow = repository.observeHandwrittenNote(versionId, book, chapter, verse)

    val uiState: StateFlow<HandwrittenNoteUiState> = combine(
        noteFlow,
        savingState
    ) { note, isSaving ->
        HandwrittenNoteUiState(
            versionId = versionId,
            book = book,
            chapter = chapter,
            verse = verse,
            strokes = note?.strokes ?: emptyList(),
            isSaving = isSaving
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HandwrittenNoteUiState(versionId, book, chapter, verse)
    )

    fun saveStrokes(strokes: List<Stroke>) {
        viewModelScope.launch {
            savingState.value = true
            try {
                repository.saveHandwrittenNote(
                    HandwrittenNote(
                        versionId = versionId,
                        book = book,
                        chapter = chapter,
                        verse = verse,
                        strokes = strokes
                    )
                )
            } finally {
                savingState.value = false
            }
        }
    }

    companion object {
        fun provideFactory(
            repository: BibleRepository,
            versionId: String,
            book: String,
            chapter: Int,
            verse: Int = 0
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HandwrittenNoteViewModel(repository, versionId, book, chapter, verse) as T
                }
            }
        }
    }
}
