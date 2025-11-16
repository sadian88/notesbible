package com.notesbible.app.ui.version

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notesbible.app.data.BibleRepository
import com.notesbible.app.data.DownloadStatus
import com.notesbible.app.ui.model.VersionItemUiState
import com.notesbible.app.ui.model.VersionListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VersionListViewModel(
    private val repository: BibleRepository
) : ViewModel() {

    private val downloads = MutableStateFlow<Map<String, DownloadStatus>>(emptyMap())

    private val _uiState = MutableStateFlow(VersionListUiState())
    val uiState: StateFlow<VersionListUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.observeAvailableVersions()
                .combine(downloads) { versions, downloadMap ->
                    versions.map { version ->
                        VersionItemUiState(
                            id = version.id,
                            name = version.name,
                            language = version.language,
                            abbreviation = version.abbreviation,
                            description = version.description,
                            isDownloaded = version.isDownloaded,
                            totalVerses = version.totalVerses,
                            lastDownloadedAt = version.lastDownloadedAt,
                            downloadStatus = downloadMap[version.id] ?: DownloadStatus.Idle()
                        )
                    }
                }
                .collect { mapped ->
                    _uiState.value = VersionListUiState(mapped)
                }
        }
    }

    fun download(versionId: String) {
        val currentStatus = downloads.value[versionId]
        if (currentStatus is DownloadStatus.InProgress) return
        viewModelScope.launch {
            repository.downloadVersion(versionId).collect { status ->
                downloads.update { current -> current + (versionId to status) }
            }
        }
    }

    companion object {
        fun provideFactory(repository: BibleRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return VersionListViewModel(repository) as T
                }
            }
    }
}
