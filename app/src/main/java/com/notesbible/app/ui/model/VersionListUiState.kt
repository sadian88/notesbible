package com.notesbible.app.ui.model

import com.notesbible.app.data.DownloadStatus

data class VersionListUiState(
    val versions: List<VersionItemUiState> = emptyList()
)

data class VersionItemUiState(
    val id: String,
    val name: String,
    val language: String,
    val abbreviation: String,
    val description: String,
    val isDownloaded: Boolean,
    val totalVerses: Int,
    val lastDownloadedAt: Long?,
    val downloadStatus: DownloadStatus = DownloadStatus.Idle()
)
