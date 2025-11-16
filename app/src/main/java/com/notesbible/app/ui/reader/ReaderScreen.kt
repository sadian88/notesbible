package com.notesbible.app.ui.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.TextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.menuAnchor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import com.notesbible.app.R
import com.notesbible.app.data.BibleRepository
import com.notesbible.app.ui.model.ReaderUiState
import com.notesbible.app.ui.model.VerseUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderRoute(
    repository: BibleRepository,
    versionId: String,
    onBack: () -> Unit,
    onOpenNotes: (String, Int) -> Unit
) {
    val viewModel: ReaderViewModel = viewModel(
        factory = ReaderViewModel.provideFactory(repository, versionId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ReaderScreen(
        uiState = uiState,
        onBack = onBack,
        onBookSelected = viewModel::selectBook,
        onChapterSelected = viewModel::selectChapter,
        onAddNote = onOpenNotes
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    uiState: ReaderUiState,
    onBack: () -> Unit,
    onBookSelected: (String) -> Unit,
    onChapterSelected: (Int) -> Unit,
    onAddNote: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.reader_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            val currentBook = uiState.selectedBook
            if (currentBook != null) {
                FloatingActionButton(onClick = { onAddNote(currentBook, uiState.selectedChapter) }) {
                    Icon(
                        imageVector = Icons.Outlined.Create,
                        contentDescription = stringResource(R.string.notes_fab)
                    )
                }
            }
        }
    ) { innerPadding ->
        if (!uiState.hasContent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.reading_empty))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                BookSelector(
                    books = uiState.bookNames,
                    selected = uiState.selectedBook,
                    onSelected = onBookSelected
                )
                Spacer(modifier = Modifier.height(12.dp))
                ChapterSelector(
                    total = uiState.chapterCount,
                    selected = uiState.selectedChapter,
                    onSelected = onChapterSelected
                )
                Spacer(modifier = Modifier.height(16.dp))
                VerseList(
                    verses = uiState.verses,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookSelector(
    books: List<String>,
    selected: String?,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(text = stringResource(id = R.string.book_label), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selected ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(id = R.string.select_book)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                books.forEach { book ->
                    DropdownMenuItem(
                        text = { Text(book) },
                        onClick = {
                            expanded = false
                            onSelected(book)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterSelector(
    total: Int,
    selected: Int,
    onSelected: (Int) -> Unit
) {
    if (total <= 0) return
    Column {
        Text(text = stringResource(id = R.string.chapter_label), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(total) { index ->
                val chapter = index + 1
                AssistChip(
                    onClick = { onSelected(chapter) },
                    label = { Text(chapter.toString()) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (chapter == selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        labelColor = if (chapter == selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@Composable
private fun VerseList(
    verses: List<VerseUi>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(verses) { _, verse ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = verse.number.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(end = 12.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = verse.text,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
