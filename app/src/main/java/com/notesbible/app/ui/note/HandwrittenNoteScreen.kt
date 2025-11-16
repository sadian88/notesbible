package com.notesbible.app.ui.note

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.consume
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notesbible.app.R
import com.notesbible.app.data.BibleRepository
import com.notesbible.app.data.models.DrawPoint
import com.notesbible.app.data.models.Stroke
import com.notesbible.app.ui.model.HandwrittenNoteUiState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandwrittenNoteRoute(
    repository: BibleRepository,
    versionId: String,
    book: String,
    chapter: Int,
    verse: Int,
    onBack: () -> Unit
) {
    val viewModel: HandwrittenNoteViewModel = viewModel(
        factory = HandwrittenNoteViewModel.provideFactory(
            repository = repository,
            versionId = versionId,
            book = book,
            chapter = chapter,
            verse = verse
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HandwrittenNoteScreen(
        uiState = uiState,
        onBack = onBack,
        onSave = viewModel::saveStrokes
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandwrittenNoteScreen(
    uiState: HandwrittenNoteUiState,
    onBack: () -> Unit,
    onSave: (List<Stroke>) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = listOf(
        Color(0xFF1A237E),
        Color(0xFFAD1457),
        Color(0xFF2E7D32),
        Color(0xFFF9A825),
        Color(0xFF424242),
        Color(0xFF00838F)
    )
    var selectedColor by remember { mutableStateOf(palette.first()) }
    var strokeWidth by remember { mutableStateOf(8f) }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var currentColor by remember { mutableStateOf(selectedColor) }
    var currentWidth by remember { mutableStateOf(strokeWidth) }

    val strokes = remember { mutableStateListOf<DrawStroke>() }
    LaunchedEffect(uiState.strokes) {
        strokes.clear()
        strokes.addAll(uiState.strokes.map { it.toDrawStroke() })
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = uiState.reference.ifBlank { stringResource(R.string.notes_title) }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        strokes.clear()
                        currentPoints = emptyList()
                    }) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = stringResource(R.string.notes_clear))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.notes_width, strokeWidth.roundToInt()),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Slider(
                        value = strokeWidth,
                        valueRange = 2f..24f,
                        onValueChange = { strokeWidth = it }
                    )
                }
                val canSave = strokes.isNotEmpty() || currentPoints.isNotEmpty() || uiState.strokes.isNotEmpty()
                Button(
                    onClick = {
                        val pendingStroke = currentPoints.takeIf { it.isNotEmpty() }?.let {
                            DrawStroke(color = currentColor, strokeWidth = currentWidth, points = it)
                        }
                        val finalStrokes = buildList {
                            addAll(strokes)
                            pendingStroke?.let { add(it) }
                        }
                        pendingStroke?.let { strokes.add(it) }
                        currentPoints = emptyList()
                        onSave(finalStrokes.map { it.toStroke() })
                    },
                    enabled = canSave
                ) {
                    Icon(imageVector = Icons.Filled.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.notes_save))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.notes_palette),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    palette.forEach { color ->
                        val isSelected = color == selectedColor
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color = color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onBackground else color.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }
            }
            Text(
                text = stringResource(R.string.notes_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .pointerInput(selectedColor, strokeWidth) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentColor = selectedColor
                                currentWidth = strokeWidth
                                currentPoints = listOf(offset)
                            },
                            onDragCancel = {
                                currentPoints = emptyList()
                            },
                            onDragEnd = {
                                if (currentPoints.isNotEmpty()) {
                                    strokes.add(
                                        DrawStroke(
                                            color = currentColor,
                                            strokeWidth = currentWidth,
                                            points = currentPoints
                                        )
                                    )
                                }
                                currentPoints = emptyList()
                            }
                        ) { change, _ ->
                            currentPoints = currentPoints + change.position
                            change.consume()
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    strokes.forEach { stroke ->
                        drawPoints(
                            points = stroke.points,
                            pointMode = PointMode.Polygon,
                            color = stroke.color,
                            strokeWidth = stroke.strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                    if (currentPoints.isNotEmpty()) {
                        drawPoints(
                            points = currentPoints,
                            pointMode = PointMode.Polygon,
                            color = currentColor,
                            strokeWidth = currentWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
                if (strokes.isEmpty() && currentPoints.isEmpty()) {
                    Text(
                        text = stringResource(R.string.notes_empty_hint),
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private data class DrawStroke(
    val color: Color,
    val strokeWidth: Float,
    val points: List<Offset>
)

private fun DrawStroke.toStroke(): Stroke {
    return Stroke(
        color = color.value.toLong(),
        strokeWidth = strokeWidth,
        points = points.map { DrawPoint(it.x, it.y) }
    )
}

private fun Stroke.toDrawStroke(): DrawStroke {
    return DrawStroke(
        color = Color(color.toULong()),
        strokeWidth = strokeWidth,
        points = points.map { Offset(it.x, it.y) }
    )
}

