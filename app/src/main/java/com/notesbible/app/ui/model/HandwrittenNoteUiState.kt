package com.notesbible.app.ui.model

import com.notesbible.app.data.models.Stroke

data class HandwrittenNoteUiState(
    val versionId: String,
    val book: String,
    val chapter: Int,
    val verse: Int = 0,
    val strokes: List<Stroke> = emptyList(),
    val isSaving: Boolean = false
) {
    val reference: String
        get() = "$book $chapter"
}
