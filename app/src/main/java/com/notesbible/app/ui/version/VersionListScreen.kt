package com.notesbible.app.ui.version

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notesbible.app.data.BibleRepository
import com.notesbible.app.data.DownloadStatus
import com.notesbible.app.ui.model.VersionItemUiState
import androidx.compose.ui.res.stringResource
import com.notesbible.app.R

@Composable
fun VersionListRoute(
    repository: BibleRepository,
    onReadVersion: (String) -> Unit
) {
    val viewModel: VersionListViewModel = viewModel(
        factory = VersionListViewModel.provideFactory(repository)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    VersionListScreen(
        versions = uiState.versions,
        onDownload = viewModel::download,
        onRead = onReadVersion
    )
}

@Composable
fun VersionListScreen(
    versions: List<VersionItemUiState>,
    onDownload: (String) -> Unit,
    onRead: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(id = R.string.versions_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.versions_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(versions, key = { it.id }) { version ->
            VersionCard(
                version = version,
                onDownload = { onDownload(version.id) },
                onRead = { onRead(version.id) }
            )
        }
    }
}

@Composable
private fun VersionCard(
    version: VersionItemUiState,
    onDownload: () -> Unit,
    onRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${version.name} (${version.abbreviation})", style = MaterialTheme.typography.titleMedium)
            Text(
                text = version.language,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = version.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            if (version.isDownloaded) {
                Text(
                    text = stringResource(id = R.string.local_verses, version.totalVerses),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (version.downloadStatus) {
                    is DownloadStatus.InProgress -> {
                        LinearProgressIndicator(
                            progress = { version.downloadStatus.progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                        )
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                    is DownloadStatus.Error -> {
                        Text(
                            text = stringResource(id = R.string.download_error),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(onClick = onDownload) {
                            Text(text = stringResource(id = R.string.retry))
                        }
                    }
                    is DownloadStatus.Success -> {
                        Button(onClick = onRead, modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(id = R.string.read))
                        }
                    }
                    else -> {
                        if (version.isDownloaded) {
                            Button(onClick = onRead, modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(id = R.string.read))
                            }
                        } else {
                            Button(onClick = onDownload, modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(id = R.string.download))
                            }
                        }
                    }
                }
            }
        }
    }
}
